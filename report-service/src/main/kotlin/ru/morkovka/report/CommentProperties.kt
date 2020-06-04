package ru.morkovka.report

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class CommentProperties (
    @Value("\${jira.search.default.comment.task.out.paragraph}")
    val taskOutParagraph: String,

    @Value("\${jira.search.default.comment.task.in.start}")
    val taskInStart: String,
    @Value("\${jira.search.default.comment.task.in.paragraph}")
    val taskInParagraph: String,

    @Value("\${jira.search.default.comment.source.code.start}")
    val sourceCodeStart: String,
    @Value("\${jira.search.default.comment.source.code.paragraph}")
    val sourceCodeParagraph: String,
    @Value("\${jira.search.default.comment.source.code.default}")
    val sourceCodeDefault: String,

    @Value("\${jira.search.default.comment.artifact.start}")
    val artifactStart: String,
    @Value("\${jira.search.default.comment.artifact.paragraph}")
    val artifactParagraph: String,
    @Value("\${jira.search.default.comment.artifact.default}")
    val artifactDefault: String,

    @Value("\${jira.search.default.comment.new.feature.start}")
    val newFeatureStart: String,
    @Value("\${jira.search.default.comment.new.feature.paragraph}")
    val newFeatureParagraph: String,
    @Value("\${jira.search.default.comment.new.feature.default}")
    val newFeatureDefault: String,

    @Value("\${jira.search.default.comment.database.change.start}")
    val databaseChangeStart: String,
    @Value("\${jira.search.default.comment.database.change.paragraph}")
    val databaseChangeParagraph: String,
    @Value("\${jira.search.default.comment.database.change.default}")
    val databaseChangeDefault: String,

    @Value("\${jira.search.default.comment.monitoring.change.start}")
    val monitoringChangeStart: String,
    @Value("\${jira.search.default.comment.monitoring.change.paragraph}")
    val monitoringChangeParagraph: String,
    @Value("\${jira.search.default.comment.monitoring.change.default}")
    val monitoringChangeDefault: String,

    @Value("\${jira.search.default.comment.config.start}")
    val configStart: String,
    @Value("\${jira.search.default.comment.config.paragraph}")
    val configParagraph: String,
    @Value("\${jira.search.default.comment.config.default}")
    val configDefault: String,

    @Value("\${jira.search.default.comment.deploy.instruction.start}")
    val deployInstructionStart: String,
    @Value("\${jira.search.default.comment.deploy.instruction.paragraph}")
    val deployInstructionParagraph: String,
    @Value("\${jira.search.default.comment.deploy.instruction.default}")
    val deployInstructionDefault: String,

    @Value("\${jira.search.default.comment.test.case.start}")
    val testCaseStart: String,
    @Value("\${jira.search.default.comment.test.case.paragraph}")
    val testCaseParagraph: String,
    @Value("\${jira.search.default.comment.test.case.default}")
    val testCaseDefault: String,

    @Value("\${jira.search.default.comment.rollback.action.start}")
    val rollbackActionStart: String,
    @Value("\${jira.search.default.comment.rollback.action.paragraph}")
    val rollbackActionParagraph: String,
    @Value("\${jira.search.default.comment.rollback.action.default}")
    val rollbackActionDefault: String
)