package com.graphite.competitionplanner.result.domain

import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestReportResult {

    private val mockedRepository = Mockito.mock(IResultRepository::class.java)
    private val mockedMatchService = Mockito.mock(MatchService::class.java)
    private val addResult = AddResult(mockedRepository, mockedMatchService)

    private val dataGenerator = DataGenerator()

    @Test
    fun addingResultsForPoolPlay() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(
                winMargin = 2,
                winScore = 11,
                numberOfSets = 3
            )
        )
        val match = dataGenerator.newSimpleMatchDTO(matchType = "D")
        val gameSpec1 = dataGenerator.newGameSpec(firstRegistrationResult = 11, secondRegistrationResult = 8)
        val gameSpec2 = dataGenerator.newGameSpec(firstRegistrationResult = 12, secondRegistrationResult = 10)
        val spec = dataGenerator.newResultSpec(listOf(gameSpec1, gameSpec2))

        // Act
        addResult.execute(match, spec, competitionCategory)

        // Assert
        verify(mockedRepository, times(1)).storeResult(match.id, gameSpec1)
        verify(mockedRepository, times(1)).storeResult(match.id, gameSpec2)
        verify(mockedRepository, times(2)).storeResult(Mockito.anyInt(), TestHelper.MockitoHelper.anyObject())
        verify(mockedMatchService, times(1)).setWinner(match.id, match.firstRegistrationId)
        verify(mockedMatchService, times(1)).setWinner(Mockito.anyInt(), Mockito.anyInt())
    }

    @Test
    fun addingResultsForPlayOff() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(
                winMarginFinal = 2,
                winScoreFinal = 11,
                numberOfSetsFinal = 3,
                useDifferentRulesInEndGame = true
            )
        )
        val match = dataGenerator.newSimpleMatchDTO(matchType = Round.SEMI_FINAL.name)
        val gameSpec1 = dataGenerator.newGameSpec(firstRegistrationResult = 11, secondRegistrationResult = 8)
        val gameSpec2 = dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 11)
        val gameSpec3 = dataGenerator.newGameSpec(firstRegistrationResult = 11, secondRegistrationResult = 13)
        val spec = dataGenerator.newResultSpec(listOf(gameSpec1, gameSpec2, gameSpec3))

        // Act
        addResult.execute(match, spec, competitionCategory)

        // Assert
        verify(mockedRepository, times(1)).storeResult(match.id, gameSpec1)
        verify(mockedRepository, times(1)).storeResult(match.id, gameSpec2)
        verify(mockedRepository, times(1)).storeResult(match.id, gameSpec3)
        verify(mockedRepository, times(3)).storeResult(Mockito.anyInt(), TestHelper.MockitoHelper.anyObject())
        verify(mockedMatchService, times(1)).setWinner(match.id, match.secondRegistrationId)
        verify(mockedMatchService, times(1)).setWinner(Mockito.anyInt(), Mockito.anyInt())
    }
}