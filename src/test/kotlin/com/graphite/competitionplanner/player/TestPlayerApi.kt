package com.graphite.competitionplanner.player

import com.fasterxml.jackson.databind.ObjectMapper
import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.api.PlayerApi
import com.graphite.competitionplanner.api.PlayerSpec
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.service.PlayerService
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfSystemProperty
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.annotation.IfProfileValue
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfSystemProperty(named = "run.integration.test", matches = "true")
class TestPlayerApi(
    @LocalServerPort val port: Int,
    @Autowired val testRestTemplate: TestRestTemplate,
    @Autowired val playerService: PlayerService,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val playerApi: PlayerApi,
    @Autowired val util: Util
) {

    lateinit var addedPlayer: PlayerDTO
    val clubId = util.getClubIdOrDefault("Lugi")
    val mapper = ObjectMapper()
    val baseAddress = "http://localhost:$port/player"

    @BeforeEach
    fun addTestPlayer() {
        val playerString = mapper.writeValueAsString(PlayerSpec(
            "Laban", "Nilsson",
            ClubNoAddressDTO(clubId, null), LocalDate.now().minusMonths(170)
        ))
        addedPlayer = testRestTemplate.postForObject(baseAddress, playerString,  PlayerDTO::class.java)
    }

    @Test
    fun updatePlayer() {
        val newName = "Anthony"

        val updatedPlayer = playerApi.updatePlayer(
            addedPlayer.id,
            PlayerSpec(
                newName, "Nilsson",
                ClubNoAddressDTO(clubId, null), LocalDate.now().minusMonths(170)
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
                ClubNoAddressDTO(clubId, null), LocalDate.now().minusMonths(170)
            )
        )
        players = playerApi.searchByPartOfName("z")
        Assertions.assertTrue(players.isNotEmpty())
        playerApi.deletePlayer(playerWithZ.id)
    }

    @AfterEach
    fun cleanUp() {
        testRestTemplate.delete(baseAddress + "/${addedPlayer.id}")
    }


}