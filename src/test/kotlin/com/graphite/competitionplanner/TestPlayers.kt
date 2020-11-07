package com.graphite.competitionplanner

import com.graphite.competitionplanner.api.PlayerDTO
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.tables.pojos.Club
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestPlayers(@Autowired val playerRepository: PlayerRepository,
                  @Autowired val util: Util) {

    @Test
    fun testAddPlayer() {
        val originalSize = playerRepository.getPlayers().size
        val clubId = util.getClubIdOrDefault("Lugi")
        val player = playerRepository.addPlayer(PlayerDTO(
                null, "Anders", "Hansson",
                clubId, LocalDate.now().minusMonths(170)
        ))
        Assertions.assertNotNull(player.id)
        Assertions.assertEquals(originalSize + 1, playerRepository.getPlayers().size)
    }

    @Test
    fun testFindByPartOfName() {
        // Add three players with similar names
        val clubId = util.getClubIdOrDefault("Lugi")

        playerRepository.addPlayer(PlayerDTO(
                null, "Aanders", "Hansson",
                clubId, LocalDate.now().minusMonths(170)
        ))
        playerRepository.addPlayer(PlayerDTO(
                null, "Hasse", "Andersson",
                clubId, LocalDate.now().minusMonths(170)
        ))
        playerRepository.addPlayer(PlayerDTO(
                null, "Aaa", "Haf",
                clubId, LocalDate.now().minusMonths(170)
        ))
        val playersWithTwoAs = playerRepository.findPlayersByPartOfName("aa")
        Assertions.assertEquals(2, playersWithTwoAs?.size)

        val playersWithThreeAs = playerRepository.findPlayersByPartOfName("aaa")
        Assertions.assertEquals(1, playersWithThreeAs?.size)

        val playersWithOneA = playerRepository.findPlayersByPartOfName("a")
        print(playersWithOneA)

    }
}