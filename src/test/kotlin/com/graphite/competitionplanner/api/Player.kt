package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.AbstractApiTest
import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.domain.dto.PlayerEntityDTO
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
        val playerSpec = dataGenerator.newPlayerSpec(club.toNoAddressDTO())

        // Act
        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerEntityDTO::class.java
        )

        // Assert creation
        Assertions.assertEquals(playerSpec.firstName, player.firstName)
        Assertions.assertEquals(playerSpec.lastName, player.lastName)
        Assertions.assertEquals(playerSpec.dateOfBirth, player.dateOfBirth)
        Assertions.assertEquals(playerSpec.club.id, player.club.id)
        Assertions.assertEquals(playerSpec.club.name, player.club.name)

        // Setup update
        val updateSpec = PlayerSpec("MyNewName", "NewLastName", club.toNoAddressDTO(), playerSpec.dateOfBirth)
        val updateRequest = HttpEntity(updateSpec, getAuthenticationHeaders())

        // Act
        val updateResponse =
            testRestTemplate.exchange<PlayerEntityDTO>(getUrl() + "/${player.id}", HttpMethod.PUT, updateRequest)
        val updatedPlayer = updateResponse.body

        // Assert update
        Assertions.assertEquals(updateSpec.firstName, updatedPlayer!!.firstName)
        Assertions.assertEquals(updateSpec.lastName, updatedPlayer.lastName)
        Assertions.assertEquals(updateSpec.dateOfBirth, updatedPlayer.dateOfBirth)
        Assertions.assertEquals(updateSpec.club.id, updatedPlayer.club.id)
        Assertions.assertEquals(updateSpec.club.name, updatedPlayer.club.name)

        // Setup find by id
        val findByIdRequest = HttpEntity(PlayerEntityDTO::class.java, getAuthenticationHeaders())
        val foundResponse =
            testRestTemplate.exchange<PlayerEntityDTO>(getUrl() + "/${player.id}", HttpMethod.GET, findByIdRequest)
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
        val playerSpec = dataGenerator.newPlayerSpec(club.toNoAddressDTO())

        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerEntityDTO::class.java
        )

        // Setup find by name request
        val findRequest = HttpEntity(HttpEntity.EMPTY, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<List<PlayerEntityDTO>>(
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
        val playerSpec = dataGenerator.newPlayerSpec(club.toNoAddressDTO())

        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerEntityDTO::class.java
        )

        // Setup find by club id request
        val findRequest = HttpEntity(HttpEntity.EMPTY, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<List<PlayerEntityDTO>>(
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
        val updateSpec = dataGenerator.newPlayerSpec(club.toNoAddressDTO())
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
        val playerSpec = dataGenerator.newPlayerSpec(club.toNoAddressDTO())
        val player = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(playerSpec, getAuthenticationHeaders()),
            PlayerEntityDTO::class.java
        )

        // Setup Update request
        val updateSpec =
            PlayerSpec(player.firstName, player.lastName, ClubNoAddressDTO(-10, "ClubA123"), player.dateOfBirth)
        val request = HttpEntity(updateSpec, getAuthenticationHeaders())

        // Act
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${player.id}", HttpMethod.PUT, request)

        // Assertion
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldGetHttpNotFoundWhenNotFindingPlayer() {
        // Setup
        val request = HttpEntity(PlayerEntityDTO::class.java, getAuthenticationHeaders())

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
    fun shouldGetHttpOkWhenPostingPlayerSpecWithExtraFields() {
        // Setup
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val badPlayerSpec = PlayerSpecWithExtraFields(
            "Laban",
            "Nilsson",
            "EXTRAFIELD",
            ClubNoAddressDTO(
                club.id,
                club.name
            ),
            LocalDate.now().minusMonths(170)

        )
        val request = HttpEntity(badPlayerSpec, getAuthenticationHeaders())

        //Act
        val response = testRestTemplate.exchange<Any>("/player", HttpMethod.POST, request)

        //Assert
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun shouldGetHttpBadRequestWhenPlayerSpecFailsValidation() {
        // Setup
        val club = clubApi.addClub(dataGenerator.newClubSpec())
        val playerSpec = PlayerSpec("", "lastName", club.toNoAddressDTO(), LocalDate.now().minusYears(10))
        val request = HttpEntity(playerSpec, getAuthenticationHeaders())

        // Act
        val response = testRestTemplate.exchange<Any>(getUrl(), HttpMethod.POST, request)

        // Assertion
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    private fun ClubSpec.toNoAddressDTO(): ClubNoAddressDTO {
        return ClubNoAddressDTO(this.id, this.name)
    }
}

data class PlayerSpecWithMissingFields(
    val lastName: String
)

data class PlayerSpecWithExtraFields(
    val firstName: String,
    val lastName: String,
    val brothersName: String,
    val club: ClubNoAddressDTO,
    val dateOfBirth: LocalDate
)