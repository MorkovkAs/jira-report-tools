package ru.morkovka.report.service

interface ReleaseService {

    fun getTasksTestingAndDeployInfoByJiraRelease(jiraFixVersion: String, limit: Int): MutableMap<String, MutableList<String>>
}