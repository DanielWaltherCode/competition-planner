package com.graphite.competitionplanner.security

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.util.AbstractApiTest
import com.graphite.competitionplanner.util.InterceptorRegistry
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(InterceptorRegistry::class)
class TestAccessInterceptor(@LocalServerPort port: Int,
                            @Autowired testRestTemplate: TestRestTemplate,
                            @Autowired val findCompetitions: FindCompetitions,
                            @Autowired val util: Util
) : AbstractApiTest(port, testRestTemplate) {
    override val resource = "/competition"

    @Test
    fun testAccessOwnCompetition() {
        // Setup
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitionId = findCompetitions.thatBelongTo(lugiId)[0].id

        // Act
        // Call endpoint with id and ensure no exception
        val headers: HttpHeaders = getAuthenticationHeaders()
        val result = testRestTemplate.exchange(getUrl() + "/" + lugiCompetitionId, HttpMethod.GET,
            HttpEntity<JvmType.Object>(headers),  String::class.java)

        // Assert
        Assertions.assertTrue(result.statusCode == HttpStatus.OK)
    }

    @Test
    fun testAccessOtherCompetition() {
        // Call endpoint with incorrect id and ensure exception
        val headers: HttpHeaders = getAuthenticationHeaders()
        val result = testRestTemplate.exchange(getUrl() + "/999999", HttpMethod.GET,
            HttpEntity<JvmType.Object>(headers),  String::class.java)
        Assertions.assertTrue(result.statusCode.value() == HttpStatus.FORBIDDEN.value())

    }
}