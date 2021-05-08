package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class TestTimeslot {

    private final val club = Club(0, "Luleå IK", "Sommarvägen 12")
    private final val p1 = Player(2, "Ida", "Larsson", club, LocalDate.of(1999, 1, 1))
    private final val p2 = Player(5, "Lisa", "Åkerman", club, LocalDate.of(1999, 7, 1))
    private final val match = Match(
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

    @Test
    fun matchesCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Timeslot(0, emptyList()) }
    }

    @Test
    fun playerIdsEqualTheIdsFoundInTheMatches() {
        val timeslot = Timeslot(0, listOf(match))

        Assertions.assertEquals(2, timeslot.playerIds.size)
        Assertions.assertTrue(timeslot.playerIds.contains(p1.id))
        Assertions.assertTrue(timeslot.playerIds.contains(p2.id))
    }
}