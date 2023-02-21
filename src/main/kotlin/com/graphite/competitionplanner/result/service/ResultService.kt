package com.graphite.competitionplanner.result.service

import com.graphite.competitionplanner.Tables.POOL
import com.graphite.competitionplanner.Tables.POOL_RESULT
import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.*
import com.graphite.competitionplanner.match.domain.*
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.domain.asInt
import com.graphite.competitionplanner.registration.domain.isReal
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.domain.AddResult
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.tables.records.MatchRecord
import com.graphite.competitionplanner.tables.records.PoolRecord
import com.graphite.competitionplanner.tables.records.PoolResultRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import kotlin.math.ceil

@Service
class ResultService(
        val resultRepository: IResultRepository,
        val matchRepository: MatchRepository,
        val findCompetitionCategory: FindCompetitionCategory,
        val addResult: AddResult,
        val competitionDrawRepository: ICompetitionDrawRepository,
        val registrationRepository: IRegistrationRepository,
        val dslContext: DSLContext
) {

    fun addFinalMatchResult(matchId: Int, resultSpec: ResultSpec): ResultDTO {
        lateinit var result: ResultDTO
        competitionDrawRepository.asTransaction {
            val match = matchRepository.getMatch2(matchId)
            val competitionCategory = findCompetitionCategory.byId(match.competitionCategoryId)
            result = addResult.execute(match, resultSpec, competitionCategory)
            advanceRegistrations(competitionCategory, match)
        }
        return result
    }

    /**
     * Delete the results of the match. This can potentially revert any advancements from pool to playoff.
     */
    fun deleteResults(matchId: Int) {
        competitionDrawRepository.asTransaction {
            val match = matchRepository.getMatch2(matchId)
            match.winner = null
            match.result = emptyList()
            matchRepository.save(match)

            when(match) {
                is PoolMatch -> {
                    removeAnyFinalGroupResult(match)
                    revertAnyAdvancementsToPlayoffFromGivenGroup(match.competitionCategoryId, match.name)
                }
                is PlayoffMatch -> {
                    tryRevertAnyPlayoffAdvancement(match)
                }
            }
        }
    }

    fun getResult(matchId: Int): ResultDTO {
        val resultList = resultRepository.getResult(matchId)
        return ResultDTO(resultList)
    }

    private fun advanceRegistrations(competitionCategory: CompetitionCategoryDTO, match: Match) {
        // Match has now been completed, and we need to decide if we need to advance players (registrations) to next round
        when (match) {
            is PlayoffMatch -> competitionCategory.handleAdvancementOf(match)
            is PoolMatch -> competitionCategory.handleAdvancementOf(match)
        }
    }

    private fun CompetitionCategoryDTO.handleAdvancementOf(match: PlayoffMatch) {
        if (match.round != Round.FINAL) {
            val draw = competitionDrawRepository.get(this.id)

            val playoff = draw.getPlayoffMatchBelongsTo(match)

            val winner = matchRepository.getMatch(match.id).winner
            val nextRound = playoff.filter { it.round < match.round }.maxByOrNull { it.round }!!
            val nextOrderNumber = ceil(match.orderNumber / 2.0).toInt() // 1 -> 1, 2 -> 1, 3 -> 2, etc.
            val nextMatch = matchRepository.getMatch2(
                    nextRound.matches.first { it.matchOrderNumber == nextOrderNumber }.id
            )
            if (match.orderNumber % 2 == 1) {
                nextMatch.firstRegistrationId = winner
            } else {
                nextMatch.secondRegistrationId = winner
            }
            matchRepository.save(nextMatch)
        }
    }

    /**
     * Return the playoff that the given match belongs to
     */
    private fun CompetitionCategoryDrawDTO.getPlayoffMatchBelongsTo(match: PlayoffMatch): List<PlayoffRoundDTO> {
        val belongsToPlayoffA = this.playOff.flatMap { round ->
            round.matches.map { match -> match.id }
        }.contains(match.id)

        return if (belongsToPlayoffA) {
            this.playOff
        } else {
            this.playOffB
        }
    }

    /**
     * Returns next rounds match that the winner of the given match will advance to. Returns null if the given
     * match is the final.
     */
    private fun CompetitionCategoryDrawDTO.getNextRoundsMatch(match: PlayoffMatch): Match? {
        if (match.round == Round.FINAL) {
            return null
        }

        val playOff = this.getPlayoffMatchBelongsTo(match)
        val nextRound = playOff.filter { it.round < match.round }.maxByOrNull { it.round }!!
        val nextOrderNumber = ceil(match.orderNumber / 2.0).toInt() // 1 -> 1, 2 -> 1, 3 -> 2, etc.
        return matchRepository.getMatch2(
            nextRound.matches.first { it.matchOrderNumber == nextOrderNumber }.id
        )
    }

    private fun CompetitionCategoryDTO.handleAdvancementOf(match: PoolMatch) {
        if (this.settings.drawType == DrawType.POOL_ONLY) {
            return // Nothing to advance
        } else {
            val draw: CompetitionCategoryDrawDTO = competitionDrawRepository.get(this.id)
            val matchesInPool: List<MatchAndResultDTO> = draw.groups.first { it.name == match.name }.matches
            val allMatchesHaveBeenPlayedInPool: Boolean =
                    matchesInPool.all { it.winner.isNotEmpty() } // If winner is empty, then there is no winner.
            if (allMatchesHaveBeenPlayedInPool) {
                val groupStanding: List<GroupStandingDTO> = draw.groups.first { it.name == match.name }.groupStandingList
                // Store final group results
                storeFinalGroupResult(groupStanding,
                        competitionDrawRepository.getPool(match.competitionCategoryId, match.name))

                val allRegistrations: List<Int> = groupStanding.sortedBy { it.groupPosition }.map {
                    registrationRepository.getRegistrationIdForPlayerInCategory(this.id, it.player.first().id)
                }

                val groupToPlayoff: List<GroupToPlayoff> =
                        draw.poolToPlayoffMap.filter { it.groupPosition.groupName == match.name }
                                .sortedBy { it.groupPosition.position }
                val registrationsToAdvance = allRegistrations.subList(0, groupToPlayoff.size)
                assert(groupToPlayoff.size == registrationsToAdvance.size) { "Number of players advancing does not match the number of group to playoff mappings" }

                groupToPlayoff.zip(registrationsToAdvance) { groupToPlayOffMapping, registrationId ->
                    val playoffPosition = groupToPlayOffMapping.playoffPosition
                    val record = matchRepository.getMatch(playoffPosition.matchId)
                    if (playoffPosition.position == 1) {
                        record.firstRegistrationId = registrationId
                        if (record.secondRegistrationId == Registration.Bye.asInt()) {
                            handleDirectAdvancementWhenMeetingBye(this, registrationId, record)
                        }
                    } else {
                        record.secondRegistrationId = registrationId
                        if (record.firstRegistrationId == Registration.Bye.asInt()) {
                            handleDirectAdvancementWhenMeetingBye(this, registrationId, record)
                        }
                    }
                    record.update()
                }
            }
        }
    }

    private fun handleDirectAdvancementWhenMeetingBye(competitionCategoryDTO: CompetitionCategoryDTO,
                                                      winnerRegistrationId: Int, record: MatchRecord) {
        record.winner = winnerRegistrationId
        record.update()
        val pm = PlayoffMatch(Round.valueOf(record.groupOrRound), record.matchOrderNumber, record.id,
                record.competitionCategoryId, record.firstRegistrationId,
                record.secondRegistrationId, record.wasWalkover, record.winner)
        competitionCategoryDTO.handleAdvancementOf(pm)
    }


    private fun removeAnyFinalGroupResult(match: PoolMatch) {
        val draw = competitionDrawRepository.get(match.competitionCategoryId)
        val playoffHasStarted = draw.playOff.first().matches.any { it.winner.isNotEmpty() } ||
                    draw.playOffB.first().matches.any { it.winner.isNotEmpty() }

        if (playoffHasStarted) {
            throw BadRequestException(BadRequestType.RESULT_CANNOT_DELETE,
                "Cannot delete result from pool match when play-off has started. Remove all play-off results first.")
        }

        dslContext.deleteFrom(POOL_RESULT)
            .where(POOL_RESULT.POOL_ID.`in`(
                dslContext
                    .select(POOL.ID)
                    .from(POOL)
                    .where(POOL.COMPETITION_CATEGORY_ID.eq(match.competitionCategoryId).and(POOL.NAME.eq(match.name)))
            ))
            .execute()
    }

    private fun revertAnyAdvancementsToPlayoffFromGivenGroup(competitionCategoryId: Int, groupName: String) {
        val draw = competitionDrawRepository.get(competitionCategoryId)

        // We need to revert any play off matches that contain players from the given pool.
        val s = matchRepository.getMatchesInCategoryForMatchType(competitionCategoryId, MatchType.GROUP)
        val t = s.filter { it.groupOrRound == groupName }
        val registrationIdsInGroup = t.flatMap { listOf(it.firstRegistrationId, it.secondRegistrationId) }.distinct()

        draw.playOff.reset(registrationIdsInGroup)
        draw.playOffB.reset(registrationIdsInGroup)
    }

    fun List<PlayoffRoundDTO>.reset(registrationIdsInGroup: List<Int>) {
        this.forEach {
            it.matches.forEach { m ->
                val playoffMatch = matchRepository.getMatch2(m.id)
                var resetResult = false
                if (playoffMatch.firstRegistrationId.isReal() && registrationIdsInGroup.contains(playoffMatch.firstRegistrationId)) {
                    playoffMatch.firstRegistrationId = Registration.Placeholder().asInt()
                    resetResult = true
                }
                if (playoffMatch.secondRegistrationId.isReal() && registrationIdsInGroup.contains(playoffMatch.secondRegistrationId)) {
                    playoffMatch.secondRegistrationId = Registration.Placeholder().asInt()
                    resetResult = true
                }
                if (resetResult) {
                    playoffMatch.winner = null
                    playoffMatch.result = emptyList()
                }
                matchRepository.save(playoffMatch)
            }
        }
    }

    /**
     * Try and revert any advancement from the given match. Nothing happens if this is the FINAL as there is no
     * advancement to revert.
     *
     * @throws BadRequestException If the next round's match has already been played.
     */
    @Throws(BadRequestException::class)
    private fun tryRevertAnyPlayoffAdvancement(match: PlayoffMatch) {
        val draw = competitionDrawRepository.get(match.competitionCategoryId)
        val nextRoundsMatch = draw.getNextRoundsMatch(match)
        if (nextRoundsMatch?.winner != null) {
            throw BadRequestException(BadRequestType.RESULT_CANNOT_DELETE,
                "Next round has already been played. Delete that result first.")
        } else if (nextRoundsMatch != null) {
            if (match.orderNumber % 2 == 1) {
                nextRoundsMatch.firstRegistrationId = Registration.Placeholder().asInt()
            } else {
                nextRoundsMatch.secondRegistrationId = Registration.Placeholder().asInt()
            }
            matchRepository.save(nextRoundsMatch)
        }
    }

    private fun storeFinalGroupResult(groupStanding: List<GroupStandingDTO>, pool: PoolRecord) {
        for (standing in groupStanding) {
            val poolResultRecord: PoolResultRecord = dslContext.newRecord(POOL_RESULT)
            val registrationId = registrationRepository.getRegistrationIdForPlayerInCategory(pool.competitionCategoryId,
                    standing.player.first().id)
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