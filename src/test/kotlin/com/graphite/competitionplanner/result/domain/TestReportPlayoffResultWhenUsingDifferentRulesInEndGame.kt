package com.graphite.competitionplanner.result.domain

import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.match.domain.IMatchRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Description

@SpringBootTest
class TestReportPlayoffResultWhenUsingDifferentRulesInEndGame {

    private val mockedRepository = Mockito.mock(IMatchRepository::class.java)
    private val addResult = AddResult(mockedRepository)

    private val dataGenerator = DataGenerator()

    @Test
    @Description("In this scenario we have not report any results yet, but we try to report too many.")
    fun cannotAddMoreResultsThanThereAreNumberOfSets() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(numberOfSetsFinal = 1, useDifferentRulesInEndGame = true)
        )
        val match = dataGenerator.newPlayOffMatch(round = Round.ROUND_OF_128)
        val gameSpec1 = dataGenerator.newGameSpec(firstRegistrationResult = 11, secondRegistrationResult = 8)
        val gameSpec2 = dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 11)
        val spec = dataGenerator.newResultSpec(listOf(gameSpec1, gameSpec2))

        // Act & Assertions
        val exceptionType = Assertions.assertThrows(BadRequestException::class.java) {
            addResult.execute(match, spec, competitionCategory)
        }.exceptionType

        Assertions.assertEquals(BadRequestType.GAME_TOO_MANY_SETS_REPORTED, exceptionType)
    }

    @Test
    fun cannotReportResultsWithoutWinner() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(winScoreFinal = 7, useDifferentRulesInEndGame = true)
        )
        val match = dataGenerator.newPlayOffMatch(round = Round.QUARTER_FINAL)
        val gameSpec = dataGenerator.newGameSpec(firstRegistrationResult = 6, secondRegistrationResult = 2)

        // Act & Assertions
        val exceptionType = Assertions.assertThrows(BadRequestException::class.java) {
            addResult.execute(match, dataGenerator.newResultSpec(listOf(gameSpec)), competitionCategory)
        }.exceptionType

        Assertions.assertEquals(BadRequestType.GAME_TOO_FEW_POINTS_IN_SET, exceptionType)
    }

    @Test
    fun cannotReportResultIfWinnerDoesNotHaveTheMargin() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(
                winScoreFinal = 7,
                winMarginFinal = 3,
                useDifferentRulesInEndGame = true
            )
        )
        val match = dataGenerator.newPlayOffMatch(round = Round.ROUND_OF_16)
        val gameSpec = dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 5)

        // Act & Assertions
        val exceptionType = Assertions.assertThrows(BadRequestException::class.java) {
            addResult.execute(match, dataGenerator.newResultSpec(listOf(gameSpec)), competitionCategory)
        }.exceptionType

        Assertions.assertEquals(BadRequestType.GAME_NOT_ENOUGH_WIN_MARGIN, exceptionType)
    }

    @Test
    fun cannotReportResultIfNotEnoughGamesHaveBeenPlayed() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(
                winScoreFinal = 7,
                winMarginFinal = 2,
                numberOfSetsFinal = 5,
                useDifferentRulesInEndGame = true
            )
        )
        val match = dataGenerator.newPlayOffMatch(round = Round.ROUND_OF_16)
        val resultSpec = dataGenerator.newResultSpec(
            games = listOf(
                dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 5),
                dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 5),
            )
        )

        // Act & Assertions
        val exceptionType = Assertions.assertThrows(BadRequestException::class.java) {
            addResult.execute(match, resultSpec, competitionCategory)
        }.exceptionType

        Assertions.assertEquals(BadRequestType.GAME_COULD_NOT_DECIDE_WINNER, exceptionType)
    }

    @Test
    fun cannotReportResultIfNotEnoughGamesHaveBeenPlayed2() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(
                winScoreFinal = 7,
                winMarginFinal = 2,
                numberOfSetsFinal = 5,
                useDifferentRulesInEndGame = true,
            )
        )
        val match = dataGenerator.newPlayOffMatch(round = Round.ROUND_OF_16)
        val resultSpec = dataGenerator.newResultSpec(
            games = listOf(
                dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 5),
                dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 5),
                dataGenerator.newGameSpec(firstRegistrationResult = 5, secondRegistrationResult = 7),
                dataGenerator.newGameSpec(firstRegistrationResult = 5, secondRegistrationResult = 7),
            )
        )

        // Act & Assertions
        val exceptionType = Assertions.assertThrows(BadRequestException::class.java) {
            addResult.execute(match, resultSpec, competitionCategory)
        }.exceptionType

        Assertions.assertEquals(BadRequestType.GAME_COULD_NOT_DECIDE_WINNER, exceptionType)
    }

    @Test
    fun shouldOnlyApplyEndGameRulesFromSpecifiedRound() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            gameSettings = dataGenerator.newGameSettingsDTO(
                winScore = 7,
                winMargin = 2,
                numberOfSets = 3,
                winScoreFinal = 7,
                winMarginFinal = 2,
                numberOfSetsFinal = 5,
                useDifferentRulesInEndGame = true,
                differentNumberOfGamesFromRound = Round.SEMI_FINAL
            )
        )
        val quarterFinal = dataGenerator.newPlayOffMatch(round = Round.QUARTER_FINAL)
        val semiFinal = dataGenerator.newPlayOffMatch(round = Round.SEMI_FINAL)
        val resultSpec = dataGenerator.newResultSpec(
            games = listOf( // Results valid in quarter, but not semi.
                dataGenerator.newGameSpec(firstRegistrationResult = 7, secondRegistrationResult = 5),
                dataGenerator.newGameSpec(firstRegistrationResult = 5, secondRegistrationResult = 7),
                dataGenerator.newGameSpec(firstRegistrationResult = 5, secondRegistrationResult = 7),
            )
        )
       `when`(mockedRepository.getMatch2(quarterFinal.id)).thenReturn(quarterFinal)

        // Act & Assertions
        Assertions.assertDoesNotThrow {
            addResult.execute(quarterFinal, resultSpec, competitionCategory)
        }

        Assertions.assertThrows(BadRequestException::class.java) {
            addResult.execute(semiFinal, resultSpec, competitionCategory)
        }

    }
}