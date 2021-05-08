package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class TestMatch {

    private final val club = Club(0, "Luleå IK", "Sommarvägen 12")
    private final val p1 = Player(0, "Ida", "Larsson", club, LocalDate.of(1999, 1, 1))
    private final val p2 = Player(0, "Lisa", "Åkerman", club, LocalDate.of(1999, 7, 1))

    @Test
    fun startTimeCannotBeAfterEndTime() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Match(
                0,
                LocalDateTime.now(),
                LocalDateTime.now().minusMinutes(10),
                CompetitionCategory(0),
                MatchType("POOL"),
                listOf(p1),
                listOf(p2),
                0,
                "Group A"
            )
        }
    }

    @Test
    fun playerCannotBelongToBothTeamsInSameMatch() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Match(
                0,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                CompetitionCategory(0),
                MatchType("POOL"),
                listOf(p2),
                listOf(p2),
                0,
                "Group A"
            )
        }
    }
}