package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.PlayerDTO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.LocalDate
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Player(
    @LocalServerPort val port: Int,
    @Autowired val testRestTemplate: TestRestTemplate,
    @Autowired val clubApi: ClubApi,
    @Autowired val helper: TestHelper
)
{
    val baseAddress = "http://localhost:$port/player"
    lateinit var club: ClubDTO

    @BeforeEach
    private fun createClub() {
        club = clubApi.addClub(
            ClubDTO(
                null,
                "TestClub" + Random.nextLong().toString(),
                "Testroad 12B"
            )
        )
    }

    @AfterEach
    private fun cleanUp(){
        helper.cleanUpAll()
    }

    @Test
    fun createdPlayerShouldMatchSpec() {
        // Setup
        val playerSpec = helper.anyPlayerSpecFor(club)

        // Act
        val player = testRestTemplate.postForObject(baseAddress, playerSpec, PlayerDTO::class.java)

        // Assert
        Assertions.assertEquals(playerSpec.firstName, player.firstName)
        Assertions.assertEquals(playerSpec.lastName, player.lastName)
        Assertions.assertEquals(playerSpec.dateOfBirth, player.dateOfBirth)
        Assertions.assertEquals(playerSpec.club.id, player.club.id)
        Assertions.assertEquals(playerSpec.club.name, player.club.name)
    }

    @Test
    fun createdPlayerShouldHaveAnId(){
        // Setup
        val playerSpec = helper.anyPlayerSpecFor(club)

        // Act
        val player = testRestTemplate.postForObject(baseAddress, playerSpec, PlayerDTO::class.java)

        // Assert
        Assertions.assertTrue(player.id > 0, "Creating a player should assign it an ID")
    }

    @Test
    fun shouldReturnCorrectPlayer(){
        // Setup
        val playerSpec = helper.anyPlayerSpecFor(club)
        val playerOnPost = testRestTemplate.postForObject(baseAddress, playerSpec, PlayerDTO::class.java)

        // Act
        val playerOnGet = testRestTemplate.getForObject(baseAddress + "/${playerOnPost.id}", PlayerDTO::class.java)

        // Assert
        Assertions.assertEquals(playerOnPost.firstName, playerOnGet.firstName)
        Assertions.assertEquals(playerOnPost.lastName, playerOnGet.lastName)
        Assertions.assertEquals(playerOnPost.dateOfBirth, playerOnGet.dateOfBirth)
        Assertions.assertEquals(playerOnPost.id, playerOnGet.id)
        Assertions.assertEquals(playerOnPost.club.id, playerOnGet.club.id)
        Assertions.assertEquals(playerOnPost.club.name, playerOnGet.club.name)
    }

    @Test
    fun shouldReturnNotFoundWhenClubId(){
        // Setup
        val clubThatDoesNotExist = ClubDTO(
            -1,
            "TestClub" + Random.nextLong().toString(),
            "Testroad 12B"
        )
        val badPlayerSpec = helper.anyPlayerSpecFor(clubThatDoesNotExist)

        val request = HttpEntity(badPlayerSpec)

        //Act
        val response = testRestTemplate.exchange<Any>("/player", HttpMethod.POST, request)

        //Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun postingAPlayerSpecWithMissingFieldsShouldReturnHttpBadRequest(){
        // Setup
        val badPlayerSpec = PlayerSpecWithMissingFields(
            "Laban"
        )
        val request = HttpEntity(badPlayerSpec)

        //Act
        val response = testRestTemplate.exchange<Any>("/player", HttpMethod.POST, request)

        //Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun postingAPlayerSpecWithExtraFieldsShouldReturnOk(){
        // Setup
        val badPlayerSpec = PlayerSpecWithExtraFields(
            "Laban",
            "Nilsson",
            "EXTRAFIELD",
            ClubNoAddressDTO(
                club.id!!,
                club.name
            ),
            LocalDate.now().minusMonths(170)

        )
        val request = HttpEntity(badPlayerSpec)

        //Act
        val response = testRestTemplate.exchange<Any>("/player", HttpMethod.POST, request)

        //Assert
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }
}

data class PlayerSpecWithMissingFields (
    val lastName: String
)

data class PlayerSpecWithExtraFields(
    val firstName: String,
    val lastName: String,
    val brothersName: String,
    val club: ClubNoAddressDTO,
    val dateOfBirth: LocalDate
)
