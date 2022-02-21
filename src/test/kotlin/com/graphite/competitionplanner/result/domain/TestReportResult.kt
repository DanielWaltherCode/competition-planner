package com.graphite.competitionplanner.result.domain

import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.match.domain.GameResult
import com.graphite.competitionplanner.match.domain.IMatchRepository
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestReportResult {

    private val mockedRepository = Mockito.mock(IMatchRepository::class.java)
    private val addResult = AddResult(mockedRepository)

    private val dataGenerator = DataGenerator()

    @Captor
    lateinit var classCaptor: ArgumentCaptor<Match>

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
        val match = dataGenerator.newPoolMatch(name = "D")
        val gameSpec1 = dataGenerator.newGameSpec(firstRegistrationResult = 11, secondRegistrationResult = 8)
        val gameSpec2 = dataGenerator.newGameSpec(firstRegistrationResult = 12, secondRegistrationResult = 10)
        val spec = dataGenerator.newResultSpec(listOf(gameSpec1, gameSpec2))
        `when`(mockedRepository.getMatch2(match.id)).thenReturn(match)

        // Act
        addResult.execute(match, spec, competitionCategory)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedRepository).save(TestHelper.MockitoHelper.capture(classCaptor))
        val storedResult = classCaptor.value.result

        assertCorrectResultWasStored(spec, storedResult)
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
        val match = dataGenerator.newPlayOffMatch(round = Round.SEMI_FINAL)
        val gameSpec1 = dataGenerator.newGameSpec(firstRegistrationResult = 11, secondRegistrationResult = 8)
        val gameSpec2 = dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 11)
        val gameSpec3 = dataGenerator.newGameSpec(firstRegistrationResult = 11, secondRegistrationResult = 13)
        val spec = dataGenerator.newResultSpec(listOf(gameSpec1, gameSpec2, gameSpec3))
        `when`(mockedRepository.getMatch2(match.id)).thenReturn(match)

        // Act
        addResult.execute(match, spec, competitionCategory)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedRepository).save(TestHelper.MockitoHelper.capture(classCaptor))
        val storedResult = classCaptor.value.result

        assertCorrectResultWasStored(spec, storedResult)
    }

    private fun assertCorrectResultWasStored(
        spec: ResultSpec,
        storedResult: List<GameResult>
    ) {
        Assertions.assertEquals(spec.gameList.size, storedResult.size)
        for (i in (0 until spec.gameList.size)) {
            val specResult = spec.gameList[i]
            val gameResult = storedResult[i]
            Assertions.assertEquals(specResult.gameNumber, gameResult.number)
            Assertions.assertEquals(specResult.firstRegistrationResult, gameResult.firstRegistrationResult)
            Assertions.assertEquals(specResult.secondRegistrationResult, gameResult.secondRegistrationResult)
        }
    }
}