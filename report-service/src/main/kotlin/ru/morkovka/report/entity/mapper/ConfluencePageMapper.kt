package ru.morkovka.report.entity.mapper

import ru.morkovka.report.entity.ConfluencePage
import ru.morkovka.report.entity.dto.ConfluencePageDto

class ConfluencePageMapper {
    companion object {
        fun getConfluencePage(confluencePageDto: ConfluencePageDto, confluenceUrl: String) = ConfluencePage(
            type = confluencePageDto.entityType,
            title = confluencePageDto.title,
            link = confluenceUrl + confluencePageDto.url,
            lastModified = confluencePageDto.lastModified
        )
    }
}