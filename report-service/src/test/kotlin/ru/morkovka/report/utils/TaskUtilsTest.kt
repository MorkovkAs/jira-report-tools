package ru.morkovka.report.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.morkovka.report.utils.TaskUtils.Companion.getProjectNameFromJiraKey

class TaskUtilsTest {

    @Test
    fun getProjectNameFromJiraKeyTestCorrect() {
        val jiraKey = "DM-123"

        assertEquals("DM", getProjectNameFromJiraKey(jiraKey))
    }

    @Test
    fun getProjectNameFromJiraKeyTestCorrect2() {
        val jiraKey = "DM-NEW-123"

        assertEquals("DM-NEW", getProjectNameFromJiraKey(jiraKey))
    }

    @Test
    fun getProjectNameFromJiraKeyTestCorrect3() {
        val jiraKey = "D12M-123"

        assertEquals("D12M", getProjectNameFromJiraKey(jiraKey))
    }

    @Test
    fun getProjectNameFromJiraKeyTestCorrect4() {
        val jiraKey = "D1M2-123"

        assertEquals("D1M2", getProjectNameFromJiraKey(jiraKey))
    }

    // TODO tests for else functions of TaskUtils class
}