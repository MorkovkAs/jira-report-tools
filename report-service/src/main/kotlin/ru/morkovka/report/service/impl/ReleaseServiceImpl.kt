package ru.morkovka.report.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.ReleaseService
import ru.morkovka.report.utils.ReleaseUtils
import ru.morkovka.report.utils.TaskUtils.Companion.sortByJiraKey
import java.util.*
import java.util.stream.Collectors

@Service
class ReleaseServiceImpl(
    @Value("\${jira.search.default.comment.test.case.start}")
    private var taskCommentTestCaseStart: String,

    @Value("\${jira.search.default.comment.deploy.instruction.start}")
    private var taskCommentDeployInstructionsStart: String
) : ReleaseService {

    @Autowired
    private lateinit var taskServiceImpl: TaskServiceImpl

    @Autowired
    private lateinit var releaseUtils: ReleaseUtils

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
        var comments: MutableMap<String, MutableList<String>> = LinkedHashMap()
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)

        //TODO change here to String.startsWith when the team starts using Jira comments correctly to populate such the data
        comments = getCommentsFromTasksByKey(taskList)

        logger.info("getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraFixVersion]: jira search completed")

        return sortByJiraKey(comments)
    }

    private fun getCommentsFromTasksByKey(
        taskList: MutableList<Task>
    ): MutableMap<String, MutableList<String>> {
        val comments: MutableMap<String, MutableList<String>> = LinkedHashMap()
        taskList.stream().forEach { task ->
            task.comments = task.comments.stream()
                .filter { comment ->
                    comment.contains(taskCommentTestCaseStart)
                            || comment.startsWith(taskCommentDeployInstructionsStart)
                }.collect(Collectors.toList())
            comments[task.key] = task.comments
        }
        return comments
    }
}