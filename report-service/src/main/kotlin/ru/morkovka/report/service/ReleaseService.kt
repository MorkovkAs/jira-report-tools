package ru.morkovka.report.service

import ru.morkovka.report.entity.ReleaseNote

interface ReleaseService {

    fun getTasksTestingAndDeployInfoByJiraRelease(jiraProject: String, jiraFixVersion: String, limit: Int): MutableMap<String, MutableList<String>>

    fun getReleaseNoteByJiraRelease(jiraProject: String, jiraFixVersion: String, limit: Int): ReleaseNote

    fun getReleaseNoteToString(jiraProject: String, jiraFixVersion: String, releaseNumber: String, lastReleaseDate: String, limit: Int): String
}