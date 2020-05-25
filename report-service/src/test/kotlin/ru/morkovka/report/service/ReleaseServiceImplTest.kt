package ru.morkovka.report.service

import org.junit.*;
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import ru.morkovka.report.utils.TaskUtils
import java.util.*
import kotlin.collections.set

@Ignore("not ready yet")
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ReleaseServiceImplTest {

    @Test
    fun customSort(map: MutableMap<String, MutableList<String>>) {
        val expectedResult: String =
            "DM-1 = [first test message, second test message, third test message]\n" +
                    "DM-3 = [first test message, second test message, third test message]\n" +
                    "DM-4 = [first test message, second test message, third test message]\n" +
                    "DM-New-2 = [first test message, second test message, third test message]\n" +
                    "DM-New-5 = [first test message, second test message, third test message]\n"
        val checkMap: MutableMap<String, MutableList<String>> = LinkedHashMap()
        val list: MutableList<String> = ArrayList()
        list.add("first test message")
        list.add("second test message")
        list.add("third test message")
        checkMap["DM-3"] = list
        checkMap["DM-4"] = list
        checkMap["DM-1"] = list
        checkMap["DM-New-5"] = list
        checkMap["DM-New-2"] = list

        val sortedMap: MutableMap<String, MutableList<String>> = TaskUtils.sortByJiraKey(map)
        val result = StringBuilder()
        for ((k, v) in sortedMap) {
            result.append(
                "$k = $v"
            )
        }

        Assert.assertEquals(expectedResult, result.toString())
    }
}