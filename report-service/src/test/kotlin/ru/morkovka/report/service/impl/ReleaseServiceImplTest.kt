package ru.morkovka.report.service.impl

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.ReleaseService
import java.util.ArrayList

@Ignore("not ready yet")
@SpringBootTest
class ReleaseServiceImplTest {

    @Autowired
    private lateinit var releaseService: ReleaseService

//    @MockBean
//    private lateinit var taskList: MutableList<Task>

    @MockBean
    private lateinit var jiraFixVersion: String

    @MockBean
    private var limit: Int = 15

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
/**
    @Test
    fun getReleaseNoteFromTaskList() {
        var taskList = createTaskList()
        var taskListSpy = Mockito.spy(taskList)
        val actualNote = createReleaseNote()
        val expectedNote = releaseService.getReleaseNoteByJiraRelease(jiraFixVersion, limit)
        assertEquals(expectedNote, actualNote)
    }

    private fun createReleaseNote(): ReleaseNote {
        val result = ReleaseNote()
        result.taskInKey = "https://jira.unidata-platform.com/browse/DM-815"
        result.sourceCode
        result.artifact
        result.features = ArrayList(
            listOf(
                "DM-874" + "; " + "Решенные" + "; " + "Создание операции удаления профилей по массиву mdm_id",
                "DM-884" + "; " + "Решенные" + "; " + "Перевести вставку гражданина на bulkUpsert"
            )
        )
        result.dbChanges.add("Данные по полю с БД")
        result.monitoringChanges.add("Данные по полю с мониторингом")
        result.configs.add("Данные по полю с конфигурацией")
        result.deploy.add("Данные по полю с установкой")
        result.testCase.add("Данные по полю с тестированием")
        result.rollback.add("Данные по полю с откатом")
        return result
    }

    private fun createTaskList(): ArrayList<Task> {
        val result =
            ArrayList<Task>()
        val id1 = 92723.toLong()
        val key1 = "DM-815"
        val link1 = "https://jira.unidata-platform.com/browse/$key1"
        val summary1 =
            "Релиз КСРД"
        val status1 = "Решенные"
        val description1 = "Разобраться с двойным инсертом со стороны кзд."
        val fixVersions1 =
            ArrayList(listOf("1.31.2", "1.37.0"))
        val comments1 = ArrayList(
            listOf(
                "h2.Настройки и изменения в структуре БД, атрибутах, справочниках: Данные по полю с БД",
                "h2.Конфигурация: Данные по полю с конфигурацией"
            )
        )
        val task1 =
            Task(id1, key1, link1, summary1, status1, description1, fixVersions1, comments1)
        val id2 = 95825.toLong()
        val key2 = "DM-874"
        val link2 = "https://jira.unidata-platform.com/browse/$key2"
        val summary2 = "Создание операции удаления профилей по массиву mdm_id"
        val status2 = "Решенные"
        val description2 =
            "Необходимо создать таблицу в pg с столбцами mdm_id и timestamp, реализовать операцию массового удаления профилей (не физического)."
        val fixVersions2 =
            ArrayList(listOf("1.37.0"))
        val comments2 = ArrayList(
            listOf(
                "h2.Порядок установки и изменения настроек: Данные по полю с установкой",
                "h2.План тестирования у заказчика: Данные по полю с тестированием"
            )
        )
        val task2 =
            Task(id2, key2, link2, summary2, status2, description2, fixVersions2, comments2)
        val id3 = 96334.toLong()
        val key3 = "DM-884"
        val link3 = "https://jira.unidata-platform.com/browse/$key3"
        val summary3 = "Перевести вставку гражданина на bulkUpsert"
        val status3 = "Решенные"
        val description3 = "Перевести вставку гражданина на bulkUpsert + убрать откат"
        val fixVersions3 =
            ArrayList(listOf("1.37.0"))
        val comments3 = ArrayList(
            listOf(
                "h2.План отката: " + "Данные по полю с откатом",
                "h2.Информация для подключений и мониторинга: Данные по полю информаця для подключения и мониторинга"
            )
        )
        val task3 =
            Task(id3, key3, link3, summary3, status3, description3, fixVersions3, comments3)
        result.add(task1)
        result.add(task2)
        result.add(task3)
        return result
    }
    **/
}