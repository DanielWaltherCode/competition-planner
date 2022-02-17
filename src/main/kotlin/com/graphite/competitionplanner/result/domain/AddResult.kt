package com.graphite.competitionplanner.result.domain

import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.match.domain.PlayoffMatch
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.result.service.ResultDTO
import org.springframework.stereotype.Component
import kotlin.math.abs
import kotlin.math.ceil

@Component
class AddResult(
    val repository: IResultRepository,
    val matchService: MatchService,
) {

    fun execute(match: Match, result: ResultSpec, competitionCategory: CompetitionCategoryDTO): ResultDTO {
        val gameSettings = competitionCategory.gameSettings

        val policy = getPolicyFor(match, gameSettings)
        val winnerId = policy.validateResultAndReturnWinner(match, result)

        repository.deleteResults(match.id)
        val games = result.gameList.map { repository.storeResult(match.id, it) }
        matchService.setWinner(match.id, winnerId)

        return ResultDTO(games)
    }

    private fun getPolicyFor(match: Match, gameSettings: GameSettingsDTO): ResultValidationSpecification {
        return if (
            match is PlayoffMatch &&
            gameSettings.useDifferentRulesInEndGame &&
            match.round <= gameSettings.differentNumberOfGamesFromRound)
        {
            ResultValidationSpecification(gameSettings.numberOfSetsFinal, gameSettings.winMarginFinal, gameSettings.winScoreFinal)
        } else {
            ResultValidationSpecification(gameSettings.numberOfSets, gameSettings.winMargin, gameSettings.winScore)
        }
    }
}

class ResultValidationSpecification(val numberOfSets: Int, val winMargin: Int, val winScore: Int) {
    /**
     * Check the validity of the reported results and return the winner's id.
     *
     * @throws GameValidationException When the results are invalid.
     * @return The registration id of the winner.
     */
    @Throws(GameValidationException::class)
    fun validateResultAndReturnWinner(match: Match, result: ResultSpec): Int {

        if (result.gameList.size > this.numberOfSets) {
            throw GameValidationException(GameValidationException.Reason.TOO_MANY_SETS_REPORTED)
        }

        if (result.gameList.any { it.firstRegistrationResult < this.winScore  &&
                    it.secondRegistrationResult < this.winScore }) {
            throw GameValidationException(GameValidationException.Reason.TOO_FEW_POINTS_IN_SET)
        }

        if (result.gameList.any { abs(it.firstRegistrationResult - it.secondRegistrationResult) < this.winMargin }) {
            throw GameValidationException(GameValidationException.Reason.NOT_ENOUGH_WIN_MARGIN)
        }

        val requiredWins = ceil(this.numberOfSets / 2.0).toInt()
        val firstRegistrationWins = result.gameList.filter { it.firstRegistrationResult > it.secondRegistrationResult }.size
        val secondRegistrationWins = result.gameList.filter { it.firstRegistrationResult < it.secondRegistrationResult }.size

        if (firstRegistrationWins < requiredWins && secondRegistrationWins < requiredWins) {
            throw GameValidationException(GameValidationException.Reason.COULD_NOT_DECIDE_WINNER)
        }

        return if (firstRegistrationWins > secondRegistrationWins) {
            match.firstRegistrationId
        }else {
            match.secondRegistrationId
        }
    }
}