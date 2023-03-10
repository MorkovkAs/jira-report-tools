package ru.morkovka.report.controller

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class TaskControllerTest {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    val port: String = System.getenv("PORT") ?: "80"

    @Ignore("Need to add Authorization header")
    @Test
    fun getTasksByJqlStringTest() {
        val jiraKey = "DM-555"
        val jql = "project = DM AND issueKey = $jiraKey"

        val result = restTemplate.getForEntity("http://localhost:$port/api/task/byJql?jql={jql}", String::class.java, jql)

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body!!.contains(jiraKey))
    }

    @Ignore("Need to add Authorization header")
    @Test
    fun getTaskByKeyTest() {
        val jiraKey = "DM-555"
        val result = restTemplate.getForEntity("http://localhost:$port/api/task/byKey?jiraKey={jiraKey}", String::class.java, jiraKey)

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body!!.contains(jiraKey))
    }

    @Ignore("Need to add Authorization header")
    @Test
    fun getTaskListByReleaseTest() {
        val jiraRelease = "1.37.0"
        val result =
            restTemplate.getForEntity("http://localhost:$port/api/task/byRelease?jiraRelease={jiraRelease}", String::class.java, jiraRelease)

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body!!.contains(jiraRelease))
    }

    @Test(expected = HttpClientErrorException.Unauthorized::class)
    fun getApiRequestWithoutAuthDataTest() {
        val jiraKey = "DM-555"
        restTemplate.getForEntity("http://localhost:$port/api/task/byKey?jiraKey={jiraKey}", String::class.java, jiraKey)
    }
}