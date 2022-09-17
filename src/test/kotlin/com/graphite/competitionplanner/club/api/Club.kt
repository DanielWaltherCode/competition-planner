package com.graphite.competitionplanner.club.api

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.util.AbstractApiTest
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
    fun weCanCreateUpdateFindAndDeleteClub() {
        // Setup creation
        val clubSpec = ClubSpec("SuperClub", "SuperAddress")

        // Act
        val club = testRestTemplate.postForObject(
            getUrl(),
            HttpEntity(clubSpec, getAuthenticationHeaders()),
            ClubDTO::class.java
        )

        // Assert creation
        Assertions.assertTrue(club.id != 0)
        Assertions.assertEquals(clubSpec.name, club.name)
        Assertions.assertEquals(clubSpec.address, club.address)

        // Setup update
        val updateSpec = ClubSpec("SuperClub2", "SuperAddress2")
        val updateRequest = HttpEntity(updateSpec, getAuthenticationHeaders())

        // Act
        val updateResponse = testRestTemplate.exchange<ClubDTO>(getUrl() + "/${club.id}", HttpMethod.PUT, updateRequest)
        val updatedClub = updateResponse.body

        // Assert update
        Assertions.assertEquals(club.id, updatedClub!!.id)
        Assertions.assertEquals(updateSpec.name, updatedClub.name)
        Assertions.assertEquals(updateSpec.address, updatedClub.address)

        // Setup find by id
        val findByIdRequest = HttpEntity(ClubDTO::class.java, getAuthenticationHeaders())
        val foundResponse =
            testRestTemplate.exchange<ClubDTO>(getUrl() + "/${club.id}", HttpMethod.GET, findByIdRequest)
        val foundClub = foundResponse.body

        Assertions.assertEquals(club.id, foundClub!!.id)
        Assertions.assertEquals(updateSpec.name, foundClub.name)
        Assertions.assertEquals(updateSpec.address, foundClub.address)

        // Setup get all
        val getAllRequest = HttpEntity<List<ClubDTO>>(getAuthenticationHeaders())
        val getAllResponse = testRestTemplate.exchange<List<ClubDTO>>(getUrl(), HttpMethod.GET, getAllRequest)
        val clubs = getAllResponse.body

        // Assert that our newly created club is part of the collection
        Assertions.assertTrue(clubs!!.any { it.id == club.id })

        // Setup delete
        val deleteRequest = HttpEntity(Boolean, getAuthenticationHeaders())
        val deleteResponse =
            testRestTemplate.exchange<Boolean>(getUrl() + "/${club.id}", HttpMethod.DELETE, deleteRequest)
        val deleted = deleteResponse.body

        Assertions.assertTrue(deleted!!)
    }

    @Test
    fun shouldGetHttpNotFoundWhenDeletingClubThatDoesNotExist() {
        val request = HttpEntity(Boolean, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${-1}", HttpMethod.DELETE, request)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldGetHttpNotFoundWhenUpdatingClubThatDoesNotExist() {
        val updateSpec = ClubSpec("SuperClub2", "SuperAddress2")
        val request = HttpEntity(updateSpec, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${-13}", HttpMethod.PUT, request)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @Test
    fun shouldGetHttpNotFoundWhenNotFindingClub() {
        val request = HttpEntity(ClubSpec::class.java, getAuthenticationHeaders())
        val response = testRestTemplate.exchange<Any>(getUrl() + "/${-1}", HttpMethod.GET, request)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }
}