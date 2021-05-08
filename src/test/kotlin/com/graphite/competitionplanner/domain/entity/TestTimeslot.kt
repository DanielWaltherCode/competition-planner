package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestTimeslot {

    private final val dataGenerator = DataGenerator()
    private final val match = dataGenerator.newMatch()

    @Test
    fun matchesCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Timeslot(0, emptyList()) }
    }

    @Test
    fun playerIdsEqualTheIdsFoundInTheMatches() {
        val timeslot = Timeslot(0, listOf(match))

        Assertions.assertEquals(2, timeslot.playerIds.size)
        Assertions.assertTrue(timeslot.playerIds.contains(match.firstPlayer.first().id))
        Assertions.assertTrue(timeslot.playerIds.contains(match.secondPlayer.first().id))
    }
}