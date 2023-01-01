package com.graphite.competitionplanner.security

import com.graphite.competitionplanner.club.domain.CreateClub
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.user.api.LoginDTO
import com.graphite.competitionplanner.user.api.UserSpec
import com.graphite.competitionplanner.user.service.UserService
import com.graphite.competitionplanner.util.*
import com.graphite.competitionplanner.util.InterceptorRegistry
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.http.*
import org.springframework.web.server.ResponseStatusException
import kotlin.random.Random
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(InterceptorRegistry::class)
class TestAccessInterceptor(
    @LocalServerPort port: Int,
    @Autowired testRestTemplate: TestRestTemplate,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val util: Util,
    @Autowired val createClub: CreateClub,
    @Autowired val createCompetition: CreateCompetition,
    @Autowired val userService: UserService
) : AbstractApiTest(port, testRestTemplate) {

    override val resource = "/competition"

    val dataGenerator = DataGenerator()
    lateinit var username: String
    lateinit var password: String
    lateinit var club: ClubDTO

    @BeforeEach
    fun setupClubAndUser() {
        club = createClub.execute(dataGenerator.newClubSpec())
        username = "Test" + Random.nextLong().toString()
        password = "password" + Random.nextLong().toString()
        userService.addUser(UserSpec(username, password, club.id))
    }

    @Test
    fun testAccessOwnCompetition() {
        // Setup
        val competition = createCompetition.execute(dataGenerator.newCompetitionSpec(organizingClubId = club.id))

        // Act
        // Call endpoint with id and ensure no exception
        val headers: HttpHeaders = getAuthenticationHeaders()
        val result = testRestTemplate.exchange(getUrl() + "/" + competition.id, HttpMethod.GET,
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

    override fun login(): LoginDTO {
        val loginDetails = UserLogin(username, password)
        val valueToSend = objectMapper.writeValueAsString(loginDetails)
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        val entityToSend = HttpEntity(valueToSend, httpHeaders)

        val loginResult = testRestTemplate.postForEntity(
            getBaseUrl() + "/login",
            entityToSend, LoginDTO::class.java
        ).body

        return loginResult ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)

    }
}