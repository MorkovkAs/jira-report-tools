package ru.morkovka.report.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import ru.morkovka.report.service.ReleaseService

@RestController
@RequestMapping("/release")
@CrossOrigin
class ReleaseController(
    @Autowired
    val releaseService: ReleaseService
) {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/infoByRelease")
    fun getTasksTestingAndDeployInfoByJiraRelease(
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): MutableMap<String, MutableList<String>> {
        logger.info("Got request getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraRelease; limit = $limit]")
        return releaseService.getTasksTestingAndDeployInfoByJiraRelease(jiraRelease, limit)
    }
}