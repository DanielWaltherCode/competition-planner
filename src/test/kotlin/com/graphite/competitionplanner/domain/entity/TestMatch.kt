package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@SpringBootTest
class TestMatch {

    private final val dataGenerator = DataGenerator()
    private final val p1 = dataGenerator.newPlayer()
    private final val p2 = dataGenerator.newPlayer()

    @Test
    fun playerCannotBelongToBothTeamsInSameMatch() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Match(
                0,
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