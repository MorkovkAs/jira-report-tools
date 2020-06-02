package ru.morkovka.report.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.*
import ru.morkovka.report.entity.ReleaseNote
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
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int,
        @RequestParam(value = "token") token: String?
    ): MutableMap<String, MutableList<String>> {
        logger.info("Got request getTasksTestingAndDeployInfoByJiraRelease [jiraFixVersion = $jiraRelease; limit = $limit]")
        checkToken(token)
        return releaseService.getTasksTestingAndDeployInfoByJiraRelease(jiraRelease, limit)
    }

    @GetMapping("/getReleaseNote")
    fun getReleaseNoteByJiraRelease(
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int,
        @RequestParam(value = "token") token: String?
    ): ReleaseNote {
        logger.info("Got request getReleaseNoteByJiraRelease [jiraFixVersion = $jiraRelease; limit = $limit]")
        checkToken(token)
        return releaseService.getReleaseNoteByJiraRelease(jiraRelease, limit)
    }

    private fun checkToken(token: String?) {
        val hashedValue = "\$2a\$10\$9eYB57vXgXENoyXfiPXTyebWCfquNBfOkXKMCrnnLIrflQY8R3Ca."
        if (token == null || !BCryptPasswordEncoder().matches(token, hashedValue)) {
            logger.warn("Unauthorized access attempt")
            throw IllegalAccessException("Incorrect token")
        }
    }
}