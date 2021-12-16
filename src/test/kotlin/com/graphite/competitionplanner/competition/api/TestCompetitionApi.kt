package com.graphite.competitionplanner.competition.api

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.competition.domain.UpdateCompetition
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.user.service.UserService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestCompetitionApi {

    private val createCompetition = mock(CreateCompetition::class.java)
    private val updateCompetition = mock(UpdateCompetition::class.java)
    private val findCompetition = mock(FindCompetitions::class.java)
    private val getDaysOfCompetition = mock(GetDaysOfCompetition::class.java)
    private val userService = mock(UserService::class.java)
    private val api = CompetitionApi(createCompetition, updateCompetition, findCompetition, getDaysOfCompetition, userService)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCallServiceWhenAddingCompetition() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec()

        // Act
        api.addCompetition(spec)

        // Assert
        verify(createCompetition, times(1)).execute(spec)
        verify(createCompetition, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallDomainWhenUpdatingCompetition() {
        // Setup
        val updateSpec = dataGenerator.newCompetitionUpdateSpec()

        // Act
        api.updateCompetition(1, updateSpec)

        // Assert
        verify(updateCompetition, times(1)).execute(1, updateSpec)
        verify(updateCompetition, times(1)).execute(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallDomainWhenFindingCompetition() {
        // Act
        api.getCompetition(1)

        // Assert
        verify(findCompetition, times(1)).byId(1)
        verify(findCompetition, times(1)).byId(anyInt())
    }

    @Test
    fun shouldCallWithNullArguments() {
        // Act
        api.getAll(null, null)

        // Assert
        verify(findCompetition, times(1)).thatStartOrEndWithin(null, null)
    }

    @Test
    fun shouldCallWithSpecifiedArguments() {
        // Setup
        val start = LocalDate.now()
        val end = start.plusMonths(1)

        // Act
        api.getAll(start, end)

        // Assert
        verify(findCompetition, times(1)).thatStartOrEndWithin(start, end)
    }

    @Test
    fun shouldCallDomainWhenGettingDates() {
        // Setup
        val competition = dataGenerator.newCompetitionDTO(id = 10)
        `when`(findCompetition.byId(competition.id)).thenReturn(competition)

        // Act
        api.getDaysInCompetition(competition.id)

        // Assert
        verify(getDaysOfCompetition, times(1)).execute(competition)
        verify(getDaysOfCompetition, times(1)).execute(TestHelper.MockitoHelper.anyObject())
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