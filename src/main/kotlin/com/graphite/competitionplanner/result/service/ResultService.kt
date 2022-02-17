package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.*
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.domain.AddResult
import com.graphite.competitionplanner.result.repository.ResultRepository
import com.graphite.competitionplanner.tables.records.GameRecord
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class ResultService(
    val resultRepository: ResultRepository,
    val matchService: MatchService,
    val matchRepository: MatchRepository,
    val findCompetitionCategory: FindCompetitionCategory,
    val addResult: AddResult,
    val competitionDrawRepository: ICompetitionDrawRepository,
    val registrationRepository: RegistrationRepository
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
        } else if (finalGame.firstRegistrationResult < finalGame.secondRegistrationResult) {
            winnerId = match.secondRegistrationId
        } else {
            // Couldn't determine winner, something is wrong!
            LOGGER.error("Couldn't determine winner in match ${matchId}!", resultSpec)
            throw GameValidationException(GameValidationException.Reason.COULD_NOT_DECIDE_WINNER)
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
        // TODO: Think about what happens if something fails in this function. How do we recover?
        // TODO: Maybe we have to make all of this in one transaction so we avoid getting into an unwanted state?

        val match = matchService.getSimpleMatchDTO(matchId)
        val competitionCategory = findCompetitionCategory.byId(match.competitionCategoryId)
        val result = addResult.execute(match, resultSpec, competitionCategory)
        advanceRegistrations(competitionCategory, match)
        return result
    }

    private fun advanceRegistrations(competitionCategory: CompetitionCategoryDTO, match: BaseMatch) {
        // Match has now been completed, and we need to decide if we need to advance players (registrations) to next round
        when(match) {
            is PlayoffMatch -> competitionCategory.handleAdvancementOf(match)
            is PoolMatch -> competitionCategory.handleAdvancementOf(match)
        }
    }

    fun CompetitionCategoryDTO.handleAdvancementOf(match: PlayoffMatch) {
        if (match.round == Round.FINAL) {
            // Nothing to advance
        } else {
            val draw = competitionDrawRepository.get(this.id)
            val winner = matchRepository.getMatch(match.id).winner
            val nextRound = draw.playOff.filter { it.round < match.round }.minByOrNull { it.round }!!
            val nextOrderNumber = ceil( match.orderNumber / 2.0 ).toInt() // 1 -> 1, 2 -> 1, 3 -> 2, etc.
            val nextMatch = nextRound.matches.first { it.matchOrderNumber == nextOrderNumber }
            val record = matchRepository.getMatch(nextMatch.id)
            if (match.orderNumber % 2 == 1) {
                record.firstRegistrationId = winner
            }else {
                record.secondRegistrationId = winner
            }
            record.update()
        }
    }

    fun CompetitionCategoryDTO.handleAdvancementOf(match: PoolMatch) {
        if (this.settings.drawType == DrawType.POOL_ONLY) {
            return // Nothing to advance
        } else {
            val draw = competitionDrawRepository.get(this.id)
            val matchesInPool = draw.groups.first { it.name == match.name }.matches
            val allMatchesHaveBeenPlayedInPool = matchesInPool.all { it.winner.isNotEmpty() } // If winner is empty, then there is no winner.
            if (allMatchesHaveBeenPlayedInPool) {
                val groupStanding = draw.groups.first { it.name == match.name }.groupStandingList
                val registrationsToAdvance = groupStanding.map { // Assuming groupStanding is sorted first to last place
                    registrationRepository.getRegistrationIdForPlayerInCategory(this.id, it.player.first().id)
                }

                val groupToPlayoff = draw.poolToPlayoffMap.filter { it.groupPosition.groupName == match.name }.sortedBy { it.groupPosition.position }
                assert(groupToPlayoff.size == registrationsToAdvance.size) { "Number of players advancing does not match the number of group to playoff mappings" }

                draw.groups.first().groupStandingList

                groupToPlayoff.zip(registrationsToAdvance) { groupToPlayOffMapping, registrationId ->
                    val playoffPosition = groupToPlayOffMapping.playoffPosition
                    val record = matchRepository.getMatch(playoffPosition.matchId)
                    if (playoffPosition.position == 1) {
                        record.firstRegistrationId = registrationId
                    }else {
                        record.secondRegistrationId = registrationId
                    }
                    record.update()
                }
            }
        }
    }

    fun getResult(matchId: Int): ResultDTO {
        val resultList = resultRepository.getResult(matchId)
        return ResultDTO(resultList.map { recordToDTO(it) })
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
            throw GameValidationException(GameValidationException.Reason.TOO_FEW_SETS_REPORTED)
        }

        // Todo -- add special validation for tie breaks or final matches with more sets
    }

    private fun validateGame(gameRules: GameSettingsDTO, game: GameSpec) {
        if (game.firstRegistrationResult < gameRules.winScore
            && game.secondRegistrationResult < gameRules.winScore
        ) {
            // Both players have lower score than registered win score
            throw GameValidationException(GameValidationException.Reason.TOO_FEW_POINTS_IN_SET)
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