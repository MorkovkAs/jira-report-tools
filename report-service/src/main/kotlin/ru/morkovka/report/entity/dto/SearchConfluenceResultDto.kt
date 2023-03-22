package ru.morkovka.report.entity.dto

data class SearchConfluenceResultDto(
    val cqlQuery: String,
    val start: Int,
    val limit: Int,
    val size: Int,
    val results: MutableList<ConfluencePageDto>
)