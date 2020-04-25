package ru.morkovka.report.service.impl

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import ru.morkovka.report.entity.Task
import ru.morkovka.report.entity.dto.SearchResultDto
import ru.morkovka.report.entity.dto.TaskDto
import ru.morkovka.report.entity.mapper.TaskMapper.Companion.getTask
import ru.morkovka.report.service.TaskService
import java.util.*
import java.util.stream.Collectors

@Service
class TaskServiceImpl(
    @Value("\${jira.url}")
    private val jiraUrl: String,

    @Value("\${jira.auth.basic}")
    private val jiraAuthBasic: String,

    @Value("\${jira.comment.test.case.start}")
    private var taskCommentTestCaseStart: String,

    @Value("\${jira.comment.deploy.instruction.start}")
    private var taskCommentDeployInstructionsStart: String
) : TaskService {
    val logger: Logger = LoggerFactory.getLogger(javaClass)
    val jiraUrlRest: String = "$jiraUrl/rest/api/latest"
    val restTemplate: RestTemplate = RestTemplateBuilder().errorHandler(DefaultResponseErrorHandler()).build()
    private val headers = HttpHeaders()

    init {
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE)
        //TODO check your auth data in application.yml file. It should be the result of ("Basic " + base64(login + ":" + password))
        headers.set("Authorization", jiraAuthBasic)
    }

    /**
     *  Search and minimize a representation of the issue for the given issue key
     *
     *  @param jiraKey the code of the issue to search by. For example "DM-915"
     *  @return the issue with summary, status, description, fixVersions, comments
     */
    override fun getTaskByJiraKey(jiraKey: String): Task {

        val builder = UriComponentsBuilder.fromHttpUrl("$jiraUrlRest/issue/$jiraKey")
            .queryParam("fields", "summary")
            .queryParam("fields", "status")
            .queryParam("fields", "description")
            .queryParam("fields", "fixVersions")
            .queryParam("fields", "comment")

        val entity = HttpEntity<Any>(headers)

        val response: ResponseEntity<String> = restTemplate.exchange(
            builder.toUriString(),
            HttpMethod.GET,
            entity,
            String::class.java
        )

        logger.info("getTaskByJiraKey [jiraKey = $jiraKey]: jira search completed")
        val task = getTask(Gson().fromJson(response.body, TaskDto::class.java))
        logger.info("getTaskByJiraKey [jiraKey = $jiraKey]: casting to Task completed")
        return task
    }

    /**
     *  Search and minimize representations of all the issues for the given fix version
     *
     *  @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     *  @return the list of issues with summary, status, description, fixVersions, comments
     */
    override fun getTasksByJiraRelease(jiraFixVersion: String): MutableList<Task> {

        val builder = UriComponentsBuilder.fromHttpUrl("$jiraUrlRest/search")

        val requestJson = "{\n" +
                "    \"jql\": \"project=DM AND fixVersion=$jiraFixVersion\",\n" +
                "    \"startAt\": 0,\n" +
                "    \"maxResults\": 15,\n" +
                "    \"fields\": [\n" +
                "        \"summary\",\n" +
                "        \"status\",\n" +
                "        \"description\",\n" +
                "        \"fixVersions\",\n" +
                "        \"comment\"\n" +
                "    ]\n" +
                "}"

        val entity = HttpEntity<Any>(requestJson, headers)

        val response: ResponseEntity<String> = restTemplate.exchange(
            builder.toUriString(),
            HttpMethod.POST,
            entity,
            String::class.java
        )

        logger.info("getTasksByJiraRelease [jiraFixVersion = $jiraFixVersion]: jira search completed")
        val searchResultDto = Gson().fromJson(response.body, SearchResultDto::class.java)
        val taskDtoList = searchResultDto.issues
        val taskList = taskDtoList.stream().filter(Objects::nonNull).map { getTask(it) }.collect(Collectors.toList())
        logger.info("getTasksByJiraRelease [jiraFixVersion = $jiraFixVersion]: casting to List<Task> completed")
        return taskList
    }

    /**
     *  Search of all the issues for the given fix version by {@code TaskServiceImpl#getTasksByJiraRelease}.
     *  Than filter comments in issues by special strings so all issues contain comments only of
     *  test cases or deploy instructions.
     *  Limitations: All comments have to be checked by {@code String.startsWith}, now it is not always true
     *
     *  @param jiraFixVersion the code of the jira release to search by. For example "1.37.0"
     *  @return the map of <issue key, comments> with test cases or instructions for deploy
     */
    override fun getTasksTestingAndDeployInfoByJiraRelease(jiraFixVersion: String): MutableMap<String, MutableList<String>> {
        // Map to store special comments for test cases and deploy for each task
        val comments: MutableMap<String, MutableList<String>> = hashMapOf()
        val taskList = getTasksByJiraRelease(jiraFixVersion)

        //TODO change here to String.startsWith
        taskList.stream().forEach { task ->
            task.comments = task.comments.stream()
                .filter { comment ->
                    comment.contains(taskCommentTestCaseStart)
                            || comment.startsWith(taskCommentDeployInstructionsStart)
                }.collect(Collectors.toList())
            comments[task.key] = task.comments
        }

        return comments.toSortedMap()
    }
}