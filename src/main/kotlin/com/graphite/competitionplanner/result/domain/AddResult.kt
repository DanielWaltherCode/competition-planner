package com.graphite.competitionplanner.result.domain

import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.interfaces.isRound
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.match.service.SimpleMatchDTO
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

    fun execute(match: SimpleMatchDTO, result: ResultSpec, competitionCategory: CompetitionCategoryDTO): ResultDTO {
        val gameSettings = competitionCategory.gameSettings

        val policy = getPolicyFor(match, gameSettings)
        policy.validateResult(result, gameSettings)
        val winnerId = policy.getWinner(match, result, gameSettings)

        repository.deleteResults(match.id)
        val games = result.gameList.map { repository.storeResult(match.id, it) }
        matchService.setWinner(match.id, winnerId)

        return ResultDTO(games)
    }

    private fun getPolicyFor(match: SimpleMatchDTO, gameSettings: GameSettingsDTO): ResultPolicy {
        return if (
            match.matchType.isRound() &&
            gameSettings.useDifferentRulesInEndGame &&
            Round.valueOf(match.matchType) <= gameSettings.differentNumberOfGamesFromRound)
        {
            DifferentRulesPolicy()
        } else {
            NormalRulesPolicy()
        }
    }
}

abstract class ResultPolicy {
    /**
     * Check the validity of the reported results.
     *
     * @throws GameValidationException When the results are invalid.
     */
    @Throws(GameValidationException::class)
    abstract fun validateResult(result: ResultSpec, gameSettings: GameSettingsDTO)

    /**
     * Return the registration id of the winner in the match given the results and game settings.
     *
     * @throws GameValidationException When no winner can be determined.
     */
    @Throws(GameValidationException::class)
    abstract fun getWinner(match: SimpleMatchDTO, result: ResultSpec, gameSettings: GameSettingsDTO): Int
}

class NormalRulesPolicy: ResultPolicy() {
    override fun validateResult(result: ResultSpec, gameSettings: GameSettingsDTO) {

        if (result.gameList.size > gameSettings.numberOfSets) {
            throw GameValidationException(GameValidationException.Reason.TOO_MANY_SETS_REPORTED)
        }

        if (result.gameList.any { it.firstRegistrationResult < gameSettings.winScore  &&
                    it.secondRegistrationResult < gameSettings.winScore }) {
            throw GameValidationException(GameValidationException.Reason.TOO_FEW_POINTS_IN_SET)
        }

        if (result.gameList.any { abs(it.firstRegistrationResult - it.secondRegistrationResult) < gameSettings.winMargin }) {
            throw GameValidationException(GameValidationException.Reason.NOT_ENOUGH_WIN_MARGIN)
        }

        val requiredWins = ceil(gameSettings.numberOfSets / 2.0).toInt()
        val firstRegistrationWins = result.gameList.filter { it.firstRegistrationResult > it.secondRegistrationResult }.size
        val secondRegistrationWins = result.gameList.filter { it.firstRegistrationResult < it.secondRegistrationResult }.size

        if (firstRegistrationWins < requiredWins && secondRegistrationWins < requiredWins) {
            throw GameValidationException(GameValidationException.Reason.COULD_NOT_DECIDE_WINNER)
        }
    }

    override fun getWinner(match: SimpleMatchDTO, result: ResultSpec, gameSettings: GameSettingsDTO): Int {
        val requiredWins = ceil(gameSettings.numberOfSets / 2.0).toInt()
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

class DifferentRulesPolicy: ResultPolicy() {
    override fun validateResult(result: ResultSpec, gameSettings: GameSettingsDTO) {

        if (result.gameList.size > gameSettings.numberOfSetsFinal) {
            throw GameValidationException(GameValidationException.Reason.TOO_MANY_SETS_REPORTED)
        }

        if (result.gameList.any { it.firstRegistrationResult < gameSettings.winScoreFinal  &&
                    it.secondRegistrationResult < gameSettings.winScoreFinal }) {
            throw GameValidationException(GameValidationException.Reason.TOO_FEW_POINTS_IN_SET)
        }

        if (result.gameList.any { abs(it.firstRegistrationResult - it.secondRegistrationResult) < gameSettings.winMarginFinal }) {
            throw GameValidationException(GameValidationException.Reason.NOT_ENOUGH_WIN_MARGIN)
        }
    }

    override fun getWinner(match: SimpleMatchDTO, result: ResultSpec, gameSettings: GameSettingsDTO): Int {
        val requiredWins = ceil(gameSettings.numberOfSetsFinal / 2.0).toInt()
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