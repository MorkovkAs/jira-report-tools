package ru.morkovka.report.service.impl

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import ru.morkovka.report.entity.Task
import ru.morkovka.report.entity.dto.SearchResultDto
import ru.morkovka.report.entity.mapper.TaskMapper.Companion.getTask
import ru.morkovka.report.service.TaskService
import java.util.*
import java.util.stream.Collectors

@Service
class TaskServiceImpl(
    @Value("\${jira.url}")
    private val jiraUrl: String,

    @Value("\${jira.search.default.project}")
    private val jiraProject: String
) : TaskService {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    val logger: Logger = LoggerFactory.getLogger(javaClass)
    val jiraUrlRest: String = "$jiraUrl/rest/api/latest"

    /**
     *  Search issues by the given jql (Jira query language) search
     *
     *  @param jqlString the string in jql (Jira query language) to search by. For example "project = DM AND fixVersion = 1.31.1"
     *  @param limit on the number of returned issues from Jira
     *  @return the list of issues with summary, status, description, fixVersions, comments
     */
    override fun getTasksByJqlString(jqlString: String, limit: Int): MutableList<Task> {
        val builder = UriComponentsBuilder.fromHttpUrl("$jiraUrlRest/search")
        val requestJson = getRequestJsonForJqlQuery(jqlString, limit)
        val entity = HttpEntity<Any>(requestJson)

        val response: ResponseEntity<String> = restTemplate.exchange(
            builder.toUriString(),
            HttpMethod.POST,
            entity,
            String::class.java
        )

        logger.info("getTasksByJqlString [jqlString = $jqlString]: jira search completed")
        val searchResultDto = Gson().fromJson(response.body, SearchResultDto::class.java)
        val taskDtoList = searchResultDto.issues
        val taskList = taskDtoList.stream().filter(Objects::nonNull).map { getTask(it, jiraUrl) }.collect(Collectors.toList())
        logger.info("getTasksByJqlString [jqlString = $jqlString]: casting to List<Task> completed")

        return taskList
    }

    /**
     *  Search and minimize a representation of the issue for the given issue key
     *
     *  @param jiraKey the code of the issue to search by. For example "DM-915"
     *  @return the issue with summary, status, description, fixVersions, comments
     */
    override fun getTaskByJiraKey(jiraKey: String): Task {
        val taskList = getTasksByJqlString("issueKey = $jiraKey", 1)

        // We do know that there is only 1 element in taskList.
        // If not, a HttpClientErrorException has occurred because of 404 error from Jira rest
        val task = taskList[0]
        logger.info("getTaskByJiraKey [jiraKey = $jiraKey]: jira search completed")

        return task
    }

    /**
     *  Search and minimize representations of all the issues for the given fix version.
     *  It creates jql string and search by {@code TaskServiceImpl#getTasksByJqlString}
     *
     *  @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     *  @param limit on the number of returned issues from Jira
     *  @return the list of issues with summary, status, description, fixVersions, comments
     */
    override fun getTasksByJiraRelease(jiraFixVersion: String, limit: Int): MutableList<Task> {
        val taskList = getTasksByJqlString("project = $jiraProject AND fixVersion = $jiraFixVersion", limit)
        logger.info("getTasksByJiraRelease [jiraFixVersion = $jiraFixVersion]: jira search completed")

        return taskList
    }

    /**
     * Generates a JQL search string. customfield_13700 is our custom field for external tasks.
     */
    private fun getRequestJsonForJqlQuery(jqlString: String, maxResults: Int) = """{
    "jql": "$jqlString ORDER BY key ASC",
    "startAt": 0,
    "maxResults": $maxResults,
    "fields": [
        "summary",
        "status",
        "description",
        "fixVersions",
        "comment",
        "customfield_13700"
    ]
}"""

}