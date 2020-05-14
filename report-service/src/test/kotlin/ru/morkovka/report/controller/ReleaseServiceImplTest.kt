package ru.morkovka.report.controller
/**
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.util.*
import kotlin.collections.set

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ReleaseServiceImplTest {

    @Autowired
    private lateinit var releaseServiceImplTest: ReleaseServiceImplTest

    @Test
    fun customSort(map: MutableMap<String, MutableList<String>>) {
        val expectedResult: String =
            "DM-1 = [first test message, second test message, third test message]\n" +
                    "DM-New-2 = [first test message, second test message, third test message]\n" +
                    "DM-3 = [first test message, second test message, third test message]\n" +
                    "DM-4 = [first test message, second test message, third test message]\n" +
                    "DM-New-5 = [first test message, second test message, third test message]\n"
        val map: MutableMap<String, MutableList<String>> = LinkedHashMap()
        val list: MutableList<String> = ArrayList()
        list.add("first test message")
        list.add("second test message")
        list.add("third test message")
        map["DM-3"] = list
        map["DM-4"] = list
        map["DM-1"] = list
        map["DM-New-5"] = list
        map["DM-New-2"] = list

        val sortedMap: MutableMap<String, MutableList<String>> = releaseServiceImplTest.customSort(map)
        val result = StringBuilder()
        for ((k, v) in sortedMap) {
            result.append(
                "$k = $v"
            )
        }

        Assert.assertEquals(expectedResult, result.toString())
    }
}
**/