package ru.morkovka.report.interceptor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class LogRequestInterceptor : HandlerInterceptorAdapter() {
    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info("[REQUEST] [${request.method}]${request.requestURI} [${getParams(request)}]")

        return true
    }

    fun getParams(request: HttpServletRequest): String =
        request.parameterNames.toList()
            .stream()
            .map { "$it: ${request.getParameterValues(it).joinToString()}" }
            .collect(Collectors.toList()).joinToString("; ")
}