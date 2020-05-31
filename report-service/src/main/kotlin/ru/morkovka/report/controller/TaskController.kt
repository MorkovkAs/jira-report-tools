package ru.morkovka.report.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int,
        @RequestParam(value = "token") token: String?
    ): MutableList<Task> {
        logger.info("Got request getTasksByJqlString [jql = $jql; limit = $limit]")
        checkToken(token)
        return taskService.getTasksByJqlString(jql, limit)
    }

    @GetMapping("/byKey")
    fun getTaskByKey(
        @RequestParam(value = "jiraKey", required = true) jiraKey: String,
        @RequestParam(value = "token") token: String?
    ): Task {
        logger.info("Got request getTaskByKey [jiraKey = $jiraKey]")
        checkToken(token)
        return taskService.getTaskByJiraKey(jiraKey)
    }

    @GetMapping("/byRelease")
    fun getTaskListByRelease(
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int,
        @RequestParam(value = "token") token: String?
    ): MutableList<Task> {
        logger.info("Got request getTaskListByRelease [jiraFixVersion = $jiraRelease; limit = $limit]")
        checkToken(token)
        return taskService.getTasksByJiraRelease(jiraRelease, limit)
    }

    private fun checkToken(token: String?) {
        val hashedValue = "\$2a\$10\$9eYB57vXgXENoyXfiPXTyebWCfquNBfOkXKMCrnnLIrflQY8R3Ca."
        if (token == null || !BCryptPasswordEncoder().matches(token, hashedValue)) {
            logger.warn("Unauthorized access attempt")
            throw IllegalAccessException("Incorrect token")
        }
    }
}