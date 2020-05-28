package ru.morkovka.report.service.impl

import org.junit.Assert.*
import org.junit.Test

class ReleaseServiceImplTest {

    @Test
    fun mergeMapsTest() {
        val keyDuplicates = "DM-3"
        val map1: MutableMap<String, MutableList<String>> = mutableMapOf(
            "DM-1" to mutableListOf("111"),
            "DM-2" to mutableListOf("222"),
            keyDuplicates to mutableListOf("333")
        )
        val map2: MutableMap<String, MutableList<String>> = mutableMapOf(
            keyDuplicates to mutableListOf("000"),
            "DM-4" to mutableListOf("444"),
            "DM-5" to mutableListOf("555")
        )
        val mapMerged = ReleaseServiceImpl.mergeMaps(map1, map2)

        assertNotNull(mapMerged)
        assertEquals(5, mapMerged.size)
        map1.forEach { (key, _) -> assertTrue(mapMerged.containsKey(key)) }
        map2.forEach { (key, _) -> assertTrue(mapMerged.containsKey(key)) }
        assertEquals(2, mapMerged[keyDuplicates]?.size)
        assertEquals(listOf("333", "000"), mapMerged[keyDuplicates])
    }
}