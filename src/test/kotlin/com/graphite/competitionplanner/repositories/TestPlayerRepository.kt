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
        club = clubRepository.store(dto);
    }

    @AfterEach
    fun deleteClub() {
        clubRepository.deleteClub(club.id)
    }

    @Test
    fun shouldSetIdWhenSaving() {
        val dto = PlayerDTO(0, "Lasse", "Nilsson", club, LocalDate.now())
        val player = playerRepository.store(dto);

        Assertions.assertTrue(player.id != 0)

        playerRepository.deletePlayer(player.id);
    }
}