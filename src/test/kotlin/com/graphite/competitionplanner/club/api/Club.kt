package com.graphite.competitionplanner.club.api

import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.util.AbstractApiTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Club(
        @LocalServerPort port: Int,
        @Autowired testRestTemplate: TestRestTemplate
) : AbstractApiTest(
    port,
    testRestTemplate
) {
    override val resource: String = "/club"

    @Test
    fun shouldGetHttpNotFoundWhenNotFindingClub() {
        val request = HttpEntity(ClubSpec::class.java, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${-1}", HttpMethod.GET, request)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}