package ru.morkovka.report.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.service.ReleaseService

@RestController
@RequestMapping("/api/release")
class ReleaseController(
    @Autowired
    val releaseService: ReleaseService
) {

    @GetMapping("/infoByRelease")
    fun getTasksTestingAndDeployInfoByJiraRelease(
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): MutableMap<String, MutableList<String>> = releaseService.getTasksTestingAndDeployInfoByJiraRelease(jiraRelease, limit)

    @GetMapping("/getReleaseNote")
    fun getReleaseNoteByJiraRelease(
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): ReleaseNote = releaseService.getReleaseNoteByJiraRelease(jiraRelease, limit)
}