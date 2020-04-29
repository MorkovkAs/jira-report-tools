package ru.morkovka.report.service

import ru.morkovka.report.entity.Task

interface TaskService {

    fun getTasksByJqlString(jqlString: String, limit: Int): MutableList<Task>

    fun getTaskByJiraKey(jiraKey: String): Task

    fun getTasksByJiraRelease(jiraFixVersion: String, limit: Int): MutableList<Task>

    fun getTasksTestingAndDeployInfoByJiraRelease(jiraFixVersion: String, limit: Int): MutableMap<String, MutableList<String>>
}