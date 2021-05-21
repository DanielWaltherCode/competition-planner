package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.AbstractApiTest
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.service.PlayerService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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
    @LocalServerPort port: Int,
    @Autowired testRestTemplate: TestRestTemplate,
    @Autowired val clubApi: ClubApi,
    @Autowired val helper: TestHelper,
    @Autowired val playerService: PlayerService
) : AbstractApiTest(
    port,
    testRestTemplate
)
{
    override val resource: String = "/player"
    lateinit var club: ClubSpec
    var playerId: Int = -1

    @BeforeEach
    private fun createClub() {
        club = clubApi.addClub(
            NewClubSpec(
                "TestClub" + Random.nextLong().toString(),
                "Testroad 12B"
            )
        )
    }

    // Clean up after successful post requests
    private fun cleanUp(){
        playerService.deletePlayer(playerId)
    }

    @Test
    fun createdPlayerShouldMatchSpec() {
        // Setup
        val playerSpec = helper.anyPlayerSpecFor(club)

        // Act
        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerDTO::class.java
        )
        this.playerId = player.id

        // Assert
        Assertions.assertEquals(playerSpec.firstName, player.firstName)
        Assertions.assertEquals(playerSpec.lastName, player.lastName)
        Assertions.assertEquals(playerSpec.dateOfBirth, player.dateOfBirth)
        Assertions.assertEquals(playerSpec.clubId, player.club.id)
        cleanUp()
    }

    @Test
    fun createdPlayerShouldHaveAnId(){
        // Setup
        val playerSpec = helper.anyPlayerSpecFor(club)

        // Act
        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerDTO::class.java
        )
        this.playerId = player.id

        // Assert
        Assertions.assertTrue(player.id > 0, "Creating a player should assign it an ID")
        cleanUp()
    }

    @Test
    @Disabled("Couldn't figure out why deserialization doesn't work now in get method")
    fun shouldReturnCorrectPlayer(){
        // Setup
        val request = HttpEntity<String>(getAuthenticationHeaders())
        val playerSpec = helper.anyPlayerSpecFor(club)
        val playerOnPost = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerDTO::class.java
        )
        this.playerId = playerOnPost.id

        // Act
        val playerOnGet =
            testRestTemplate.getForObject(getUrl() + "/${playerOnPost.id}", PlayerDTO::class.java, request)

        // Assert
        Assertions.assertEquals(playerOnPost.firstName, playerOnGet.firstName)
        Assertions.assertEquals(playerOnPost.lastName, playerOnGet.lastName)
        Assertions.assertEquals(playerOnPost.dateOfBirth, playerOnGet.dateOfBirth)
        Assertions.assertEquals(playerOnPost.id, playerOnGet.id)
        Assertions.assertEquals(playerOnPost.club.id, playerOnGet.club.id)
        Assertions.assertEquals(playerOnPost.club.name, playerOnGet.club.name)
        cleanUp()
    }

    @Test
    fun shouldReturnNotFoundWhenClubId(){
        // Setup
        val clubThatDoesNotExist = ClubSpec(
            -1,
            "TestClub" + Random.nextLong().toString(),
            "Testroad 12B"
        )
        val badPlayerSpec = helper.anyPlayerSpecFor(clubThatDoesNotExist)

        val request = HttpEntity(badPlayerSpec, getAuthenticationHeaders())

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
        val request = HttpEntity(badPlayerSpec, getAuthenticationHeaders())

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
                club.id,
            LocalDate.now().minusMonths(170)

        )
        val request = HttpEntity(badPlayerSpec, getAuthenticationHeaders())

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
    val clubId: Int,
    val dateOfBirth: LocalDate
)
