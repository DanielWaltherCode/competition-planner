package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@SpringBootTest
class TestMatch {

    private final val dataGenerator = DataGenerator()
    private final val p2 = dataGenerator.newPlayer()

    @Test
    fun playerCannotBelongToBothTeamsInSameMatch() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Match(
                0,
                CompetitionCategory(0),
                null,
                null,
                MatchType("POOL"),
                listOf(p2.id),
                listOf(p2.id),
                0,
                "Group A"
            )
        }
    }

    @Test
    fun startTimeMustBeBeforeEndTime() {
        val startTime = LocalDateTime.now()
        val endTime = startTime.minusSeconds(10)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Match(
                0,
                CompetitionCategory(0),
                startTime,
                endTime,
                MatchType("POOL"),
                listOf(p2.id),
                listOf(p2.id),
                0,
                "Group A"
            )
        }
    }
}