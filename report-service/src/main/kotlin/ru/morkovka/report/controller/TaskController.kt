package ru.morkovka.report.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.TaskService

@RestController
@RequestMapping("/api/task")
@CrossOrigin
class TaskController(
    @Autowired
    val taskService: TaskService
) {

    @GetMapping("/byJql")
    fun getTasksByJqlString(
        @RequestParam(value = "jql", required = true) jql: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): MutableList<Task> = taskService.getTasksByJqlString(jql, limit)

    @GetMapping("/byKey")
    fun getTaskByKey(
        @RequestParam(value = "jiraKey", required = true) jiraKey: String
    ): Task = taskService.getTaskByJiraKey(jiraKey)

    @GetMapping("/byRelease")
    fun getTaskListByRelease(
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): MutableList<Task> = taskService.getTasksByJiraRelease(jiraRelease, limit)
}