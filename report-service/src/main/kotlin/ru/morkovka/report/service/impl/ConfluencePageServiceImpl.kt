package ru.morkovka.report.service.impl

import com.google.gson.Gson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import ru.morkovka.report.entity.ConfluencePage
import ru.morkovka.report.entity.dto.SearchConfluenceResultDto
import ru.morkovka.report.entity.mapper.ConfluencePageMapper.Companion.getConfluencePage
import ru.morkovka.report.service.ConfluencePageService
import java.util.*
import java.util.stream.Collectors

@Service
class ConfluencePageServiceImpl(
    @Value("\${confluence.url}")
    private val confluenceUrl: String,

    @Value("\${jira.release.offline-merge.name}")
    val offlineMergeRelease: String,

    @Value("\${jira.release.kzd2.name}")
    val kzd2Release: String,

    @Value("\${jira.release.ksrd.name}")
    val ksrdRelease: String
) : ConfluencePageService {

    @Autowired
    private lateinit var restTemplate: RestTemplate

    val logger: Logger = LoggerFactory.getLogger(javaClass)
    val confluenceUrlRest: String = "$confluenceUrl/rest/api"

    /**
     *  Search and minimize representations of all the issues for the given fix version.
     *  It creates jql string and search by {@code TaskServiceImpl#getTasksByJqlString}
     *
     *  @param jiraFixVersion the code of the jira release to search by. For example "offline-merge-1.37.0"
     *  @param lastReleaseDate the date from the last release to search changed confluence pages
     *  @param limit on the number of returned issues from Jira
     *  @return the list of issues with summary, status, description, fixVersions, comments
     */
    override fun getChangedPagesByJiraRelease(jiraFixVersion: String, lastReleaseDate: String, limit: Int): MutableList<ConfluencePage> {
        //val stringLastReleaseDate = SimpleDateFormat("yyyy-MM-dd").format(lastReleaseDate)
        val cqlString = "cql=lastmodified>$lastReleaseDate ${getConfluenceTagsForRelease(jiraFixVersion)}"
        val url = "$confluenceUrlRest/search?$cqlString&start=0&limit=$limit&includeArchivedSpaces=false"

        val builder = UriComponentsBuilder.fromHttpUrl(url)
        val response: ResponseEntity<String> = restTemplate.getForEntity(
        //    builder.toUriString(),
            url,
            String::class.java
        )

        logger.info("getChangedPagesByJiraRelease [jiraFixVersion = $jiraFixVersion]: confluence search is completed")
        val searchResultDto = Gson().fromJson(response.body, SearchConfluenceResultDto::class.java)
        val pagesDtoList = searchResultDto.results
        val pageList = pagesDtoList.stream().filter(Objects::nonNull).map { getConfluencePage(it, confluenceUrl) }.collect(Collectors.toList())
        logger.info("getConfluencePage [jiraFixVersion = $jiraFixVersion]: casting to List<ConfluencePage> completed")

        return pageList
    }

    private fun getConfluenceTagsForRelease(jiraFixVersion: String): String {
        return if (jiraFixVersion.contains(offlineMergeRelease)) {
            "AND label in ('offline-merge')"
        } else if (jiraFixVersion.contains(kzd2Release)) {
            "AND label in ('kzd-gateway-asur2', 'kzd-data-transform-gateway-asur2', 'online-merge')"
        } else "AND label in ('ksrd')"
    }
}