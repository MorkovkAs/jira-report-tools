package ru.morkovka.report.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.morkovka.report.CommentProperties
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.ReleaseService
import ru.morkovka.report.utils.TaskUtils.Companion.sortByJiraKey
import java.util.*

@Service
class ReleaseServiceImpl (
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

    override fun getReleaseNoteByJiraRelease(jiraFixVersion: String, limit: Int): ReleaseNote {
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)
        return getReleaseNoteFromTaskList(taskList)
    }

    override fun getReleaseNoteToString(jiraFixVersion: String, limit: Int): String {
        val note = getReleaseNoteByJiraRelease(jiraFixVersion, limit)
        val sb = StringBuilder()

        logger.info("releaseNoteToString [jiraFixVersion = $jiraFixVersion]: convertation started")

        sb.append(
            taskOutParagraph + "\n"
                    + "\n\n" + taskInParagraph + "\n" + note.taskIn
                    + "\n\n" + commentProperties.sourceCode.paragraph + enterCheck(note.sourceCode.first()) + note.sourceCode.joinToString(" ")
                    + "\n\n" + commentProperties.artifact.paragraph + enterCheck(note.artifact) + note.artifact
                    + "\n\n" + commentProperties.newFeature.paragraph + enterCheck(note.features.first()) + note.features.joinToString(" ")
                    + "\n\n" + commentProperties.databaseChange.paragraph + enterCheck(note.dbChanges.first()) + note.dbChanges.joinToString(" ")
                    + "\n\n" + commentProperties.monitoringChange.paragraph + enterCheck(note.monitoringChanges.first()) + note.monitoringChanges.joinToString(" ")
                    + "\n\n" + commentProperties.config.paragraph + enterCheck(note.configs.first()) + note.configs.joinToString(" ")
                    + "\n\n" + commentProperties.deployInstruction.paragraph + enterCheck(note.deploy.first()) + note.deploy.joinToString(" ")
                    + "\n\n" + commentProperties.testCase.paragraph + enterCheck(note.testCase.first()) + note.testCase.joinToString(" ")
                    + "\n\n" + commentProperties.rollbackAction.paragraph + enterCheck(note.rollback.first()) + note.rollback.joinToString(" ")
        )
        logger.info("releaseNoteToString [jiraFixVersion = $jiraFixVersion]: ReleaseNote to String convertation completed")

        return sb.toString()
    }

    private fun getReleaseNoteFromTaskList(taskList: MutableList<Task>): ReleaseNote {
        val note = ReleaseNote()

        logger.info("getReleaseNoteFromTaskList [taskList = $taskList]: ReleaseNote creation started")

        for (task in taskList) {
            if (task.summary.startsWith(taskInStart)) {
                note.taskIn = task.key + "; " + task.summary + "; " + task.link
            }
            note.sourceCode.addAll(getCommentsFromTaskByKeyword(task, commentProperties.sourceCode.start))
            note.artifact = getCommentsFromTaskByKeyword(task, commentProperties.artifact.start).joinToString(" ")
            if(!task.summary.contains(taskInStart)){
                note.features.add("\n" + task.key + "; " + task.summary + "; " + task.link)
            }
            note.dbChanges.addAll(getCommentsFromTaskByKeyword(task, commentProperties.databaseChange.start))
            note.monitoringChanges.addAll(getCommentsFromTaskByKeyword(task, commentProperties.monitoringChange.start))
            note.configs.addAll(getCommentsFromTaskByKeyword(task, commentProperties.config.start))
            note.deploy.addAll(getCommentsFromTaskByKeyword(task, commentProperties.deployInstruction.start))
            note.testCase.addAll(getCommentsFromTaskByKeyword(task, commentProperties.testCase.start))
            note.rollback.addAll(getCommentsFromTaskByKeyword(task, commentProperties.rollbackAction.start))
        }
        if (note.sourceCode.isEmpty()) {
            note.sourceCode.add(commentProperties.sourceCode.default)
        }
        if (note.artifact.isEmpty()) {
            note.artifact = commentProperties.artifact.default
        }
        if (note.features.isEmpty()) {
            note.features.add(commentProperties.newFeature.default)
        }
        if (note.dbChanges.isEmpty()) {
            note.dbChanges.add(commentProperties.databaseChange.default)
        }
        if (note.monitoringChanges.isEmpty()) {
            note.monitoringChanges.add(commentProperties.monitoringChange.default)
        }
        if (note.configs.isEmpty()) {
            note.configs.add(commentProperties.config.default)
        }
        if (note.deploy.isEmpty()) {
            note.deploy.add(commentProperties.deployInstruction.default)
        }
        if (note.testCase.isEmpty()) {
            note.testCase.add(commentProperties.testCase.default)
        }
        if (note.rollback.isEmpty()) {
            note.rollback.add(commentProperties.rollbackAction.default)
        }
        if (note.taskIn.isEmpty()) {
            note.taskIn = taskInParagraph
        }

        logger.info("getReleaseNoteFromTaskList [taskList = $taskList]: ReleaseNote creation completed")

        return note
    }

    companion object {
        fun enterCheck(comment: String): String {
            return if(comment.startsWith("\n")) {
                ""
            } else if (comment.startsWith("\n\n")) {
                ""
            } else {
                "\n"
            }
        }

        fun getCommentsFromTaskListByKeyword(
            taskList: MutableList<Task>,
            keyword: String
        ): MutableMap<String, MutableList<String>> {
            val comments: MutableMap<String, MutableList<String>> = LinkedHashMap()
            taskList.stream().forEach { task ->
                comments[task.key] = getCommentsFromTaskByKeyword(task, keyword)
            }

            return comments
        }

        fun getCommentsFromTaskByKeyword(task: Task, keyword: String) =
            task.comments.filter { it.startsWith(keyword) }.map { it.replaceFirst(keyword, "") }.toMutableList()

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