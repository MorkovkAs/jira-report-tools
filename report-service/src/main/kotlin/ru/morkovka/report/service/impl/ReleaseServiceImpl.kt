package ru.morkovka.report.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.morkovka.report.CommentProperties
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.ReleaseService
import ru.morkovka.report.utils.TaskUtils.Companion.sortByJiraKey
import java.util.*

@Service
class ReleaseServiceImpl : ReleaseService {

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
            getCommentsFromTaskListByKeyword(taskList, commentProperties.testCaseStart),
            getCommentsFromTaskListByKeyword(taskList, commentProperties.deployInstructionStart)
        )

        logger.info("getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraFixVersion]: jira search completed")

        return sortByJiraKey(commentsMap)
    }

    override fun getReleaseNoteByJiraRelease(jiraFixVersion: String, limit: Int): ReleaseNote {
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)
        return getReleaseNoteFromTaskList(taskList)
    }

    override fun releaseNoteToString(jiraFixVersion: String, limit: Int): String {
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)
        val note = getReleaseNoteFromTaskList(taskList)
        val sb = StringBuilder()
        sb.append(
            commentProperties.taskOutParagraph + "\n"
                    + "\n\n" + commentProperties.taskInParagraph + "\n" + note.taskIn
                    + "\n\n" + commentProperties.sourceCodeParagraph + "\n" + note.sourceCode
                    + "\n\n" + commentProperties.artifactParagraph + "\n" + note.artifact
                    + "\n\n" + commentProperties.newFeatureParagraph + mutableListToString(note.features)
                    + "\n\n" + commentProperties.databaseChangeParagraph + mutableListToString(note.dbChanges)
                    + "\n\n" + commentProperties.monitoringChangeParagraph + mutableListToString(note.monitoringChanges)
                    + "\n\n" + commentProperties.configParagraph + mutableListToString(note.configs)
                    + "\n\n" + commentProperties.deployInstructionParagraph + mutableListToString(note.deploy)
                    + "\n\n" + commentProperties.testCaseParagraph + mutableListToString(note.testCase)
                    + "\n\n" + commentProperties.rollbackActionParagraph + mutableListToString(note.rollback)
        )
        return sb.toString()
    }

    private fun getReleaseNoteFromTaskList(taskList: MutableList<Task>): ReleaseNote {
        val note = ReleaseNote()

        for (task in taskList) {
            if (task.summary.startsWith(commentProperties.taskInStart)) {
                note.taskIn = task.key + "; " + task.summary + "; " + task.link
            }
            note.sourceCode = mutableListToString(getCommentsFromTaskByKeyword(task, commentProperties.sourceCodeStart))
            if (note.sourceCode.isEmpty()) {
                note.sourceCode = commentProperties.sourceCodeDefault
            }
            note.artifact = mutableListToString(getCommentsFromTaskByKeyword(task, commentProperties.artifactStart))
            if (note.artifact.isEmpty()) {
                note.artifact = commentProperties.artifactDefault
            }
            note.features?.add(task.key + "; " + task.summary + "; " + task.link)
            if (note.features?.isEmpty()!!) {
                note.features!!.add(commentProperties.newFeatureDefault)
            }
            note.dbChanges?.addAll(getCommentsFromTaskByKeyword(task, commentProperties.databaseChangeStart))
            if (note.dbChanges?.isEmpty()!!) {
                note.dbChanges!!.add(commentProperties.databaseChangeDefault)
            }
            note.monitoringChanges?.addAll(getCommentsFromTaskByKeyword(task, commentProperties.monitoringChangeStart))
            if (note.monitoringChanges?.isEmpty()!!) {
                note.monitoringChanges!!.add(commentProperties.monitoringChangeDefault)
            }
            note.configs?.addAll(getCommentsFromTaskByKeyword(task, commentProperties.configStart))
            if (note.configs?.isEmpty()!!) {
                note.configs!!.add(commentProperties.configDefault)
            }
            note.deploy?.addAll(getCommentsFromTaskByKeyword(task, commentProperties.deployInstructionStart))
            if (note.deploy?.isEmpty()!!) {
                note.deploy!!.add(commentProperties.deployInstructionDefault)
            }
            note.testCase?.addAll(getCommentsFromTaskByKeyword(task, commentProperties.testCaseStart))
            if (note.testCase?.isEmpty()!!) {
                note.testCase!!.add(commentProperties.testCaseDefault)
            }
            note.rollback?.addAll(getCommentsFromTaskByKeyword(task, commentProperties.rollbackActionStart))
            if (note.rollback?.isEmpty()!!) {
                note.rollback!!.add(commentProperties.rollbackActionDefault)
            }
        }
        if (note.taskIn?.isEmpty()!!) {
            note.taskIn = commentProperties.taskInParagraph
        }
        return note
    }

    private fun mutableListToString(list: MutableList<String>?): String {
        val sb = StringBuilder()
        if (list != null) {
            for (string in list) {
                sb.append("\n").append(string)
            }
        }
        return sb.toString()
    }

    companion object {
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
            task.comments.filter { it.startsWith(keyword) }.map { it.replace(keyword, "") }.toMutableList()

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