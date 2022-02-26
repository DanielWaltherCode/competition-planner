package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.Tables.POOL_RESULT
import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.GameSettingsDTO
import com.graphite.competitionplanner.draw.interfaces.*
import com.graphite.competitionplanner.match.domain.Match
import com.graphite.competitionplanner.match.domain.PlayoffMatch
import com.graphite.competitionplanner.match.domain.PoolMatch
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.domain.AddResult
import com.graphite.competitionplanner.result.repository.ResultRepository
import com.graphite.competitionplanner.tables.records.GameRecord
import com.graphite.competitionplanner.tables.records.PoolRecord
import com.graphite.competitionplanner.tables.records.PoolResultRecord
import org.jooq.DSLContext
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
    val registrationRepository: RegistrationRepository,
    val dslContext: DSLContext
) {

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

        val match = matchRepository.getMatch2(matchId)
        val competitionCategory = findCompetitionCategory.byId(match.competitionCategoryId)
        val result = addResult.execute(match, resultSpec, competitionCategory)
        advanceRegistrations(competitionCategory, match)
        return result
    }

    private fun advanceRegistrations(competitionCategory: CompetitionCategoryDTO, match: Match) {
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
            val nextRound = draw.playOff.filter { it.round < match.round }.maxByOrNull { it.round }!!
            val nextOrderNumber = ceil( match.orderNumber / 2.0 ).toInt() // 1 -> 1, 2 -> 1, 3 -> 2, etc.
            val nextMatch = matchRepository.getMatch2(
                nextRound.matches.first { it.matchOrderNumber == nextOrderNumber }.id
            )
            if (match.orderNumber % 2 == 1) {
                nextMatch.firstRegistrationId = winner
            }else {
                nextMatch.secondRegistrationId = winner
            }
            matchRepository.save(nextMatch)
        }
    }

    fun CompetitionCategoryDTO.handleAdvancementOf(match: PoolMatch) {
        if (this.settings.drawType == DrawType.POOL_ONLY) {
            return // Nothing to advance
        } else {
            val draw: CompetitionCategoryDrawDTO = competitionDrawRepository.get(this.id)
            val matchesInPool: List<MatchAndResultDTO> = draw.groups.first { it.name == match.name }.matches
            val allMatchesHaveBeenPlayedInPool: Boolean = matchesInPool.all { it.winner.isNotEmpty() } // If winner is empty, then there is no winner.
            if (allMatchesHaveBeenPlayedInPool) {
                val groupStanding: List<GroupStandingDTO> = draw.groups.first { it.name == match.name }.groupStandingList
                // Store final group results
                storeFinalGroupResult(groupStanding, competitionDrawRepository.getPool(match.competitionCategoryId, match.name))

                val allRegistrations: List<Int> = groupStanding.sortedBy { it.groupPosition }.map {
                    registrationRepository.getRegistrationIdForPlayerInCategory(this.id, it.player.first().id)
                }

                val groupToPlayoff: List<GroupToPlayoff> = draw.poolToPlayoffMap.filter { it.groupPosition.groupName == match.name }.sortedBy { it.groupPosition.position }
                val registrationsToAdvance = allRegistrations.subList(0, groupToPlayoff.size)
                assert(groupToPlayoff.size == registrationsToAdvance.size) { "Number of players advancing does not match the number of group to playoff mappings" }

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

    fun storeFinalGroupResult(groupStanding: List<GroupStandingDTO>, pool: PoolRecord) {
        for (standing in groupStanding) {
            val poolResultRecord: PoolResultRecord = dslContext.newRecord(POOL_RESULT)
            val registrationId = registrationRepository.getRegistrationIdForPlayerInCategory(pool.competitionCategoryId, standing.player.first().id)
            poolResultRecord.poolPosition = standing.groupPosition
            poolResultRecord.poolId = pool.id
            poolResultRecord.registrationId = registrationId
            poolResultRecord.store()
        }
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