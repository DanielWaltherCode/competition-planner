package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestPlayerRepository(
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val clubRepository: ClubRepository
) {
    lateinit var club: ClubDTO

    @BeforeEach
    fun saveAClub() {
        val dto = ClubDTO(0, "Fake Clu", "Fake Address")
        club = clubRepository.store(dto)
    }

    @AfterEach
    fun deleteClub() {
        clubRepository.delete(club)
    }

    @Test
    fun shouldSetIdWhenSaving() {
        val dto = PlayerDTO(0, "Lasse", "Nilsson", club, LocalDate.now())
        val player = playerRepository.store(dto)

        Assertions.assertTrue(player.id != 0)

        playerRepository.deletePlayer(player.id)
    }

    @Test
    fun shouldGetCorrectNumberOfPlayersPerClub() {
        // Setup
        val players = listOf<PlayerDTO>(
            PlayerDTO(0, "Lasse", "Nilsson", club, LocalDate.now()),
            PlayerDTO(0, "Nils", "Nilsson", club, LocalDate.now()),
            PlayerDTO(0, "Simon", "Nilsson", club, LocalDate.now())
        )

        val storedPlayers = players.map { playerRepository.store(it) }

        // Act
        val playersInClub = playerRepository.playersInClub(club)

        Assertions.assertEquals(players.size, playersInClub.size)

        // Clean up
        storedPlayers.map { playerRepository.deletePlayer(it.id) }
    }
}