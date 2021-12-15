package com.graphite.competitionplanner.player.repository

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.util.DataGenerator
import io.jsonwebtoken.lang.Assert
import org.junit.jupiter.api.AfterEach
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
        val spec = dataGenerator.newClubSpec("Fake Club", "Fake Address")
        club = clubRepository.store(spec)
    }

    @AfterEach
    fun deleteClub() {
        clubRepository.delete(club.id)
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

        val storedPlayers = players.map { playerRepository.store(it) }

        // Act
        val playersInClub = playerRepository.playersInClub(club.id)

        Assertions.assertEquals(players.size, playersInClub.size)

        // Clean up
        storedPlayers.map { playerRepository.delete(it.id) }
    }

    @Test
    fun shouldGetBackTheCorrectClub() {
        // Setup
        val players = listOf(
            dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id),
            dataGenerator.newPlayerSpec("Nils", "Nilsson", club.id),
            dataGenerator.newPlayerSpec("Simon", "Nilsson", club.id)
        )

        val storedPlayers = players.map { playerRepository.store(it) }

        // Act
        val playersInClub = playerRepository.playersInClub(club.id)

        for (player in playersInClub) {
            Assertions.assertEquals(player.club, club)
        }

        // Clean up
        storedPlayers.map { playerRepository.delete(it.id) }
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

        // Clean up
        playerRepository.delete(updated.id)
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

        // Assertions
        Assertions.assertEquals(player.id, found.id)
        Assertions.assertEquals(player.firstName, found.firstName)
        Assertions.assertEquals(player.lastName, found.lastName)
        Assertions.assertEquals(player.clubId, found.clubId)
        Assertions.assertEquals(player.dateOfBirth, found.dateOfBirth)

        // Clean up
        playerRepository.delete(player.id)
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

        // Clean up
        playerRepository.delete(player1.id)
        playerRepository.delete(player2.id)
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

        // Assertions
        Assertions.assertThrows(NotFoundException::class.java) { playerRepository.findById(deletedPlayer.id) }
    }

    @Test
    fun shouldReturnPlayerThatStartWithName() {
        // Setup
        val lasseSpec = dataGenerator.newPlayerSpec("Lasse", "Nilsson", club.id)
        val lasse = playerRepository.store(lasseSpec)

        val lassSpec = dataGenerator.newPlayerSpec("Lass", "Nilsson", club.id)
        val lass = playerRepository.store(lassSpec)

        val lassonSpec = dataGenerator.newPlayerSpec("Karl", "Lasson", club.id)
        val lasson = playerRepository.store(lassonSpec)

        val klasDto = dataGenerator.newPlayerSpec("Klas", "Klasson", club.id)
        val klas = playerRepository.store(klasDto)

        // Act
        val matchingPlayers = playerRepository.findByName("las")
        val matchingPlayerIds = matchingPlayers.map { it.id }

        // Assertions
        Assertions.assertTrue(matchingPlayerIds.contains(lasse.id))
        Assertions.assertTrue(matchingPlayerIds.contains(lass.id))
        Assertions.assertTrue(matchingPlayerIds.contains(lasson.id))
        Assertions.assertFalse(matchingPlayerIds.contains(klas.id))

        // Clean up
        playerRepository.delete(lasse.id)
        playerRepository.delete(lass.id)
        playerRepository.delete(lasson.id)
        playerRepository.delete(klas.id)
    }

}