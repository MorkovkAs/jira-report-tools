package ru.morkovka.report.interceptor

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthInterceptor(
    @Value("\${auth.encoder.strength}")
    private var encoderStrength: Int,

    @Value("\${auth.valid.token}")
    private var validToken: String
) : HandlerInterceptorAdapter() {

    val logger: Logger = LoggerFactory.getLogger(javaClass)
    private final val encoder = BCryptPasswordEncoder(encoderStrength)
    val hashedValidTokenValue = encoder.encode(validToken)!!

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.requestURI.startsWith("/api")) {
            logger.info("Check auth data for api")
            val authToken = request.getHeader("Authorization")
            checkToken(authToken, hashedValidTokenValue)
        }
        return true
    }

    @Throws(HttpClientErrorException::class)
    fun checkToken(authToken: String?, encoded: String): Boolean {
        if (authToken == null || !encoder.matches(authToken, encoded)) {
            logger.warn("Unauthorized access attempt")
            throw HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Incorrect Authorization header")
        }
        return true
    }

    fun encode(value: String) = encoder.encode(value)
}