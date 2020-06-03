package ru.morkovka.report.service

import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task

interface ReleaseService {

    fun getTasksTestingAndDeployInfoByJiraRelease(jiraFixVersion: String, limit: Int): MutableMap<String, MutableList<String>>

    fun getReleaseNoteByJiraRelease(jiraFixVersion: String, limit: Int): ReleaseNote

    fun releaseNoteToString(jiraFixVersion: String, limit: Int): String

    fun constructReleaseNote(taskList: MutableList<Task>): ReleaseNote
}