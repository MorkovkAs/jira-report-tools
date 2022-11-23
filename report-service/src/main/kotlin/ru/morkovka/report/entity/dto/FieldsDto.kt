package ru.morkovka.report.entity.dto

data class FieldsDto(
    val summary: String,
    val status: HashMap<String, Any>?,
    val description: String?,
    val fixVersions: ArrayList<HashMap<String, String>>?,
    val customfield_13700: String?, //our custom field in Jira for external tasks
    val comment: CommentsDto?
)