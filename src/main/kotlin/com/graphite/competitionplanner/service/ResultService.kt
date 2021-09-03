package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.GameSpec
import com.graphite.competitionplanner.api.ResultSpec
import com.graphite.competitionplanner.repositories.MatchRepository
import com.graphite.competitionplanner.repositories.ResultRepository
import com.graphite.competitionplanner.tables.records.GameRecord
import com.graphite.competitionplanner.util.exception.GameValidationException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ResultService(
    val resultRepository: ResultRepository,
    val categoryService: CategoryService,
    val matchService: MatchService,
    val matchRepository: MatchRepository
) {

    private val LOGGER = LoggerFactory.getLogger(javaClass)

    fun addResult(matchId: Int, resultSpec: ResultSpec): ResultDTO {
        val match = matchRepository.getMatch(matchId)
        validateResult(match.competitionCategoryId, resultSpec)

        val resultList = mutableListOf<GameRecord>()

        for (gameResult in resultSpec.gameList) {
            val addedGame = resultRepository.addGameResult(matchId, gameResult)
            resultList.add(addedGame)
        }
        // Winner of last game must be winner
        val finalGame = resultList.last()
        var winnerId = 0
        if (finalGame.firstRegistrationResult > finalGame.secondRegistrationResult) {
            winnerId = match.firstRegistrationId
        }
        else if (finalGame.firstRegistrationResult < finalGame.secondRegistrationResult) {
            winnerId = match.secondRegistrationId
        }
        else {
            // Couldn't determine winner, something is wrong!
                LOGGER.error("Couldn't determine winner in match ${matchId}!", resultSpec)
            throw GameValidationException(HttpStatus.BAD_REQUEST, "3")
        }
        matchService.setWinner(match.id, winnerId)
        return ResultDTO(resultList.map { recordToDTO(it) })

    }

    fun updateGameResult(matchId: Int, gameId: Int, gameSpec: GameSpec): ResultDTO {
        val match = matchService.getMatch(matchId)
        val gameRules = categoryService.getCategoryGameRules(match.competitionCategory.id)
        validateGame(gameRules, gameSpec)
        resultRepository.updateGameResult(gameId, matchId, gameSpec)
        return getResult(matchId)
    }

    fun addPartialResult(matchId: Int, resultSpec: ResultSpec): ResultDTO {
        resultRepository.deleteMatchResult(matchId)
        val resultList = mutableListOf<GameRecord>()
        for (gameResult in resultSpec.gameList) {
            val addedGame = resultRepository.addGameResult(matchId, gameResult)
            resultList.add(addedGame)
        }
        return ResultDTO(resultList.map { recordToDTO(it) })
    }

    fun addFinalMatchResult(matchId: Int, resultSpec: ResultSpec): ResultDTO {
        resultRepository.deleteMatchResult(matchId)
        return addResult(matchId, resultSpec)
    }

    fun getResult(matchId: Int): ResultDTO {
        val resultList = resultRepository.getResult(matchId)
        return ResultDTO(resultList.map { recordToDTO(it) } )
    }

    private fun validateResult(categoryId: Int, resultSpec: ResultSpec) {
        val gameRules = categoryService.getCategoryGameRules(categoryId)
        // TODO -- add specific checks for group stage and playoff stage
        for (game in resultSpec.gameList) {
            validateGame(gameRules, game)
        }
        if ((resultSpec.gameList.size) < (gameRules.nrSets / 2.0)) {
            // Too few sets
            throw GameValidationException(HttpStatus.BAD_REQUEST, "1")
        }

        // Todo -- add special validation for tie breaks or final matches with more sets
    }

    private fun validateGame(gameRules: CategoryGameRulesDTO, game: GameSpec) {
        if (game.firstRegistrationResult < gameRules.winScore
            && game.secondRegistrationResult < gameRules.winScore
        ) {
            // Both players have lower score than registered win score
            throw GameValidationException(HttpStatus.BAD_REQUEST, "2")
        }
    }

    fun recordToDTO(gameRecord: GameRecord): GameDTO {
        return GameDTO(
            gameRecord.id,
            gameRecord.gameNumber,
            gameRecord.firstRegistrationResult,
            gameRecord.secondRegistrationResult
        )
    }
}

data class ResultDTO(
    val gameList: List<GameDTO>
)

data class GameDTO(
    val gameId: Int,
    val gameNumber: Int,
    val firstRegistrationResult: Int,
    val secondRegistrationResult: Int
)