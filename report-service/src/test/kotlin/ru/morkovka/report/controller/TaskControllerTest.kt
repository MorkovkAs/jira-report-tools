package ru.morkovka.report.controller

import org.junit.Assert.*
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

    @Test
    fun getTasksByJqlStringTest() {
        val jiraKey = "DM-555"
        val jql = "project = DM AND issueKey = $jiraKey"
        val result = restTemplate.getForEntity("http://localhost:8181/task/byJql?jql={jql}", String::class.java, jql)

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body!!.contains(jiraKey))
    }

    @Test
    fun getTaskByKeyTest() {
        val jiraKey = "DM-555"
        val result = restTemplate.getForEntity("http://localhost:8181/task/byKey?jiraKey={jiraKey}", String::class.java, jiraKey)

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body!!.contains(jiraKey))
    }

    @Test
    fun getTaskListByReleaseTest() {
        val jiraRelease = "1.37.0"
        val result =
            restTemplate.getForEntity("http://localhost:8181/task/byRelease?jiraRelease={jiraRelease}", String::class.java, jiraRelease)

        assertNotNull(result)
        assertEquals(HttpStatus.OK, result.statusCode)
        assertNotNull(result.body)
        assertTrue(result.body!!.contains(jiraRelease))
    }

    @Test(expected = HttpClientErrorException.NotFound::class)
    fun notFoundControllerMethodTest() {
        restTemplate.getForEntity("http://localhost:8181/task", String::class.java)
    }
}