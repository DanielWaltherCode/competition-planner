package com.graphite.competitionplanner.player.repository

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import io.jsonwebtoken.lang.Assert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPlayerRepository(
    @Autowired val playerRepository: IPlayerRepository,
    @Autowired val clubRepository: IClubRepository
) {
    lateinit var club: ClubDTO
    val dataGenerator = DataGenerator()

    @BeforeEach
    fun saveAClub() {
        val spec = dataGenerator.newClubSpec()
        club = clubRepository.store(spec)
    }

    @Test
    fun shouldSetIdWhenSaving() {
        val spec = dataGenerator.newPlayerSpec(clubId = club.id)
        val player = playerRepository.store(spec)

        Assertions.assertTrue(player.id != 0)

        playerRepository.delete(player.id)
    }

    @Test
    fun shouldGetCorrectNumberOfPlayersPerClub() {
        // Setup
        val players = listOf(
            dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id),
            dataGenerator.newPlayerSpec("Nils", "Nilsson", club.id),
            dataGenerator.newPlayerSpec("Simon", "Nilsson", club.id)
        )
        players.map { playerRepository.store(it) }

        // Act
        val playersInClub = playerRepository.playersInClub(club.id)

        // Assert
        Assertions.assertEquals(players.size, playersInClub.size)
    }

    @Test
    fun shouldGetBackTheCorrectClub() {
        // Setup
        val players = listOf(
            dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id),
            dataGenerator.newPlayerSpec("Nils", "Nilsson", club.id),
            dataGenerator.newPlayerSpec("Simon", "Nilsson", club.id)
        )
        players.map { playerRepository.store(it) }

        // Act
        val playersInClub = playerRepository.playersInClub(club.id)

        // Assert
        for (player in playersInClub) {
            Assertions.assertEquals(player.club, club)
        }
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenUpdatingPlayerThatCannotBeFound() {
        val spec = dataGenerator.newPlayerSpec()
        Assertions.assertThrows(NotFoundException::class.java) { playerRepository.update(-45461, spec) }
    }

    @Test
    fun shouldUpdatePlayerDto() {
        // Setup
        val spec = dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id)
        val player = playerRepository.store(spec)
        val updateSpec = dataGenerator.newPlayerSpec("MyNewName", player.lastName, club.id, player.dateOfBirth)

        // Act
        val updated = playerRepository.update(player.id, updateSpec)

        // Assert
        Assertions.assertEquals(updateSpec.firstName, updated.firstName)
        Assertions.assertEquals(updateSpec.lastName, updated.lastName)
        Assertions.assertEquals(updateSpec.clubId, updated.clubId)
        Assertions.assertEquals(updateSpec.dateOfBirth, updated.dateOfBirth)
        Assertions.assertEquals(player.id, updated.id)
    }

    @Test
    fun shouldThrowNotFoundExceptionIfPlayerCannotBeFound() {
        Assertions.assertThrows(NotFoundException::class.java) { playerRepository.findById(-12) }
    }

    @Test
    fun shouldReturnPlayerFound() {
        // Setup
        val spec = dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id)
        val player = playerRepository.store(spec)

        // Act
        val found = playerRepository.findById(player.id)

        // Assert
        Assertions.assertEquals(player.id, found.id)
        Assertions.assertEquals(player.firstName, found.firstName)
        Assertions.assertEquals(player.lastName, found.lastName)
        Assertions.assertEquals(player.clubId, found.clubId)
        Assertions.assertEquals(player.dateOfBirth, found.dateOfBirth)
    }

    @Test
    fun shouldIgnorePlayerIdsThatDoesNotExist() {
        // Act
        val result = playerRepository.findAllForIds(listOf(-8901, -6573))

        // Assert
        Assert.isTrue(result.isEmpty())
    }

    @Test
    fun shouldReturnAllPlayersWithGivenIds() {
        // Setup
        val player1 = playerRepository.store(dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id))
        val player2 = playerRepository.store(dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id))

        // Act
        val players = playerRepository.findAllForIds(listOf(player1.id, player2.id))

        // Assert
        val playerIds = players.map { it.id }
        Assertions.assertTrue(playerIds.size == 2)
        Assertions.assertTrue(playerIds.contains(player1.id))
        Assertions.assertTrue(playerIds.contains(player2.id))
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenDeletingPlayerThatCannotBeFound() {
        Assertions.assertThrows(NotFoundException::class.java) {
            playerRepository.delete(-10)
        }
    }

    @Test
    fun shouldReturnTheDeletedDto() {
        // Setup
        val spec = dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id)
        val player = playerRepository.store(spec)

        // Act
        val deletedPlayer = playerRepository.delete(player.id)

        // Assertions
        Assertions.assertEquals(player, deletedPlayer)
    }

    @Test
    fun shouldNotBeAbleToFindDeletedPlayer() {
        // Setup
        val spec = dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id)
        val player = playerRepository.store(spec)

        // Act
        val deletedPlayer = playerRepository.delete(player.id)

        // Assert
        Assertions.assertThrows(NotFoundException::class.java) { playerRepository.findById(deletedPlayer.id) }
    }

    @Test
    fun shouldReturnPlayersWhoseFullNameStartWithName() {
        // Setup
        playerRepository.store(dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id))
        playerRepository.store(dataGenerator.newPlayerSpec("Lass", "Nilsson", club.id))
        playerRepository.store(dataGenerator.newPlayerSpec("Karl", "Lasson", club.id))
        playerRepository.store(dataGenerator.newPlayerSpec("Klas", "Klasson", club.id))

        // Act
        val matchingPlayers = playerRepository.findByName("las")

        // Assert
        Assertions.assertTrue(matchingPlayers.isNotEmpty(), "Did not expect to get an empty result back.")
        Assertions.assertTrue(matchingPlayers.all { it.firstName.startsWith("las", ignoreCase = true) },
            "At least one player does not have a name that begins with \"las\"")
    }

}