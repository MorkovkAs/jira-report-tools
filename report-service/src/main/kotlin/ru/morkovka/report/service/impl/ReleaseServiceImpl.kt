package ru.morkovka.report.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.morkovka.report.CommentProperties
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import ru.morkovka.report.entity.TaskFeature
import ru.morkovka.report.service.ReleaseService
import ru.morkovka.report.utils.TaskUtils.Companion.sortByJiraKey
import java.util.*
import java.util.stream.Collectors

@Service
class ReleaseServiceImpl(
    @Value("\${jira.search.default.task.paragraph}")
    val tasksParagraph: String,

    @Value("\${jira.search.default.task.in.start}")
    val taskInStart: String,

    @Value("\${jira.search.default.task.in.paragraph}")
    val taskInParagraph: String
) : ReleaseService {

    @Autowired
    private lateinit var commentProperties: CommentProperties

    @Autowired
    private lateinit var taskServiceImpl: TaskServiceImpl

    private val default = "DEFAULT"

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    /**
     *  Search of all the issues for the given fix version by {@code TaskServiceImpl#getTasksByJiraRelease}.
     *  Than filter comments in issues by special strings so all issues contain comments only of
     *  test cases or deploy instructions.
     *  Limitations: All comments have to be checked by {@code String.startsWith}, now it is not always true
     *
     *  @param jiraProject the code of the jira project to search by. For example "DM"
     *  @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     *  @param limit on the number of returned issues from Jira
     *  @return the map of <issue key, comments> with test cases and instructions for deploy
     */
    override fun getTasksTestingAndDeployInfoByJiraRelease(jiraProject: String, jiraFixVersion: String, limit: Int): MutableMap<String,
            MutableList<String>> {
        // Map to store special comments for test cases and deploy instructions for each task
        val commentsMap: MutableMap<String, MutableList<String>>
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraProject, jiraFixVersion, limit)

        commentsMap = mergeMaps(
            getCommentsFromTaskListByKeyword(taskList, commentProperties.testCase.start),
            getCommentsFromTaskListByKeyword(taskList, commentProperties.deployInstruction.start)
        )

        logger.info("getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraFixVersion]: jira search completed")

        return sortByJiraKey(commentsMap)
    }

    /**
     * Search for every task suites for fix version by {@code TaskServiceImpl#getTasksByJiraRelease}.
     * Then all relevant tasks are transformed into custom ReleaseNote class.
     *
     * @param jiraProject the code of the jira project to search by. For example "DM"
     * @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     * @param limit on the number of returned issues from Jira
     * @return the custom class ReleaseNote, which fields contains all necessary information to form release report
     */

    override fun getReleaseNoteByJiraRelease(jiraProject: String, jiraFixVersion: String, limit: Int): ReleaseNote {
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraProject, jiraFixVersion, limit)
        return getReleaseNoteFromTaskList(taskList)
    }

    /**
     * Search for every task suites for fix version by {@code TaskServiceImpl#getTasksByJiraRelease}.
     * Then all relevant tasks are transformed into custom ReleaseNote class.
     * And finally construct String contains readable release report with markdown tags.
     *
     * @param jiraProject the code of the jira project to search by. For example "DM"
     * @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     * @param limit on the number of returned issues from Jira
     * @return the String contains
     */

    override fun getReleaseNoteToString(jiraProject: String, jiraFixVersion: String, limit: Int): String {
        val note = getReleaseNoteByJiraRelease(jiraProject, jiraFixVersion, limit)

        logger.info("releaseNoteToString [jiraFixVersion = $jiraFixVersion]: convertation started")

        var taskIn = ""
        if (note.taskInKey.isNotEmpty()) {
            taskIn = "{Jira:${note.taskInKey}}"
        }
        val s = """
{toc:printable=true|maxLevel=7|minLevel=1|type=list|separator=brackets}
            
$tasksParagraph      
$taskInParagraph
* $taskIn
* 

${commentProperties.sourceCode.paragraph}
${stringFromMapWithoutTaskKeys(
    note.sourceCode, commentProperties.sourceCode.default
)}
                
${commentProperties.artifact.paragraph}
${stringFromMapWithoutTaskKeys(
    note.artifact, commentProperties.artifact.default
)}
                
${commentProperties.newFeature.paragraph}
${stringFromFeatures(
    note.features, commentProperties.newFeature.default
)}
                
${commentProperties.databaseChange.paragraph}
${stringFromMapWithoutTaskKeys(
    note.dbChanges, commentProperties.databaseChange.default
)}
                
${commentProperties.monitoringChange.paragraph}
${stringFromMapWithoutTaskKeys(
    note.monitoringChanges, commentProperties.monitoringChange.default
)}
                
${commentProperties.config.paragraph}
${stringFromMapWithoutTaskKeys(
    note.configs, commentProperties.config.default
)}
                
${commentProperties.deployInstruction.paragraph}
${stringFromMapWithoutTaskKeys(
    note.deploy, commentProperties.deployInstruction.default
)}
                
${commentProperties.testCase.paragraph}
${stringFromMapWithTaskKeys(
    note.testCase, true, commentProperties.testCase.default
)}
                
${commentProperties.rollbackAction.paragraph}
${stringFromMapWithoutTaskKeys(
    note.rollback, commentProperties.rollbackAction.default
)}
"""

        logger.info("releaseNoteToString [jiraFixVersion = $jiraFixVersion]: ReleaseNote to String convertation completed")

        return s
    }

    /**
     * Go over Task list and select suitable info for ReleaseNote class.
     * Then fill ReleaseNote fields with selected info.
     *
     * @param taskList array with custom class Task contains info from Jira tasks.
     * @return custom ReleaseNote class contains fields with information for release report.
     */

    private fun getReleaseNoteFromTaskList(taskList: MutableList<Task>): ReleaseNote {
        val taskIn = taskList.filter { it.summary.startsWith(taskInStart) }.getOrNull(0)
        val features: MutableMap<String, TaskFeature> = mutableMapOf()
        taskList
            .filter { !it.summary.startsWith((taskInStart)) }
            .forEach {
                // Current track from custom jira attribute
                var externalJiraKey = "N?MDM-[0-9]+".toRegex(RegexOption.IGNORE_CASE).find(it.externalJiraLink)?.value ?: ""

                // Old track for old tasks. Getting links from description
                if (externalJiraKey.isBlank()) {
                    val regex =
                        ("(\\[ссылка\\|https*://(jcs|data)\\.passport\\.local(:8080)?/(browse|projects/MDM/issues)/N?MDM-[0-9]+]" +
                                " на задачу в джире ДИТа)").toRegex(RegexOption.IGNORE_CASE)
                    val taskOutKey = regex.find(it.description)
                    if (taskOutKey != null) {
                        externalJiraKey = "N?MDM-[0-9]+".toRegex(RegexOption.IGNORE_CASE).find(taskOutKey.value)?.value ?: ""
                    }
                }

                // Fill release features
                features[it.key] = TaskFeature(
                    key = it.key,
                    link = it.link,
                    summary = it.summary,
                    externalJiraKey = externalJiraKey
                )
            }

        val note = ReleaseNote(
            taskInKey = taskIn?.key ?: "",
            sourceCode = getCommentsFromTaskListByKeyword(taskList, commentProperties.sourceCode.start),
            artifact = getCommentsFromTaskListByKeyword(taskList, commentProperties.artifact.start),
            features = features,
            dbChanges = getCommentsFromTaskListByKeyword(taskList, commentProperties.databaseChange.start),
            monitoringChanges = getCommentsFromTaskListByKeyword(taskList, commentProperties.monitoringChange.start),
            configs = getCommentsFromTaskListByKeyword(taskList, commentProperties.config.start),
            deploy = getCommentsFromTaskListByKeyword(taskList, commentProperties.deployInstruction.start),
            testCase = getCommentsFromTaskListByKeyword(taskList, commentProperties.testCase.start),
            rollback = getCommentsFromTaskListByKeyword(taskList, commentProperties.rollbackAction.start)
        )

        logger.info("getReleaseNoteFromTaskList [taskList = $taskList]: ReleaseNote creation completed")

        return note
    }

    /**
     * Go through map<String, MutableList<String>> and concatenates suitable info to solid String using task keys.
     *
     * @param map contains info from one field of ReleaseNote
     * @param collapse Boolean
     * @param defaultValue String with default value returning when map is empty.
     * @return String consists of info from map.
     */

    private fun stringFromMapWithTaskKeys(
        map: MutableMap<String, MutableList<String>>,
        collapse: Boolean = false,
        defaultValue: String
    ): String {
        if (map.isEmpty()) {
            return defaultValue
        }

        var s = "";
        map.forEach { (key, comments) ->
            if (collapse) s += "{expand:$key}\n"
            s += comments.joinToString("\n")
            if (collapse) s += "{expand}\n"
        }
        return s
    }

    /**
     * Go through map<String, MutableList<String>> and concatenates suitable info to solid String without using task keys.
     *
     * @param map contains info from one field of ReleaseNote
     * @param defaultValue String with default value returning when map is empty.
     * @return String consists of info from map.
     */

    private fun stringFromMapWithoutTaskKeys(map: MutableMap<String, MutableList<String>>, defaultValue: String): String {
        return if (map.isNotEmpty()) {
            map.values.stream().map { it.joinToString("\n") }.collect(Collectors.toList()).joinToString("\n")
        } else {
            defaultValue
        }
    }

    /**
     * Construct String of features from Map of features.
     *
     * @param map fields contains String and TaskFeature class.
     * @param keyMap contains map of keys.
     * @param defaultValue String with default value returning when map is empty.
     * @return String consisted of features' info.
     */

    private fun stringFromFeatures(map: MutableMap<String, TaskFeature>, defaultValue: String): String {
        if (map.isEmpty()) {
            return defaultValue
        }

        var s = "||Задача ДИТ||Задача ЮД||Наименование||"
        map.forEach { (key, task) ->
            var externalJiraKey = task.externalJiraKey
            if (externalJiraKey.trim().isNotEmpty()) {
                externalJiraKey = "{Jira:${externalJiraKey}}"
            } else {
                externalJiraKey = " "
            }
            s += "\n|$externalJiraKey|{Jira:$key}|${task.summary}|"
        }
        return s
    }

    companion object {

        /**
         * Getting comments that suits keyword search from list of tasks.
         *
         * @param taskList List of custom class Task instances
         * @param keyword String contains keyword by which comments are got
         * @return MutableMap<String, MutableList<String>> contains comments as String and comments as List
         */

        fun getCommentsFromTaskListByKeyword(
            taskList: MutableList<Task>,
            keyword: String
        ): MutableMap<String, MutableList<String>> {
            val comments: MutableMap<String, MutableList<String>> = LinkedHashMap()
            taskList.stream().forEach { task ->
                val taskComments = getCommentsFromTaskByKeyword(task, keyword)
                if (taskComments.isNotEmpty()) {
                    comments[task.key] = taskComments
                }
            }

            return comments
        }

        /**
         * Search through task.comments for suitable one which starts from specified keyword.
         * Then replace keyword with header for release report.
         *
         * @param task instance of custom Task class contains info from Jira task.
         * @return MutableList<String> contains comments which match fields for ReleaseNote
         */

        private fun getCommentsFromTaskByKeyword(task: Task, keyword: String) =
            task.comments.filter { it.startsWith(keyword) }.map {
                it.replaceFirst(keyword, "").replaceFirst("\r\n", "").replaceFirst("\n", "")
            }.toMutableList()

        /**
         * Merges two maps. If there are duplicated keys in maps, it merges the corresponded lists
         *
         * @param map1
         * @param map2
         * @return the map containing all data
         */
        fun mergeMaps(
            map1: MutableMap<String, MutableList<String>>,
            map2: MutableMap<String, MutableList<String>>
        ): MutableMap<String, MutableList<String>> {
            map2.forEach { (key, commentList) ->
                map1.merge(key, commentList) { list1, list2 ->
                    list1.addAll(list2)
                    list1
                }
            }
            return map1
        }
    }
}