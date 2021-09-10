package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestCompetition {

    val dataGenerator = DataGenerator()

    @Test
    fun startDateMustBeBeforeEndDate() {
        val startDate = LocalDate.now()
        val endDate = startDate.minusDays(1)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Competition(
                0,
                dataGenerator.newLocation(),
                "Svedala",
                "Välkommen",
                startDate,
                endDate
            )
        }
    }

    @Test
    fun startDateCanBeSameAsEndDate() {
        val startDate = LocalDate.now()
        Assertions.assertDoesNotThrow {
            Competition(
                0,
                dataGenerator.newLocation(),
                "Svedala",
                "Välkommen",
                startDate,
                startDate
            )
        }
    }

    @Test
    fun nameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Competition(
                0,
                dataGenerator.newLocation(),
                "",
                "Välkommen",
                LocalDate.of(1999, 10, 18),
                LocalDate.of(1999, 10, 19)
            )
        }
    }

    @Test
    fun nameCannotBeBlank() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Competition(
                0,
                dataGenerator.newLocation(),
                "   ",
                "Välkommen",
                LocalDate.of(1999, 10, 18),
                LocalDate.of(1999, 10, 19)
            )
        }
    }
}