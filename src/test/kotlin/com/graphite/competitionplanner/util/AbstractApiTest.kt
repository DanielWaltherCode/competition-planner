package com.graphite.competitionplanner.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.graphite.competitionplanner.user.api.LoginDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.annotation.IfProfileValue
import org.springframework.test.context.TestPropertySource
import org.springframework.web.server.ResponseStatusException

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@IfProfileValue(name = "spring.profiles.active", value = "api-test")
@TestPropertySource(locations = ["/application.properties"])
abstract class AbstractApiTest(
    @LocalServerPort val port: Int,
    @Autowired val testRestTemplate: TestRestTemplate
) {

    val objectMapper = ObjectMapper()
    protected abstract val resource: String

    fun getUrl(): String {
        return "http://localhost:$port$resource"
    }

    fun getUrlWithoutResourcePath(): String {
        return "http://localhost:$port"
    }

    private fun login(): LoginDTO {
        // This user is created in SetupTestData.kt
        val loginDetails = UserLogin("abraham", "anders")
        val valueToSend = objectMapper.writeValueAsString(loginDetails)
        val httpHeaders = HttpHeaders()
        httpHeaders.setContentType(MediaType.APPLICATION_JSON)
        val entityToSend = HttpEntity(valueToSend, httpHeaders)

        val loginResult = testRestTemplate.postForEntity(
            getUrlWithoutResourcePath() + "/login",
            entityToSend, LoginDTO::class.java
        ).body

        return loginResult ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)

    }

    fun getAuthenticationHeaders(): HttpHeaders {
        val tokens = login()
        val httpHeaders = HttpHeaders()
        httpHeaders.set("Authorization", "Bearer ${tokens.accessToken}")
        return httpHeaders
    }
}