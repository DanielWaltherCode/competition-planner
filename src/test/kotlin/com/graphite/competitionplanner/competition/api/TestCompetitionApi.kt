package com.graphite.competitionplanner.competition.api

import com.graphite.competitionplanner.competition.domain.UpdateCompetition
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestCompetitionApi {

    private val service = mock(CompetitionService::class.java)
    private val updateCompetition = mock(UpdateCompetition::class.java)
    private val api = CompetitionApi(service, updateCompetition)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCallServiceWhenAddingCompetition() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()

        // Act
        api.addCompetition(spec)

        // Assert
        verify(service, times(1)).addCompetition(spec)
        verify(service, times(1)).addCompetition(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallServiceWhenUpdatingCompetition() {
        // Setup
        val updateSpec = dataGenerator.newCompetitionUpdateSpec()

        // Act
        api.updateCompetition(1, updateSpec)

        // Assert
        verify(updateCompetition, times(1)).execute(1, updateSpec)
        verify(updateCompetition, times(1)).execute(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallServiceWithNullArguments() {
        // Act
        api.getAll(null, null)

        // Assert
        verify(service, times(1)).getByDate(null, null)
    }

    @Test
    fun shouldCallServiceWithSpecifiedArguments() {
        // Setup
        val start = LocalDate.now()
        val end = start.plusMonths(1)

        // Act
        api.getAll(start, end)

        // Assert
        verify(service, times(1)).getByDate(start, end)
    }

    @Test
    fun shouldCallServiceWhenGettingDates() {
        // Act
        api.getDaysInCompetition(10)

        // Assert
        verify(service, times(1)).getDaysOfCompetition(10)
        verify(service, times(1)).getDaysOfCompetition(anyInt())
    }

    @Test
    fun shouldGetListOfRound() {
        // Act
        val rounds = api.getPossibleRounds()

        // Assert
        val expectedRounds = Round.values()
        val equal = expectedRounds.intersect(rounds.toList()).size == expectedRounds.size
        Assertions.assertTrue(equal)
    }

}