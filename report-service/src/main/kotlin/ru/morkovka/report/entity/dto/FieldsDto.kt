package ru.morkovka.report.entity.dto

data class FieldsDto(
    val summary: String,
    val status: HashMap<String, Any>?,
    val description: String?,
    val fixVersions: ArrayList<HashMap<String, String>>?,
    val comment: CommentsDto?
)