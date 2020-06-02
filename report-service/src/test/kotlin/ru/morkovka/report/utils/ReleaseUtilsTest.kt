package ru.morkovka.report.utils

import org.junit.Assert
import org.junit.Test
import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import java.util.*

class ReleaseUtilsTest {
    @Test
    fun taskToReleaseTest() {
        val taskList = createTaskList()
        val actualNote = ReleaseUtils.taskToRelease(taskList)
        val expectedNote = createReleaseNote()
        Assert.assertEquals(expectedNote, actualNote)
    }

    private fun createReleaseNote(): ReleaseNote {
        val result = ReleaseNote()
        result.changes = ArrayList(
            listOf(
                "DM-815" + "\t" + "Решенные" + "\t" + 92723 + "\t" + "Решение проблемы с дублями (профили с одним id) из-за близких во времени запросов от КЗД",
                "DM-874" + "\t" + "Решенные" + "\t" + 95825 + "\t" + "Создание операции удаления профилей по массиву mdm_id",
                "DM-884" + "\t" + "Решенные" + "\t" + 96334 + "\t" + "Перевести вставку гражданина на bulkUpsert"
            )
        )
        result.dbChanges?.add("Данные по полю с БД")
        result.configs?.add("Данные по полю с конфигурацией")
        result.installation?.add("Данные по полю с установкой")
        result.testing?.add("Данные по полю с тестированием")
        result.rollback?.add("Данные по полю с откатом")
        return result
    }

    private fun createTaskList(): ArrayList<Task> {
        val result =
            ArrayList<Task>()
        val id1 = 92723.toLong()
        val key1 = "DM-815"
        val summary1 =
            "Решение проблемы с дублями (профили с одним id) из-за близких во времени запросов от КЗД"
        val status1 = "Решенные"
        val description1 = "Разобраться с двойным инсертом со стороны кзд."
        val fixVersions1 =
            ArrayList(listOf("1.31.2", "1.37.0"))
        val comments1 = ArrayList(
            listOf(
                "БД: Данные по полю с БД",
                "Конфигурация: Данные по полю с конфигурацией"
            )
        )
        val task1 =
            Task(id1, key1, summary1, status1, description1, fixVersions1, comments1)
        val id2 = 95825.toLong()
        val key2 = "DM-874"
        val summary2 = "Создание операции удаления профилей по массиву mdm_id"
        val status2 = "Решенные"
        val description2 =
            "Необходимо создать таблицу в pg с столбцами mdm_id и timestamp, реализовать операцию массового удаления профилей (не физического)."
        val fixVersions2 =
            ArrayList(listOf("1.37.0"))
        val comments2 = ArrayList(
            listOf(
                "Установка: Данные по полю с установкой",
                "Тестирование: Данные по полю с тестированием"
            )
        )
        val task2 =
            Task(id2, key2, summary2, status2, description2, fixVersions2, comments2)
        val id3 = 96334.toLong()
        val key3 = "DM-884"
        val summary3 = "Перевести вставку гражданина на bulkUpsert"
        val status3 = "Решенные"
        val description3 = "Перевести вставку гражданина на bulkUpsert + убрать откат"
        val fixVersions3 =
            ArrayList(listOf("1.37.0"))
        val comments3 = ArrayList(
            listOf(
                "Откат: Данные по полю с откатом"
            )
        )
        val task3 =
            Task(id3, key3, summary3, status3, description3, fixVersions3, comments3)
        result.add(task1)
        result.add(task2)
        result.add(task3)
        return result
    }
}