package ru.morkovka.report.service

import ru.morkovka.report.entity.Task

interface TaskService {

    fun getTaskByJiraKey(jiraKey: String): Task

    fun getTasksByJiraRelease(jiraFixVersion: String): MutableList<Task>

    fun getTasksTestingAndDeployInfoByJiraRelease(jiraFixVersion: String): MutableMap<String, MutableList<String>>
}