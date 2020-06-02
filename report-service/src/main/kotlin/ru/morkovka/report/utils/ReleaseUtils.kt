package ru.morkovka.report.utils

import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task

object ReleaseUtils {
    fun taskToRelease(taskList: MutableList<Task>): ReleaseNote {
        val note = ReleaseNote()
        for ((id, key, summary, status, _, _, comments) in taskList) {
            note.changes?.add(key + "\t" + status + "\t" + id + "\t" + summary)
            parseCommentToFields(comments, note)
        }
        return note
    }

    private fun parseCommentToFields(
        commentList: List<String>,
        note: ReleaseNote
    ) {
        for (comment in commentList) {
            when {
                comment.startsWith("БД: ") -> {
                    note.dbChanges?.add(comment.replace("БД: ", ""))
                }
                comment.startsWith("Конфигурация: ") -> {
                    note.configs?.add(comment.replace("Конфигурация: ", ""))
                }
                comment.startsWith("Установка: ") -> {
                    note.installation?.add(comment.replace("Установка: ", ""))
                }
                comment.startsWith("Тестирование: ") -> {
                    note.testing?.add(comment.replace("Тестирование: ", ""))
                }
                comment.startsWith("Откат: ") -> {
                    note.rollback?.add(comment.replace("Откат: ", ""))
                }
            }
        }
    }
}