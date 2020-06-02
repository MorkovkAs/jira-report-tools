package ru.morkovka.report.interceptor

import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.client.HttpClientErrorException

@RunWith(SpringRunner::class)
@SpringBootTest
@ContextConfiguration(classes = [AuthInterceptor::class])
class AuthInterceptorTest {

    @Autowired
    private lateinit var authInterceptor: AuthInterceptor

    @Test
    fun checkTokenTest() {
        val encoded = authInterceptor.encode("MorkovkA")
        assertTrue(authInterceptor.checkToken("MorkovkA", encoded))
    }

    @Test(expected = HttpClientErrorException::class)
    fun checkWrongTokenTest() {
        val encoded = authInterceptor.encode("MorkovkA")
        authInterceptor.checkToken("_MorkovkA_", encoded)
    }
}