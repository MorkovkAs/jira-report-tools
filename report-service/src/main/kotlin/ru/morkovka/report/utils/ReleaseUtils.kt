package ru.morkovka.report.utils

import ru.morkovka.report.entity.ReleaseNote
import ru.morkovka.report.entity.Task
import ru.morkovka.report.service.impl.ReleaseServiceImpl

object ReleaseUtils {

    fun taskToRelease(taskList: MutableList<Task>, dbChanges: String, dbChangesEmpty: String,
                      configs: String, configsEmpty: String,
                      installation: String, installationEmpty: String,
                      testing: String, testingEmpty: String,
                      rollback: String, rollbackEmpty: String): ReleaseNote {
        val note = ReleaseNote()
        for (task in taskList) { //(id, key, summary, status, _, _, comments)
            note.changes?.add(task.key + "; " + task.status + "; " + task.id + "; " + task.summary)
            if (task.summary.startsWith("Релиз КСРД")) {
                note.distributions = task.key + "; " + task.summary + "; " + task.link
            }
            note.dbChanges?.addAll(ReleaseServiceImpl.getCommentsFromTaskByKeyword(task, dbChanges))
            if (note.dbChanges?.isEmpty()!!) { note.dbChanges!!.add(dbChangesEmpty) }
            note.configs?.addAll(ReleaseServiceImpl.getCommentsFromTaskByKeyword(task, configs))
            if (note.configs?.isEmpty()!!) { note.configs!!.add(configsEmpty) }
            note.installation?.addAll(ReleaseServiceImpl.getCommentsFromTaskByKeyword(task, installation))
            if (note.installation?.isEmpty()!!) { note.installation!!.add(installationEmpty) }
            note.testing?.addAll(ReleaseServiceImpl.getCommentsFromTaskByKeyword(task, testing))
            if (note.testing?.isEmpty()!!) { note.testing!!.add(testingEmpty) }
            note.rollback?.addAll(ReleaseServiceImpl.getCommentsFromTaskByKeyword(task, rollback))
            if (note.rollback?.isEmpty()!!) { note.rollback!!.add(rollbackEmpty) }
        }
        return note
    }
}