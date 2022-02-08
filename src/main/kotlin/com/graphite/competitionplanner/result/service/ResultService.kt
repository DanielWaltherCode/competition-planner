package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.repository.ResultRepository
import com.graphite.competitionplanner.tables.records.GameRecord
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class ResultService(
    val resultRepository: ResultRepository,
    val matchService: MatchService,
    val matchRepository: MatchRepository,
    val findCompetitionCategory: FindCompetitionCategory
) {

    private val LOGGER = LoggerFactory.getLogger(javaClass)

    fun addResult(matchId: Int, resultSpec: ResultSpec): ResultDTO {
        val match: MatchRecord = matchRepository.getMatch(matchId)
        validateResult(match.competitionCategoryId, resultSpec)

        val resultList = mutableListOf<GameRecord>()

        for (gameResult in resultSpec.gameList) {
            val addedGame = resultRepository.addGameResult(matchId, gameResult)
            resultList.add(addedGame)
        }
        // Winner of last game must be winner
        val finalGame: GameRecord = resultList.last()
        val winnerId: Int
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
        val match: MatchDTO = matchService.getMatch(matchId)
        val gameRules: GameSettingsDTO = findCompetitionCategory.byId(match.competitionCategory.id).gameSettings
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

    fun calculateGroupResultsForCategory(categoryId: Int) {
        val groupMatches: List<MatchRecord> = matchRepository.getMatchesInCategoryForMatchType(categoryId, MatchType.GROUP)
        val groups = groupMatches.distinctBy { it.groupOrRound }
        val groupMatchMap: Map<String, List<MatchRecord>> = mutableMapOf()
        for (match in groupMatches) {

        }
    }

    // Returns a list of who each registrationID in a group has defeated
    fun getOpponentsDefeatedInGroup(categoryId: Int, groupName: String): MutableMap<Int, MutableList<Int>> {
        val allGroupMatches: List<MatchRecord> = matchRepository.getMatchesInCategoryForMatchType(categoryId, MatchType.GROUP)
        val matchesInGroup = allGroupMatches.filter { it.groupOrRound == groupName }
        val uniquePlayerRegistrations: MutableSet<Int> = mutableSetOf()
        for (match in matchesInGroup) {
            uniquePlayerRegistrations.add(match.firstRegistrationId)
            uniquePlayerRegistrations.add(match.secondRegistrationId)
        }
        val winMap: MutableMap<Int, MutableList<Int>> = mutableMapOf()

        for (player in uniquePlayerRegistrations) {
            val defeatedPlayersList: MutableList<Int> = mutableListOf()
            for (match in matchesInGroup) {
                if (match.winner == null) {
                    continue
                }
                if (match.winner == player) {
                    if(match.firstRegistrationId == player) {
                        defeatedPlayersList.add(match.secondRegistrationId)
                    }
                    else if(match.secondRegistrationId == player) {
                        defeatedPlayersList.add(match.firstRegistrationId)
                    }
                }
            }
            winMap[player] = defeatedPlayersList
        }
        return winMap
    }

    private fun validateResult(categoryId: Int, resultSpec: ResultSpec) {
        val gameRules = findCompetitionCategory.byId(categoryId).gameSettings
        // TODO -- add specific checks for group stage and playoff stage
        for (game in resultSpec.gameList) {
            validateGame(gameRules, game)
        }
        if ((resultSpec.gameList.size) < (gameRules.numberOfSets / 2.0)) {
            // Too few sets
            throw GameValidationException(HttpStatus.BAD_REQUEST, "1")
        }

        // Todo -- add special validation for tie breaks or final matches with more sets
    }

    private fun validateGame(gameRules: GameSettingsDTO, game: GameSpec) {
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