package ru.morkovka.report.handler

import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import org.springframework.web.client.UnknownHttpStatusCodeException

/**
 * Custom realisation of DefaultResponseErrorHandler if needed for error handling
 *
 */
class RestTemplateResponseErrorHandler : DefaultResponseErrorHandler() {

    override fun handleError(response: ClientHttpResponse) {

        val statusCode: HttpStatus = response.statusCode
        val statusText = response.statusText
        val headers = response.headers
        val body = getResponseBody(response)
        val charset = getCharset(response)
        println("Error: statusCode [$statusCode], statusText [$statusText], body: [${String(body)}], charset [$charset]")

        when (statusCode.series()) {
            HttpStatus.Series.CLIENT_ERROR -> {
                throw HttpClientErrorException.create(
                    statusCode,
                    statusText,
                    headers,
                    body,
                    charset
                )
            }
            HttpStatus.Series.SERVER_ERROR -> {
                throw HttpServerErrorException.create(
                    statusCode,
                    statusText,
                    headers,
                    body,
                    charset
                )
            }
            else -> throw UnknownHttpStatusCodeException(statusCode.value(), statusText, headers, body, charset)
        }
    }
}