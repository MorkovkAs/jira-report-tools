package ru.morkovka.report.service.impl

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.morkovka.report.service.ReleaseService
import java.util.*
import java.util.ArrayList
import java.util.regex.Pattern
import java.util.stream.Collectors

@Service
class ReleaseServiceImpl(
    @Value("\${jira.search.default.comment.test.case.start}")
    private var taskCommentTestCaseStart: String,

    @Value("\${jira.search.default.comment.deploy.instruction.start}")
    private var taskCommentDeployInstructionsStart: String
) : ReleaseService {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    val taskServiceImpl: TaskServiceImpl
        get() {
            TODO()
        }

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
        val comments: MutableMap<String, MutableList<String>> = LinkedHashMap()
        val taskList = taskServiceImpl.getTasksByJiraRelease(jiraFixVersion, limit)

        //TODO change here to String.startsWith when the team starts using Jira comments correctly to populate such the data
        taskList.stream().forEach { task ->
            task.comments = task.comments.stream()
                .filter { comment ->
                    comment.contains(taskCommentTestCaseStart)
                            || comment.startsWith(taskCommentDeployInstructionsStart)
                }.collect(Collectors.toList())
            comments[task.key] = task.comments
        }

        logger.info("getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraFixVersion]: jira search completed")

        // TODO change default comparator to custom one. "DM-1" < "DM-2" < ... < "DM-10" < "DM-11" < ...
        customSort(comments) //Custom comparator added
        return comments
    }

    private fun customSort(map: MutableMap<String, MutableList<String>>) {
        val comparator =
            Comparator { o1: Map.Entry<String, List<String>>, o2: Map.Entry<String, List<String>> ->
                val one = getNumber(o1.key)
                val two = getNumber(o2.key)
                one.compareTo(two)
            }
        val entries: List<Map.Entry<String, MutableList<String>>> =
            ArrayList<Map.Entry<String, MutableList<String>>>(map.entries)
        entries.sortedWith(comparator)
        map.clear()
        for ((key, value) in entries) {
            map[key] = value
        }
    }

    private fun getNumber(str: String): Int {
        val pattern =
            Pattern.compile("(.*)-(\\d+)", Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(str)
        matcher.find()
        return matcher.group(2).toInt()
    }
}