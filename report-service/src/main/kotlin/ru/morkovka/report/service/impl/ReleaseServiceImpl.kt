package ru.morkovka.report.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.ReleaseService
import ru.morkovka.report.utils.TaskUtils.Companion.sortByJiraKey
import java.util.*

@Service
class ReleaseServiceImpl(
    @Value("\${jira.search.default.comment.test.case.start}")
    private var taskCommentTestCaseStart: String,

    @Value("\${jira.search.default.comment.deploy.instruction.start}")
    private var taskCommentDeployInstructionsStart: String,

    @Value("\${jira.search.default.comment.new.functions.start}")
    private var changes: String,

    @Value("\${jira.search.default.comment.new.functions.default}")
    private var changesEmpty: String,

    @Value("\${jira.search.default.comment.database.changes.start}")
    private var dbChanges: String,

    @Value("\${jira.search.default.comment.database.changes.default}")
    private var dbChangesEmpty: String,

    @Value("\${jira.search.default.comment.configs.start}")
    private var configs: String,

    @Value("\${jira.search.default.comment.configs.default}")
    private var configsEmpty: String,

    @Value("\${jira.search.default.comment.settings.changes.start}")
    private var installation: String,

    @Value("\${jira.search.default.comment.settings.changes.default}")
    private var installationEmpty: String,

    @Value("\${jira.search.default.comment.testing.plan.start}")
    private var testing: String,

    @Value("\${jira.search.default.comment.testing.plan.default}")
    private var testingEmpty: String,

    @Value("\${jira.search.default.comment.rollback.plan.start}")
    private var rollback: String,

    @Value("\${jira.search.default.comment.rollback.plan.default}")
    private var rollbackEmpty: String
) : ReleaseService {

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
            getCommentsFromTaskListByKeyword(taskList, taskCommentTestCaseStart),
            getCommentsFromTaskListByKeyword(taskList, taskCommentDeployInstructionsStart)
        )

        logger.info("getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraFixVersion]: jira search completed")

        return sortByJiraKey(commentsMap)
    }

    override fun getReleaseNoteByJiraRelease(jiraFixVersion: String, limit: Int): ReleaseNote {
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)

        val note = ReleaseNote()
        for (task in taskList) { //(id, key, summary, status, _, _, comments)
            note.changes?.add(task.key + "; " + task.status + "; " + task.id + "; " + task.summary)
            if(note.changes?.isEmpty()!!) { note.changes!!.add(changesEmpty) }
            if (task.summary.startsWith("Релиз КСРД")) {
                note.distributions = task.key + "; " + task.summary + "; " + task.link
            }
            note.dbChanges?.addAll(getCommentsFromTaskByKeyword(task, dbChanges))
            if (note.dbChanges?.isEmpty()!!) { note.dbChanges!!.add(dbChangesEmpty) }
            note.configs?.addAll(getCommentsFromTaskByKeyword(task, configs))
            if (note.configs?.isEmpty()!!) { note.configs!!.add(configsEmpty) }
            note.installation?.addAll(getCommentsFromTaskByKeyword(task, installation))
            if (note.installation?.isEmpty()!!) { note.installation!!.add(installationEmpty) }
            note.testing?.addAll(getCommentsFromTaskByKeyword(task, testing))
            if (note.testing?.isEmpty()!!) { note.testing!!.add(testingEmpty) }
            note.rollback?.addAll(getCommentsFromTaskByKeyword(task, rollback))
            if (note.rollback?.isEmpty()!!) { note.rollback!!.add(rollbackEmpty) }
        }
        return note
    }

    override fun releaseNoteToString(note: ReleaseNote): String {
        val sb = StringBuilder()
        sb.append(note.distributions
                + "\n" + dbChanges + mutableListToString(note.changes)
                + "\n" + dbChanges + mutableListToString(note.dbChanges)
                + "\n" + configs + mutableListToString(note.configs)
                + "\n" + installation + mutableListToString(note.installation)
                + "\n" + testing + mutableListToString(note.testing)
                + "\n" + rollback + mutableListToString(note.rollback))
        return sb.toString()
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