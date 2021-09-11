package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestCompetitionSpec {

    val dataGenerator = DataGenerator()

    @Test
    fun startDateMustBeBeforeEndDate() {
        val startDate = LocalDate.now()
        val endDate = startDate.minusDays(1)
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CompetitionSpec(
                dataGenerator.newLocationSpec(),
                "Svedala",
                "Välkommen",
                0,
                startDate,
                endDate
            )
        }
    }

    @Test
    fun startDateCanBeSameAsEndDate() {
        val startDate = LocalDate.now()
        Assertions.assertDoesNotThrow {
            CompetitionSpec(
                dataGenerator.newLocationSpec(),
                "Svedala",
                "Välkommen",
                0,
                startDate,
                startDate
            )
        }
    }

    @Test
    fun nameCannotBeEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CompetitionSpec(
                dataGenerator.newLocationSpec(),
                "",
                "Välkommen",
                0,
                LocalDate.of(1999, 10, 18),
                LocalDate.of(1999, 10, 19)
            )
        }
    }

    @Test
    fun nameCannotBeBlank() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CompetitionSpec(
                dataGenerator.newLocationSpec(),
                "   ",
                "Välkommen",
                0,
                LocalDate.of(1999, 10, 18),
                LocalDate.of(1999, 10, 19)
            )
        }
    }
}