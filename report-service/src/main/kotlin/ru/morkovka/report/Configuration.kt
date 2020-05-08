package ru.morkovka.report

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate

@Configuration
class Configuration(
    @Value("\${jira.auth.basic}")
    private val jiraAuthBasic: String
) {
    @Bean
    @Description("RestTemplate connection with predefined auth headers")
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplateBuilder().errorHandler(DefaultResponseErrorHandler()).build()

        //Add a ClientHttpRequestInterceptor to the RestTemplate
        restTemplate.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
            //TODO check your auth data in application.yml file. It should be the result of ("Basic " + base64(login + ":" + password))
            val authVar: String = System.getenv("jiraAuthBasic") ?: jiraAuthBasic
            request.headers["Authorization"] = authVar

            execution.execute(request, body)
        })
        return restTemplate
    }
}