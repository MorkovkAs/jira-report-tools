package ru.morkovka.report.handler

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import ru.morkovka.report.entity.error.ApiError

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        return buildResponseEntity(ApiError(status = HttpStatus.BAD_REQUEST, message = "Malformed JSON request", ex = ex))
    }

    @ExceptionHandler(HttpClientErrorException::class)
    protected fun handleHttpClientErrorException(ex: HttpClientErrorException): ResponseEntity<Any> {
        logger.warn("HttpClientErrorException: statusCode [${ex.statusCode}], statusText [${ex.statusText}], body: [${ex.responseBodyAsString}]")

        when (ex.statusCode) {
            HttpStatus.FORBIDDEN, HttpStatus.UNAUTHORIZED, HttpStatus.BAD_REQUEST, HttpStatus.NOT_FOUND, HttpStatus.UNSUPPORTED_MEDIA_TYPE
            -> {
                logger.warn(ex.printStackTrace())
                return buildResponseEntity(ApiError(status = ex.statusCode, message = ex.responseBodyAsString, ex = ex))
            }
            else -> {
                throw ex
            }
        }
    }

    private fun buildResponseEntity(apiError: ApiError) = ResponseEntity<Any>(apiError, apiError.status)
}