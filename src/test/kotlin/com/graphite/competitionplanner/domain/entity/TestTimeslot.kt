package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.schedule.domain.entity.Timeslot
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestTimeslot {

    private val dataGenerator = DataGenerator()
    private val match = dataGenerator.newMatch()

    @Test
    fun matchesCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Timeslot(0, emptyList()) }
    }

    @Test
    fun playerIdsEqualTheIdsFoundInTheMatches() {
        val timeslot = Timeslot(0, listOf(match))

        Assertions.assertEquals(2, timeslot.playerIds.size)
        Assertions.assertTrue(timeslot.playerIds.contains(match.firstTeamPlayerIds.first()))
        Assertions.assertTrue(timeslot.playerIds.contains(match.secondTeamPlayerIds.first()))
    }
}