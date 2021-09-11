package com.graphite.competitionplanner.player.api

import com.graphite.competitionplanner.club.api.ClubApi
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.util.AbstractApiTest
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class Player(
    @LocalServerPort port: Int,
    @Autowired testRestTemplate: TestRestTemplate,
    @Autowired val clubApi: ClubApi
) : AbstractApiTest(
    port,
    testRestTemplate
) {
    override val resource: String = "/player"

    val dataGenerator = DataGenerator()

    @Test
    fun canCreateUpdateFindDeletePlayer() {
        // Setup create
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val playerSpec = dataGenerator.newPlayerSpec(clubId = club.id)

        // Act
        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerWithClubDTO::class.java
        )

        // Assert creation
        Assertions.assertEquals(playerSpec.firstName, player.firstName)
        Assertions.assertEquals(playerSpec.lastName, player.lastName)
        Assertions.assertEquals(playerSpec.dateOfBirth, player.dateOfBirth)
        Assertions.assertEquals(club.id, player.club.id)
        Assertions.assertEquals(club.name, player.club.name)

        // Setup update
        val updateSpec = PlayerSpec("MyNewName", "NewLastName", club.id, playerSpec.dateOfBirth)
        val updateRequest = HttpEntity(updateSpec, getAuthenticationHeaders())

        // Act
        val updateResponse =
            testRestTemplate.exchange<PlayerWithClubDTO>(getUrl() + "/${player.id}", HttpMethod.PUT, updateRequest)
        val updatedPlayer = updateResponse.body

        // Assert update
        Assertions.assertEquals(updateSpec.firstName, updatedPlayer!!.firstName)
        Assertions.assertEquals(updateSpec.lastName, updatedPlayer.lastName)
        Assertions.assertEquals(updateSpec.dateOfBirth, updatedPlayer.dateOfBirth)
        Assertions.assertEquals(club.id, updatedPlayer.club.id)
        Assertions.assertEquals(club.name, updatedPlayer.club.name)

        // Setup find by id
        val findByIdRequest = HttpEntity(PlayerWithClubDTO::class.java, getAuthenticationHeaders())
        val foundResponse =
            testRestTemplate.exchange<PlayerWithClubDTO>(getUrl() + "/${player.id}", HttpMethod.GET, findByIdRequest)
        val foundPlayer = foundResponse.body

        Assertions.assertEquals(player.id, foundPlayer!!.id)
        Assertions.assertEquals(updatedPlayer.firstName, foundPlayer.firstName)
        Assertions.assertEquals(updatedPlayer.lastName, foundPlayer.lastName)
        Assertions.assertEquals(updatedPlayer.dateOfBirth, foundPlayer.dateOfBirth)
        Assertions.assertEquals(updatedPlayer.club.id, foundPlayer.club.id)
        Assertions.assertEquals(updatedPlayer.club.name, foundPlayer.club.name)
        Assertions.assertEquals(updatedPlayer.club.address, foundPlayer.club.address)

        // Setup delete
        val deleteRequest = HttpEntity(Boolean, getAuthenticationHeaders())
        val deleteResponse =
            testRestTemplate.exchange<Boolean>(getUrl() + "/${player.id}", HttpMethod.DELETE, deleteRequest)
        val deleted = deleteResponse.body

        Assertions.assertTrue(deleted!!)
    }

    @Test
    fun canFindPlayerByNameSearch() {
        // Setup
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val playerSpec = dataGenerator.newPlayerSpec(clubId = club.id)

        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerWithClubDTO::class.java
        )

        // Setup find by name request
        val findRequest = HttpEntity(HttpEntity.EMPTY, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<List<PlayerWithClubDTO>>(
            getUrl() + "/name-search?partOfName=${player.firstName}",
            HttpMethod.GET,
            findRequest
        )
        val found = response.body

        // Assert
        Assertions.assertTrue(found!!.contains(player))
    }

    @Test
    fun canGetPlayerByClub() {
        // Setup
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val playerSpec = dataGenerator.newPlayerSpec(clubId = club.id)

        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerWithClubDTO::class.java
        )

        // Setup find by club id request
        val findRequest = HttpEntity(HttpEntity.EMPTY, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<List<PlayerWithClubDTO>>(
            getUrl() + "/?clubId=${club.id}",
            HttpMethod.GET,
            findRequest
        )
        val found = response.body

        // Assert
        Assertions.assertTrue(found!!.contains(player))
    }

    @Test
    fun shouldGetHttpNotFoundWhenDeletingPlayerThatDoesNotExist() {
        // Setup
        val request = HttpEntity(Boolean, getAuthenticationHeaders())

        // Act
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${-1}", HttpMethod.DELETE, request)

        // Assertion
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldGetHttpNotFoundWhenUpdatingPlayerThatDoesNotExist() {
        // Setup
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val updateSpec = dataGenerator.newPlayerSpec(clubId = club.id)
        val request = HttpEntity(updateSpec, getAuthenticationHeaders())

        // Act
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${-1}", HttpMethod.PUT, request)

        // Assertion
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldGetHttpNotFoundWhenChangingToNonExistingClub() {
        // Setup Player
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val playerSpec = dataGenerator.newPlayerSpec(clubId = club.id)
        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerWithClubDTO::class.java
        )

        // Setup Update request
        val updateSpec =
            PlayerSpec(player.firstName, player.lastName, -10, player.dateOfBirth)
        val request = HttpEntity(updateSpec, getAuthenticationHeaders())

        // Act
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${player.id}", HttpMethod.PUT, request)

        // Assertion
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldGetHttpNotFoundWhenNotFindingPlayer() {
        // Setup
        val request = HttpEntity(PlayerWithClubDTO::class.java, getAuthenticationHeaders())

        // Act
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${-33}", HttpMethod.GET, request)

        // Assertion
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldGetHttpBadRequestWhenPostingPlayerSpecWithMissingField() {
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
    fun shouldGetHttpBadRequestWhenPlayerSpecFailsValidation() {
        // Setup
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val playerSpec = PlayerSpecForTesting("", "lastName", club.id, LocalDate.now().minusYears(10))
        val request = HttpEntity(playerSpec, getAuthenticationHeaders())

        // Act
        val response = testRestTemplate.exchange<Any>(getUrl(), HttpMethod.POST, request)

        // Assertion
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }
}

data class PlayerSpecForTesting(
    val firstName: String,
    val lastName: String,
    val clubId: Int,
    val dateOfBirth: LocalDate
)

data class PlayerSpecWithMissingFields(
    val lastName: String
)
