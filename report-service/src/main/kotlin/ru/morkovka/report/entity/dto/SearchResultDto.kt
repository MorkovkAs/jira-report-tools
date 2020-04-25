package ru.morkovka.report.entity.dto

data class SearchResultDto(
    val expand: String,
    val startAt: Int,
    val maxResults: Int,
    val total: Int,
    val issues: MutableList<TaskDto>
)