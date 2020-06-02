package ru.morkovka.report.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.ReleaseService
import ru.morkovka.report.utils.ReleaseUtils
import ru.morkovka.report.utils.TaskUtils.Companion.sortByJiraKey
import java.util.*

@Service
class ReleaseServiceImpl(
    @Value("\${jira.search.default.comment.test.case.start}")
    private var taskCommentTestCaseStart: String,

    @Value("\${jira.search.default.comment.deploy.instruction.start}")
    private var taskCommentDeployInstructionsStart: String,

    @Value("\${constant.dbChanges}")
    private var dbChanges: String,

    @Value("\${constant.dbChangesEmpty}")
    private var dbChangesEmpty: String,

    @Value("\${constant.configs}")
    private var configs: String,

    @Value("\${constant.configsEmpty}")
    private var configsEmpty: String,

    @Value("\${constant.installation}")
    private var installation: String,

    @Value("\${constant.installationEmpty}")
    private var installationEmpty: String,

    @Value("\${constant.testing}")
    private var testing: String,

    @Value("\${constant.testingEmpty}")
    private var testingEmpty: String,

    @Value("\${constant.rollback}")
    private var rollback: String,

    @Value("\${constant.rollbackEmpty}")
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

        return ReleaseUtils.taskToRelease(taskList, dbChanges, dbChangesEmpty,
            configs, configsEmpty, installation, installationEmpty,
            testing, testingEmpty, rollback, rollbackEmpty)
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