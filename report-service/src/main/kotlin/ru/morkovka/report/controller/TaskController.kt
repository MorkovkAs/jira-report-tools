package ru.morkovka.report.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.TaskService

@RestController
@RequestMapping("/task")
@CrossOrigin
class TaskController(
    @Autowired
    val taskService: TaskService
) {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/byJql")
    fun getTasksByJqlString(
        @RequestParam(value = "jql", required = true) jql: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): MutableList<Task> {
        logger.info("Got request getTasksByJqlString [jql = $jql; limit = $limit]")
        return taskService.getTasksByJqlString(jql, limit)
    }

    @GetMapping("/byKey")
    fun getTaskByKey(@RequestParam(value = "jiraKey", required = true) jiraKey: String): Task {
        logger.info("Got request getTaskByKey [jiraKey = $jiraKey]")
        return taskService.getTaskByJiraKey(jiraKey)
    }

    @GetMapping("/byRelease")
    fun getTaskListByRelease(
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): MutableList<Task> {
        logger.info("Got request getTaskListByRelease [jiraFixVersion = $jiraRelease; limit = $limit]")
        return taskService.getTasksByJiraRelease(jiraRelease, limit)
    }
}