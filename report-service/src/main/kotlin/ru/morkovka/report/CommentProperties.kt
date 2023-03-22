package ru.morkovka.report

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "jira.search.default.comment")
data class CommentProperties(
    val sourceCode: Type,
    val artifact: Type,
    val newFeature: Type,
    val confluencePageChange: Type,
    val databaseChange: Type,
    val monitoringChange: Type,
    val config: Type,
    val deployInstruction: Type,
    val testCase: Type,
    val rollbackAction: Type
) {
    data class Type(
        val start: String?,
        val paragraph: String,
        val paragraph_external: String?,
        val default: String
    )
}