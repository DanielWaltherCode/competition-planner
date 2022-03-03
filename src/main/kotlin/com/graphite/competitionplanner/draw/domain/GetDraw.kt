package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.Tables.POOL
import com.graphite.competitionplanner.Tables.POOL_TO_PLAYOFF_MAP
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.*
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.jooq.DSLContext
import org.jooq.Record
import org.springframework.stereotype.Repository

@Repository
class GetDraw(
    val competitionCategoryRepository: ICompetitionCategoryRepository,
    val matchService: MatchService,
    val dslContext: DSLContext,
    val calculateGroupStanding: CalculateGroupStanding
) {

    fun execute(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val matchesInCategory: List<MatchAndResultDTO> = matchService.getMatchesInCategory(competitionCategoryId)

        val playOffMatches: List<MatchAndResultDTO> = matchesInCategory.filter { it.groupOrRound.isRound() }

        // Front end assumes sorted list by order. TODO: Clean up and write tests
        val updatedPlayoffMatches: List<MatchAndResultDTO> =
            updatePlaceholderNamesInFirstRound(competitionCategoryId, playOffMatches).sortedBy { it.matchOrderNumber }

        val playoffRounds: List<PlayoffRoundDTO> = convertToPlayoffRound(updatedPlayoffMatches)
        val groupDraws: List<GroupDrawDTO> =
            constructGroupDraw(matchesInCategory.filterNot { it.groupOrRound.isRound() })
        val playoffRoundsSorted = playoffRounds.sortedByDescending { p -> p.round }
        val groupToPlayoffList: List<GroupToPlayoff> = getPoolToPlayoffList(competitionCategoryId)

        return CompetitionCategoryDrawDTO(
            competitionCategoryId,
            playoffRoundsSorted,
            groupDraws,
            groupToPlayoffList
        )
    }

    private fun convertToPlayoffRound(playoffMatches: List<MatchAndResultDTO>): MutableList<PlayoffRoundDTO> {
        val playoffRoundList = mutableListOf<PlayoffRoundDTO>()
        val distinctRounds = playoffMatches.map { it.groupOrRound }.distinct()

        for (round in distinctRounds) {
            playoffRoundList.add(
                PlayoffRoundDTO(
                    Round.valueOf(round),
                    playoffMatches.filter { it.groupOrRound == round }
                )
            )
        }
        return playoffRoundList
    }

    /**
     * Update the name of all placeholder matches in the first round with the name of the group position.
     */
    private fun updatePlaceholderNamesInFirstRound(
        competitionCategoryId: Int,
        playOffMatches: List<MatchAndResultDTO>
    ): List<MatchAndResultDTO> {
        val poolToPlayOffMap: List<GroupToPlayoff> = getPoolToPlayoffList(competitionCategoryId)
        val potentialMatchUpdateIds: List<Int> = poolToPlayOffMap.map { it.playoffPosition.matchId }
        val matchesThatCanBeUpdated: List<MatchAndResultDTO> =
            playOffMatches.filter { potentialMatchUpdateIds.contains(it.id) }
        val matchesThatCannotBeUpdated: List<MatchAndResultDTO> =
            playOffMatches.filterNot { potentialMatchUpdateIds.contains(it.id) }

        val placeholder = Registration.Placeholder()
        val updatedMatches = mutableListOf<MatchAndResultDTO>()
        for (match in matchesThatCanBeUpdated) {
            var player1 = match.firstPlayer
            var player2 = match.secondPlayer
            if (match.firstPlayer.any { it.firstName == placeholder.toString() }) {
                val player1Map =
                    poolToPlayOffMap.first { it.playoffPosition.matchId == match.id && it.playoffPosition.position == 1 }
                player1 = match.firstPlayer.map { updateNameOfPlaceholder(it, player1Map) }
            }
            if (match.secondPlayer.any { it.firstName == placeholder.toString() }) {
                val player2Map =
                    poolToPlayOffMap.first { it.playoffPosition.matchId == match.id && it.playoffPosition.position == 2 }
                player2 = match.secondPlayer.map { updateNameOfPlaceholder(it, player2Map) }
            }

            updatedMatches.add(
                MatchAndResultDTO(
                    match.id,
                    match.startTime,
                    match.endTime,
                    match.competitionCategory,
                    match.matchType,
                    player1,
                    player2,
                    match.matchOrderNumber,
                    match.groupOrRound,
                    match.winner,
                    match.wasWalkover,
                    match.result
                )
            )

        }

        return matchesThatCannotBeUpdated + updatedMatches
    }

    private fun updateNameOfPlaceholder(player: PlayerWithClubDTO, groupToPlayoff: GroupToPlayoff): PlayerWithClubDTO {
        return PlayerWithClubDTO(
            player.id,
            "${groupToPlayoff.groupPosition.groupName}${groupToPlayoff.groupPosition.position}",
            player.lastName,
            player.club,
            player.dateOfBirth
        )
    }

    private fun constructGroupDraw(matches: List<MatchAndResultDTO>): List<GroupDrawDTO> {
        val groups = mutableListOf<GroupDrawDTO>()

        if (matches.isNotEmpty()) {
            val distinctGroups = matches.map { it.groupOrRound }.distinct()
            for (group in distinctGroups) {
                val matchesInGroup = matches.filter { it.groupOrRound == group }
                val playersInGroup: MutableSet<PlayerInPoolDTO> = mutableSetOf()
                for (match in matchesInGroup) {
                    playersInGroup.add(PlayerInPoolDTO(match.firstPlayer, 1))
                    playersInGroup.add(PlayerInPoolDTO(match.secondPlayer, 1))
                }
                val groupStanding: List<GroupStandingDTO> = calculateGroupStanding.execute(
                    matchesInGroup[0].competitionCategory.id,
                    matchesInGroup[0].groupOrRound
                )
                groups.add(GroupDrawDTO(group, playersInGroup.toList(), matchesInGroup, groupStanding))
            }
        }
        return groups
    }

    private fun getPoolToPlayoffList(competitionCategoryId: Int): List<GroupToPlayoff> {

        val records = dslContext.select()
            .from(POOL_TO_PLAYOFF_MAP)
            .join(POOL).on(POOL_TO_PLAYOFF_MAP.POOL_ID.eq(POOL.ID))
            .where(POOL_TO_PLAYOFF_MAP.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .orderBy(POOL_TO_PLAYOFF_MAP.MATCH_ID).fetch().toList()

        return processPoolToPlayoffMapRecords(records)
    }

    private fun processPoolToPlayoffMapRecords(records: List<Record>): List<GroupToPlayoff> {
        return records.map {
            GroupToPlayoff(
                PlayoffPosition(
                    it.get(POOL_TO_PLAYOFF_MAP.MATCH_ID),
                    it.get(POOL_TO_PLAYOFF_MAP.MATCH_REGISTRATION_POSITION),
                ),
                GroupPosition(
                    it.getValue(POOL.NAME),
                    it.getValue(POOL_TO_PLAYOFF_MAP.POOL_POSITION)
                )
            )
        }
    }

    fun String.isRound(): Boolean {
        return Round.values().any { it.name == this }
    }
}