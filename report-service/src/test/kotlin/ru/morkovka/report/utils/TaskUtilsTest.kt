package ru.morkovka.report.utils

import org.junit.Assert.*
import org.junit.Test
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import ru.morkovka.report.utils.TaskUtils.Companion.getNumberFromJiraKey
import ru.morkovka.report.utils.TaskUtils.Companion.getProjectNameFromJiraKey
import ru.morkovka.report.utils.TaskUtils.Companion.sortByJiraKey
import java.util.*

class TaskUtilsTest {

    @Test
    fun sortByJiraKeyTest() {
        val taskMap: MutableMap<String, MutableList<String>> = LinkedHashMap()
        val taskSortedKeys = mutableListOf("DM-1", "DM-2", "DM-11", "DM-NEW-2", "DM-NEW-5")

        taskSortedKeys.shuffled().forEach { taskMap[it] = mutableListOf() }
        assertNotEquals(taskSortedKeys, taskMap.keys.toList())

        val taskSortedMap = sortByJiraKey(taskMap)

        assertEquals(taskMap.size, taskSortedMap.size)
        assertEquals(taskSortedKeys.size, taskSortedMap.size)
        assertEquals(taskSortedKeys, taskSortedMap.keys.toList())
    }

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
    
    @Test
    fun checkEncoder() {
        val encoded = BCryptPasswordEncoder().encode("MorkovkA")

        assertTrue(BCryptPasswordEncoder().matches("MorkovkA", encoded))
        assertTrue(BCryptPasswordEncoder().matches("MorkovkA", "\$2a\$10\$VRn.zR2KmV/LPfss4Ez5Ne/ITdUA0STzdUKTf0Z6iyWz9/SaQRVYi"))
    }
}