package ru.morkovka.report.entity

data class Task(
    val id: Long,
    val key: String,
    val link: String,
    var externalJiraLink: String,
    var summary: String,
    var status: String,
    var description: String,
    var fixVersions: MutableList<String>,
    var comments: MutableList<String>
)