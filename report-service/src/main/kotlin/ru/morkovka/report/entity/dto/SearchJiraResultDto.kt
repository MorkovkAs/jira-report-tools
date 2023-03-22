package ru.morkovka.report.entity.dto

data class SearchJiraResultDto(
    val expand: String,
    val startAt: Int,
    val maxResults: Int,
    val total: Int,
    val issues: MutableList<TaskDto>
)