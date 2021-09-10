package com.graphite.competitionplanner.player

import com.graphite.competitionplanner.player.api.PlayerSpec
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestPlayerService(@Autowired val playerRepository: PlayerRepository,
                        @Autowired val util: Util) {

    @Test
    fun testAddPlayer() {
        val clubId = util.getClubIdOrDefault("Lugi")
        val originalSize = playerRepository.getPlayersByClub(clubId).size
        val player = playerRepository.addPlayer(
            PlayerSpec("Anders", "Hansson",
                clubId, LocalDate.now().minusMonths(170)
        )
        )
        Assertions.assertNotNull(player.id)
        Assertions.assertEquals(originalSize + 1, playerRepository.getPlayersByClub(clubId).size)
    }

    @Test
    fun testFindByPartOfName() {
        // Add three players with similar names
        val clubId = util.getClubIdOrDefault("Lugi")

        playerRepository.addPlayer(
            PlayerSpec(
                "Aanders", "Hansson",
                clubId, LocalDate.now().minusMonths(170)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                "Hasse", "Andersson",
                clubId, LocalDate.now().minusMonths(170)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                "Aaa", "Haf",
                clubId, LocalDate.now().minusMonths(170)
            )
        )
        val playersWithTwoAs = playerRepository.findPlayersByPartOfName("aa")
        Assertions.assertEquals(2, playersWithTwoAs?.size)

        val playersWithThreeAs = playerRepository.findPlayersByPartOfName("aaa")
        Assertions.assertEquals(1, playersWithThreeAs?.size)

        val playersWithOneA = playerRepository.findPlayersByPartOfName("a")
        print(playersWithOneA)

    }
}