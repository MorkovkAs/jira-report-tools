package ru.morkovka.report

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Description
import org.springframework.http.MediaType
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.converter.StringHttpMessageConverter
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.RestTemplate
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import ru.morkovka.report.interceptor.AuthInterceptor
import ru.morkovka.report.interceptor.LogRequestInterceptor
import java.nio.charset.StandardCharsets

@Configuration
@ComponentScan(basePackages = ["ru.morkovka.report"])
class AppConfiguration(
    @Value("\${jira.auth.basic}")
    private val jiraAuthBasic: String
) : WebMvcConfigurer {
    @Autowired
    lateinit var logRequestInterceptor: LogRequestInterceptor

    @Autowired
    lateinit var authInterceptor: AuthInterceptor

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(logRequestInterceptor)
        registry.addInterceptor(authInterceptor)
    }

    @Bean
    @Description("RestTemplate connection with predefined auth headers")
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplateBuilder().errorHandler(DefaultResponseErrorHandler()).build()
        restTemplate.getMessageConverters()
            .add(0, StringHttpMessageConverter(StandardCharsets.UTF_8));

        //Adding a ClientHttpRequestInterceptor to the RestTemplate
        restTemplate.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers["Content-Type"] = MediaType.APPLICATION_JSON_VALUE
            //TODO check your auth data in application.yml file. It should be the result of ("Basic " + base64(login + ":" + password))
            request.headers["Authorization"] = jiraAuthBasic

            execution.execute(request, body)
        })
        return restTemplate
    }
}