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

    @GetMapping("/byKey")
    fun getTaskByKey(@RequestParam(value = "jiraKey", defaultValue = "DM-891") jiraKey: String): Task {
        logger.info("Got request getTaskByKey [jiraKey = $jiraKey]")
        return taskService.getTaskByJiraKey(jiraKey)
    }

    @GetMapping("/byRelease")
    fun getTaskListByRelease(@RequestParam(value = "jiraRelease", defaultValue = "1.31.1") jiraRelease: String): MutableList<Task> {
        logger.info("Got request getTaskListByRelease [jiraFixVersion = $jiraRelease]")
        return taskService.getTasksByJiraRelease(jiraRelease)
    }

    @GetMapping("/testingInfoByRelease")
    fun getTasksTestingInfoByRelease(@RequestParam(value = "jiraRelease", defaultValue = "1.31.1") jiraRelease: String):
            MutableMap<String, MutableList<String>> {
        logger.info("Got request getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraRelease]")
        return taskService.getTasksTestingAndDeployInfoByJiraRelease(jiraRelease)
    }
}