package com.graphite.competitionplanner.result.domain

import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.match.domain.GameResult
import com.graphite.competitionplanner.match.domain.IMatchRepository
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.match.domain.PlayoffMatch
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.GameDTO
import com.graphite.competitionplanner.result.service.ResultDTO
import org.springframework.stereotype.Component
import kotlin.math.abs
import kotlin.math.ceil

@Component
class AddResult(
    val matchRepository: IMatchRepository,
) {

    fun execute(match: Match, result: ResultSpec, competitionCategory: CompetitionCategoryDTO): ResultDTO {
        val gameSettings = competitionCategory.gameSettings

        val policy = getPolicyFor(match, gameSettings)
        val winnerId = policy.validateResultAndReturnWinner(match, result)

        match.winner = winnerId
        match.result = result.gameList.map { GameResult(0, it.gameNumber, it.firstRegistrationResult, it.secondRegistrationResult) }
        matchRepository.save(match)

        return ResultDTO(
            matchRepository.getMatch2(match.id).result.map { GameDTO(it.id, it.number, it.firstRegistrationResult, it.secondRegistrationResult) }
        )
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
     * @throws BadRequestException When the results are invalid.
     * @return The registration id of the winner.
     */
    @Throws(BadRequestException::class)
    fun validateResultAndReturnWinner(match: Match, result: ResultSpec): Int {

        if (result.gameList.size > this.numberOfSets) {
            throw BadRequestException(BadRequestType.GAME_TOO_MANY_SETS_REPORTED, "Too many sets reported")
        }

        if (result.gameList.any { it.firstRegistrationResult < this.winScore  &&
                    it.secondRegistrationResult < this.winScore }) {
            throw BadRequestException(BadRequestType.GAME_TOO_FEW_POINTS_IN_SET, "Too few points in set")
        }

        if (result.gameList.any { abs(it.firstRegistrationResult - it.secondRegistrationResult) < this.winMargin }) {
            throw BadRequestException(BadRequestType.GAME_NOT_ENOUGH_WIN_MARGIN, "The win margin is too small")
        }

        val requiredWins = ceil(this.numberOfSets / 2.0).toInt()
        val firstRegistrationWins = result.gameList.filter { it.firstRegistrationResult > it.secondRegistrationResult }.size
        val secondRegistrationWins = result.gameList.filter { it.firstRegistrationResult < it.secondRegistrationResult }.size

        if (firstRegistrationWins < requiredWins && secondRegistrationWins < requiredWins) {
            throw BadRequestException(BadRequestType.GAME_COULD_NOT_DECIDE_WINNER, "Couldn't decide winner")
        }

        return if (firstRegistrationWins > secondRegistrationWins) {
            match.firstRegistrationId
        }else {
            match.secondRegistrationId
        }
    }
}