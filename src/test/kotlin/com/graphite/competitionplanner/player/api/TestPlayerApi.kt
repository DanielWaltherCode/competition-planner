package com.graphite.competitionplanner.player.api

import com.graphite.competitionplanner.player.service.PlayerDTO
import com.graphite.competitionplanner.util.AbstractApiTest
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import java.time.LocalDate

class TestPlayerApi(
    @LocalServerPort port: Int,
    @Autowired testRestTemplate: TestRestTemplate,
    @Autowired val playerApi: PlayerApi,
    @Autowired val util: Util
) : AbstractApiTest(
    port,
    testRestTemplate

) {
    override val resource: String = "/player"
    lateinit var addedPlayer: PlayerDTO
    val clubId = util.getClubIdOrDefault("Lugi")

    @BeforeEach
    fun addTestPlayer() {
        val playerString = PlayerSpec(
                "Laban", "Nilsson",
                clubId, LocalDate.now().minusMonths(170)
        )
        addedPlayer = testRestTemplate.postForObject(getUrl(),
            HttpEntity(playerString, getAuthenticationHeaders()), PlayerDTO::class.java)
    }

    @Test
    fun updatePlayer() {
        val newName = "Anthony"

        val updatedPlayer = playerApi.updatePlayer(
                addedPlayer.id,
                PlayerSpec(
                        newName, "Nilsson",
                        clubId, LocalDate.now().minusMonths(170)
                )
        )
        Assertions.assertNotNull(updatedPlayer)
        Assertions.assertEquals(addedPlayer.id, updatedPlayer.id)
    }

    @Test
    fun getPlayersByClub() {
        val players = playerApi.getPlayersByClubId(clubId)
        Assertions.assertNotNull(players)
        Assertions.assertTrue(players.isNotEmpty())
    }

    @Test
    fun getPlayersByPartOfName() {
        var players = playerApi.searchByPartOfName("a")
        Assertions.assertNotNull(players)
        Assertions.assertTrue(players.isNotEmpty())

        // Search for player with z, find nothing, add player, assert is there
        players = playerApi.searchByPartOfName("z")
        Assertions.assertTrue(players.isEmpty())

        val playerWithZ = playerApi.addPlayer(
                PlayerSpec(
                        "Zaban", "Zilsson",
                        clubId, LocalDate.now().minusMonths(170)
                )
        )
        players = playerApi.searchByPartOfName("z")
        Assertions.assertTrue(players.isNotEmpty())
        playerApi.deletePlayer(playerWithZ.id)
    }

    @Test
    fun deletePlayer(){
        // Setup
        val request = HttpEntity<String>(getAuthenticationHeaders())

        //Act
        val response = testRestTemplate.exchange<Any>("/player/-1", HttpMethod.DELETE, request)

        //Assert
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
    }

    @AfterEach
    fun cleanUp() {
        testRestTemplate.delete(getUrl() + "/${addedPlayer.id}", HttpEntity<String>(getAuthenticationHeaders()))
    }

}
