package ru.morkovka.report.entity.mapper

import ru.morkovka.report.entity.Task
import ru.morkovka.report.entity.dto.TaskDto
import java.util.*
import java.util.stream.Collectors

class TaskMapper {
    companion object {
        fun getTask(taskDto: TaskDto) = Task(
            id = taskDto.id,
            key = taskDto.key,
            summary = taskDto.fields.summary,
            status = taskDto.fields.status?.get("name")?.toString() ?: "",
            description = taskDto.fields.description ?: "",
            fixVersions = (taskDto.fields.fixVersions ?: arrayListOf<Map<String, String>>()).stream()
                .filter(Objects::nonNull)
                .map { it["name"] }
                .filter(Objects::nonNull)
                .collect(Collectors.toList()),
            comments = (taskDto.fields.comment?.comments ?: arrayListOf<Map<String, Any>>()).stream()
                .filter(Objects::nonNull)
                .map { it["body"] as String }
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        )
    }
}