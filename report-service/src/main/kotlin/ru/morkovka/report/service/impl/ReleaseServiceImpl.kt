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
    @Value("\${jira.search.default.task.out.paragraph}")
    val taskOutParagraph: String,

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
     *  @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     *  @param limit on the number of returned issues from Jira
     *  @return the map of <issue key, comments> with test cases and instructions for deploy
     */
    override fun getTasksTestingAndDeployInfoByJiraRelease(jiraFixVersion: String, limit: Int): MutableMap<String,
            MutableList<String>> {
        // Map to store special comments for test cases and deploy instructions for each task
        val commentsMap: MutableMap<String, MutableList<String>>
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)

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
     * @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     * @param limit on the number of returned issues from Jira
     * @return the custom class ReleaseNote, which fields contains all necessary information to form release report
     */

    override fun getReleaseNoteByJiraRelease(jiraFixVersion: String, limit: Int): ReleaseNote {
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)
        return getReleaseNoteFromTaskList(taskList)
    }

    /**
     * Search for every task suites for fix version by {@code TaskServiceImpl#getTasksByJiraRelease}.
     * Then all relevant tasks are transformed into custom ReleaseNote class.
     * And finally construct String contains readable release report with markdown tags.
     *
     * @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     * @param limit on the number of returned issues from Jira
     * @return the String contains
     */

    override fun getReleaseNoteToString(jiraFixVersion: String, limit: Int): String {
        val note = getReleaseNoteByJiraRelease(jiraFixVersion, limit)

        logger.info("releaseNoteToString [jiraFixVersion = $jiraFixVersion]: convertation started")

        var taskIn = ""
        if (note.taskInKey.isNotEmpty()) {
            taskIn = "{Jira:${note.taskInKey}}"
        }
        val s = "$taskOutParagraph\n" +
                "\n\n${taskInParagraph}\n$taskIn" +
                "\n\n${commentProperties.sourceCode.paragraph}\n${stringFromMapWithoutTaskKeys(
                    note.sourceCode, commentProperties.sourceCode.default
                )}" +
                "\n\n${commentProperties.artifact.paragraph}\n${stringFromMapWithoutTaskKeys(
                    note.artifact, commentProperties.artifact.default
                )}" +
                "\n\n${commentProperties.newFeature.paragraph}\n${stringFromFeatures(
                    note.features, note.jiraKeysMap, commentProperties.newFeature.default
                )}" +
                "\n\n${commentProperties.databaseChange.paragraph}\n${stringFromMapWithoutTaskKeys(
                    note.dbChanges, commentProperties.databaseChange.default
                )}" +
                "\n\n${commentProperties.monitoringChange.paragraph}\n${stringFromMapWithoutTaskKeys(
                    note.monitoringChanges, commentProperties.monitoringChange.default
                )}" +
                "\n\n${commentProperties.config.paragraph}\n${stringFromMapWithoutTaskKeys(
                    note.configs, commentProperties.config.default
                )}" +
                "\n\n${commentProperties.deployInstruction.paragraph}\n${stringFromMapWithoutTaskKeys(
                    note.deploy, commentProperties.deployInstruction.default
                )}" +
                "\n\n${commentProperties.testCase.paragraph}\n${stringFromMapWithTaskKeys(
                    note.testCase, true, commentProperties.testCase.default
                )}" +
                "\n\n${commentProperties.rollbackAction.paragraph}\n${stringFromMapWithoutTaskKeys(
                    note.rollback, commentProperties.rollbackAction.default
                )}"

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
        val jiraKeys: MutableMap<String, String> = mutableMapOf()
        val features: MutableMap<String, TaskFeature> = mutableMapOf()
        taskList
            .filter { !it.summary.startsWith((taskInStart)) }
            .forEach {
                // Fill collection for mapping jira tasks
                val regex = ("(\\[ссылка\\|https*://jcs\\.passport\\.local/(browse|projects/MDM/issues)/MDM-[0-9]+]" +
                        " на задачу в джире ДИТа)").toRegex(RegexOption.IGNORE_CASE)
                val taskOutKey = regex.find(it.description)
                if (taskOutKey != null) {
                    jiraKeys[it.key] = "MDM-[0-9]+".toRegex(RegexOption.IGNORE_CASE).find(taskOutKey.value)?.value ?: ""
                }

                // Fill release features
                features[it.key] = TaskFeature(
                    key = it.key,
                    link = it.link,
                    summary = it.summary
                )
            }

        val note = ReleaseNote(
            taskInKey = taskIn?.key ?: "",
            jiraKeysMap = jiraKeys,
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

    private fun stringFromFeatures(map: MutableMap<String, TaskFeature>, keyMap: MutableMap<String, String>, defaultValue: String): String {
        if (map.isEmpty()) {
            return defaultValue
        }

        var s = "||Задача в jira||Задача в jira ТаскДата||Наименование задачи в jira ТаскДата||";
        map.forEach { (key, task) ->
            var taskOutKey = keyMap.getOrDefault(key, " ")
            if (taskOutKey != " ") {
                taskOutKey = "{Jira:$taskOutKey}"
            }
            s += "\n|$taskOutKey|{Jira:$key}|${task.summary}|"
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