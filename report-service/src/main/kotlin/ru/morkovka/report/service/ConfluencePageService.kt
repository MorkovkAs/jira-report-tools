package ru.morkovka.report.service

import ru.morkovka.report.entity.ConfluencePage

interface ConfluencePageService {
    fun getChangedPagesByJiraRelease(jiraFixVersion: String, lastReleaseDate: String, limit: Int): MutableList<ConfluencePage>
}