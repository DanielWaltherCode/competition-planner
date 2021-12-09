package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestGetDaysOfCompetition {

    private val getDaysOfCompetition = GetDaysOfCompetition()
    private val dataGenerator = DataGenerator()

    @Test
    fun shouldReturnOneDate() {
        // Setup
        val startDate = LocalDate.now()
        val competition = dataGenerator.newCompetitionDTO(startDate = startDate, endDate = startDate)

        // Act
        val dates = getDaysOfCompetition.execute(competition)

        // Assert
        Assertions.assertEquals(1, dates.size)
        Assertions.assertEquals(listOf(startDate), dates)
    }

    @Test
    fun shouldReturnTwoDates() {
        // Setup
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(1)
        val competition = dataGenerator.newCompetitionDTO(startDate = startDate, endDate = endDate)

        // Act
        val dates = getDaysOfCompetition.execute(competition)

        // Assert
        Assertions.assertEquals(2, dates.size)
        Assertions.assertEquals(listOf(startDate, endDate), dates)
    }

    @Test
    fun shouldReturnTenDates() {
        // Setup
        val startDate = LocalDate.now()
        val endDate = startDate.plusDays(9)
        val competition = dataGenerator.newCompetitionDTO(startDate = startDate, endDate = endDate)

        // Act
        val dates = getDaysOfCompetition.execute(competition)

        // Assert
        Assertions.assertEquals(10, dates.size)
    }
}