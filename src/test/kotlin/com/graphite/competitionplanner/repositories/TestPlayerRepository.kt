package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.NewPlayerDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.domain.interfaces.IPlayerRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestPlayerRepository(
    @Autowired val playerRepository: IPlayerRepository,
    @Autowired val clubRepository: IClubRepository
) {
    lateinit var club: ClubDTO

    @BeforeEach
    fun saveAClub() {
        val dto = ClubDTO(0, "Fake Club", "Fake Address")
        club = clubRepository.store(dto)
    }

    @AfterEach
    fun deleteClub() {
        clubRepository.delete(club)
    }

    @Test
    fun shouldSetIdWhenSaving() {
        val dto = NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now())
        val player = playerRepository.store(dto)

        Assertions.assertTrue(player.id != 0)

        playerRepository.delete(player)
    }

    @Test
    fun shouldGetCorrectNumberOfPlayersPerClub() {
        // Setup
        val players = listOf(
            NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now()),
            NewPlayerDTO("Nils", "Nilsson", club.id, LocalDate.now()),
            NewPlayerDTO("Simon", "Nilsson", club.id, LocalDate.now())
        )

        val storedPlayers = players.map { playerRepository.store(it) }

        // Act
        val playersInClub = playerRepository.playersInClub(club)

        Assertions.assertEquals(players.size, playersInClub.size)

        // Clean up
        storedPlayers.map { playerRepository.delete(it) }
    }

    @Test
    fun shouldGetBackTheCorrectClub() {
        // Setup
        val players = listOf(
            NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now()),
            NewPlayerDTO("Nils", "Nilsson", club.id, LocalDate.now()),
            NewPlayerDTO("Simon", "Nilsson", club.id, LocalDate.now())
        )

        val storedPlayers = players.map { playerRepository.store(it) }

        // Act
        val playersInClub = playerRepository.playersInClub(club)

        for (player in playersInClub) {
            Assertions.assertEquals(player.club, club)
        }

        // Clean up
        storedPlayers.map { playerRepository.delete(it) }
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenUpdatingPlayerThatCannotBeFound() {
        val dto = PlayerDTO(-1, "Lasse", "Nilsson", club.id, LocalDate.now())
        Assertions.assertThrows(NotFoundException::class.java) { playerRepository.update(dto) }
    }

    @Test
    fun shouldUpdatePlayerDto() {
        // Setup
        val dto = NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now())
        val saved = playerRepository.store(dto)
        val dtoWithNewName = PlayerDTO(saved.id, "MyNewName", saved.lastName, club.id, saved.dateOfBirth)

        // Act
        val updated = playerRepository.update(dtoWithNewName)

        // Assert
        Assertions.assertEquals(dtoWithNewName, updated)

        // Clean up
        playerRepository.delete(updated)
    }

    @Test
    fun shouldThrowNotFoundExceptionIfPlayerCannotBeFound() {
        Assertions.assertThrows(NotFoundException::class.java) { playerRepository.findById(-12) }
    }

    @Test
    fun shouldReturnPlayerFound() {
        // Setup
        val dto = NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now())
        val player = playerRepository.store(dto)

        // Act
        val found = playerRepository.findById(player.id)

        // Assertions
        Assertions.assertEquals(player.id, found.id)
        Assertions.assertEquals(player.firstName, found.firstName)
        Assertions.assertEquals(player.lastName, found.lastName)
        Assertions.assertEquals(player.clubId, found.clubId)
        Assertions.assertEquals(player.dateOfBirth, found.dateOfBirth)

        // Clean up
        playerRepository.delete(player)
    }

    @Test
    fun shouldThrowNotFoundExceptionWhenDeletingPlayerThatCannotBeFound() {
        Assertions.assertThrows(NotFoundException::class.java) {
            playerRepository.delete(PlayerDTO(-10, "firstname", "Lastname", 12, LocalDate.now()))
        }
    }

    @Test
    fun shouldReturnTheDeletedDto() {
        // Setup
        val dto = NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now())
        val player = playerRepository.store(dto)

        // Act
        val deletedPlayer = playerRepository.delete(player)

        // Assertions
        Assertions.assertEquals(player, deletedPlayer)
    }

    @Test
    fun shouldNotBeAbleToFindDeletedPlayer() {
        // Setup
        val dto = NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now())
        val player = playerRepository.store(dto)

        // Act
        val deletedPlayer = playerRepository.delete(player)

        // Assertions
        Assertions.assertThrows(NotFoundException::class.java) { playerRepository.findById(deletedPlayer.id) }
    }

    @Test
    fun shouldReturnPlayerThatStartWithName() {
        // Setup
        val lasseDto = NewPlayerDTO("Lasse", "Nilsson", club.id, LocalDate.now())
        val lasse = playerRepository.store(lasseDto)

        val lassDto = NewPlayerDTO("Lass", "Nilsson", club.id, LocalDate.now())
        val lass = playerRepository.store(lassDto)

        val lassonDto = NewPlayerDTO("Karl", "Lasson", club.id, LocalDate.now())
        val lasson = playerRepository.store(lassonDto)

        val klasDto = NewPlayerDTO("Klas", "Klasson", club.id, LocalDate.now())
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
        playerRepository.delete(lasse)
        playerRepository.delete(lass)
        playerRepository.delete(lasson)
        playerRepository.delete(klas)
    }

}