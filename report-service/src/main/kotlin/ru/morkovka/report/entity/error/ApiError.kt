package ru.morkovka.report.entity.error

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

/**
 * Error handling by
 * https://stackoverflow.com/questions/38117717/what-is-the-best-way-to-return-different-types-of-responseentity-in-spring-mvc-o
 * https://www.toptal.com/java/spring-boot-rest-api-error-handling
 */
data class ApiError @JvmOverloads constructor(
    val status: HttpStatus,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val message: String = "Unexpected error",
    val debugMessage: String,
    val exception: String?,
    val subErrors: List<String> = arrayListOf()
) {
    constructor(status: HttpStatus) : this(status = status, debugMessage = "", exception = null)
    constructor(status: HttpStatus, ex: Throwable) : this(
        status = status,
        debugMessage = ex.localizedMessage,
        exception = ex.toString()
    )

    constructor(status: HttpStatus, message: String, ex: Throwable) : this(
        status = status,
        message = message,
        debugMessage = ex.localizedMessage,
        exception = ex.toString()
    )
}