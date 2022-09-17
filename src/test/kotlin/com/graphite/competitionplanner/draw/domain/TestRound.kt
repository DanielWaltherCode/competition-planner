package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.Round
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestRound {

    @Test
    fun enumerationIsInCorrectOrder() {
        // Act
        val rounds = Round.values()

        // Assert
        Assertions.assertEquals(8, rounds.size)
        Assertions.assertTrue(rounds[0] == Round.FINAL, "Breaking change. Order has changed!")
        Assertions.assertTrue(rounds[1] == Round.SEMI_FINAL, "Breaking change. Order has changed!")
        Assertions.assertTrue(rounds[2] == Round.QUARTER_FINAL, "Breaking change. Order has changed!")
        Assertions.assertTrue(rounds[3] == Round.ROUND_OF_16, "Breaking change. Order has changed!")
        Assertions.assertTrue(rounds[4] == Round.ROUND_OF_32, "Breaking change. Order has changed!")
        Assertions.assertTrue(rounds[5] == Round.ROUND_OF_64, "Breaking change. Order has changed!")
        Assertions.assertTrue(rounds[6] == Round.ROUND_OF_128, "Breaking change. Order has changed!")
        Assertions.assertTrue(rounds[7] == Round.UNKNOWN, "Breaking change. Order has changed!")
    }
}