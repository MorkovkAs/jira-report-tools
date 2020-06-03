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

    @Value("\${jira.search.default.comment.release.distribution.start}")
    private var commentReleaseDistributionStart: String,

    @Value("\${jira.search.default.comment.release.distribution.paragraph}")
    private var commentReleaseDistributionParagraph: String,

    @Value("\${jira.search.default.comment.release.distribution.default}")
    private var commentReleaseDistributionDefault: String,

    @Value("\${jira.search.default.comment.new.functions.start}")
    private var commentNewFunctionsStart: String,

    @Value("\${jira.search.default.comment.new.functions.paragraph}")
    private var commentNewFunctionsParagraph: String,

    @Value("\${jira.search.default.comment.new.functions.default}")
    private var commentNewFunctionsDefault: String,

    @Value("\${jira.search.default.comment.database.changes.start}")
    private var commentDatabaseChangesStart: String,

    @Value("\${jira.search.default.comment.database.changes.paragraph}")
    private var commentDatabaseChangesParagraph: String,

    @Value("\${jira.search.default.comment.database.changes.default}")
    private var commentDatabaseChangesDefault: String,

    @Value("\${jira.search.default.comment.configs.start}")
    private var commentConfigsStart: String,

    @Value("\${jira.search.default.comment.configs.paragraph}")
    private var commentConfigsParagraph: String,

    @Value("\${jira.search.default.comment.configs.default}")
    private var commentConfigsDefault: String,

    @Value("\${jira.search.default.comment.settings.changes.start}")
    private var commentSettingsChangesStart: String,

    @Value("\${jira.search.default.comment.settings.changes.paragraph}")
    private var commentSettingsChangesParagraph: String,

    @Value("\${jira.search.default.comment.settings.changes.default}")
    private var commentSettingsChangesDefault: String,

    @Value("\${jira.search.default.comment.testing.plan.start}")
    private var commentTestingPlanStart: String,

    @Value("\${jira.search.default.comment.testing.plan.paragraph}")
    private var commentTestingPlanParagraph: String,

    @Value("\${jira.search.default.comment.testing.plan.default}")
    private var commentTestingPlanDefault: String,

    @Value("\${jira.search.default.comment.rollback.plan.start}")
    private var commentRollbackPlanStart: String,

    @Value("\${jira.search.default.comment.rollback.plan.paragraph}")
    private var commentRollbackPlanParagraph: String,

    @Value("\${jira.search.default.comment.rollback.plan.default}")
    private var commentRollbackPlanDefault: String
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
        return constructReleaseNote(taskList)
    }

    override fun constructReleaseNote(taskList: MutableList<Task>): ReleaseNote{
        val note = ReleaseNote()
        for (task in taskList) { //(id, key, summary, status, _, _, comments)
            note.changes?.add(task.key + "; " + task.status + "; " + task.summary)
            if(note.changes?.isEmpty()!!) { note.changes!!.add(commentNewFunctionsDefault) }
            if (task.summary.startsWith("Релиз КСРД")) {
                note.distributions = task.key + "; " + task.summary + "; " + task.link
            }
            note.dbChanges?.addAll(getCommentsFromTaskByKeyword(task, commentDatabaseChangesStart))
            if (note.dbChanges?.isEmpty()!!) { note.dbChanges!!.add(commentDatabaseChangesDefault) }
            note.configs?.addAll(getCommentsFromTaskByKeyword(task, commentConfigsStart))
            if (note.configs?.isEmpty()!!) { note.configs!!.add(commentConfigsDefault) }
            note.installation?.addAll(getCommentsFromTaskByKeyword(task, commentSettingsChangesStart))
            if (note.installation?.isEmpty()!!) { note.installation!!.add(commentSettingsChangesDefault) }
            note.testing?.addAll(getCommentsFromTaskByKeyword(task, commentTestingPlanStart))
            if (note.testing?.isEmpty()!!) { note.testing!!.add(commentTestingPlanDefault) }
            note.rollback?.addAll(getCommentsFromTaskByKeyword(task, commentRollbackPlanStart))
            if (note.rollback?.isEmpty()!!) { note.rollback!!.add(commentRollbackPlanDefault) }
        }
        if (note.distributions?.isEmpty()!!) {note.distributions = commentReleaseDistributionDefault}
        return note
    }

    override fun releaseNoteToString(jiraFixVersion: String, limit: Int): String {
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)
        val note = constructReleaseNote(taskList)
        val sb = StringBuilder()
        sb.append(commentReleaseDistributionParagraph + "\n" + note.distributions
                + "\n\n" + commentNewFunctionsParagraph + mutableListToString(note.changes)
                + "\n\n" + commentDatabaseChangesParagraph + mutableListToString(note.dbChanges)
                + "\n\n" + commentConfigsParagraph + mutableListToString(note.configs)
                + "\n\n" + commentSettingsChangesParagraph + mutableListToString(note.installation)
                + "\n\n" + commentTestingPlanParagraph + mutableListToString(note.testing)
                + "\n\n" + commentRollbackPlanParagraph + mutableListToString(note.rollback))
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