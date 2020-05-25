package ru.morkovka.report.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import ru.morkovka.report.utils.TaskUtils.Companion.getNumberFromJiraKey
import ru.morkovka.report.utils.TaskUtils.Companion.getProjectNameFromJiraKey

class TaskUtilsTest {

    @Test
    fun getProjectNameFromJiraKeyTest() {
        val jiraKey = "DM-123"

        assertEquals("DM", getProjectNameFromJiraKey(jiraKey))
    }

    @Test
    fun getProjectNameFromJiraKeyTestCheckCaseInsensitiveness() {
        val jiraKey = "Dm-123"

        assertEquals("Dm", getProjectNameFromJiraKey(jiraKey))
    }

    @Test
    fun getProjectNameFromJiraKeyTestMultipleHyphens() {
        val jiraKey = "DM-NEW-123"

        assertEquals("DM-NEW", getProjectNameFromJiraKey(jiraKey))
    }

    @Test
    fun getProjectNameFromJiraKeyTestCorrectNameWithNumbers() {
        val jiraKey = "D12M-123"
        val jiraKey2 = "D1M2-123"
        val jiraKey3 = "1D2M3-123"

        assertEquals("D12M", getProjectNameFromJiraKey(jiraKey))
        assertEquals("D1M2", getProjectNameFromJiraKey(jiraKey2))
        assertEquals("1D2M3", getProjectNameFromJiraKey(jiraKey3))
    }

    @Test
    fun getNumberFromJiraKeyTest() {
        val jiraKey = "DM-123"

        assertEquals(123, getNumberFromJiraKey(jiraKey))
    }

    @Test
    fun getNumberFromJiraKeyTestMultipleHyphens() {
        val jiraKey = "DM-NEW-123"

        assertEquals(123, getNumberFromJiraKey(jiraKey))
    }

    @Test
    fun getNumberFromJiraKeyWithLetters() {
        val jiraKey = "D12M-1B23"

        assertNotEquals(123, getProjectNameFromJiraKey(jiraKey))
    }

    // TODO tests for sort function of TaskUtils class
}