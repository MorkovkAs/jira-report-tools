package ru.morkovka.report.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
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
        @RequestParam(value = "jiraProject", required = true) jiraProject: String,
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "releaseNumber", required = true) releaseNumber: String,
        @RequestParam(value = "lastReleaseDate", required = false) lastReleaseDate: String?,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): MutableMap<String, MutableList<String>> = releaseService.getTasksTestingAndDeployInfoByJiraRelease(jiraProject, jiraRelease, limit)

    @GetMapping("/getReleaseNote")
    fun getReleaseNoteByJiraRelease(
        @RequestParam(value = "jiraProject", required = true) jiraProject: String,
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "releaseNumber", required = true) releaseNumber: String,
        @RequestParam(value = "lastReleaseDate", required = false) lastReleaseDate: String?,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): ReleaseNote = releaseService.getReleaseNoteByJiraRelease(jiraProject, jiraRelease, limit)

    @GetMapping("/getReleaseNoteString")
    fun releaseNoteByJiraReleaseToString(
        @RequestParam(value = "jiraProject", required = true) jiraProject: String,
        @RequestParam(value = "jiraRelease", required = true) jiraRelease: String,
        @RequestParam(value = "releaseNumber", required = true) releaseNumber: String,
        @RequestParam(value = "lastReleaseDate", required = true) lastReleaseDate: String,
        @RequestParam(value = "limit", defaultValue = "\${jira.search.default.limit}") limit: Int
    ): String = releaseService.getReleaseNoteToString(jiraProject, jiraRelease, releaseNumber, lastReleaseDate, limit)
}