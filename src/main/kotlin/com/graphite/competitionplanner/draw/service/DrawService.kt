package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.api.DrawDTO
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.tables.records.PoolDrawRecord
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class DrawService(
    val registrationService: RegistrationService,
    val registrationRepository: RegistrationRepository,
    val matchRepository: MatchRepository,
    val matchService: MatchService,
    val competitionDrawRepository: CompetitionDrawRepository,
    val drawUtil: DrawUtil,
    val drawUtilTwoProceed: DrawUtilTwoProceed,
    val playerRepository: PlayerRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val findCompetitionCategory: FindCompetitionCategory
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getDraw(competitionCategoryId: Int): DrawDTO {
        val metadata = findCompetitionCategory.byId(competitionCategoryId).settings
        if (DrawType.valueOf(metadata.drawType.name) == DrawType.CUP_ONLY) {
            return getCupOnlyDraw(competitionCategoryId)
        }
        return DrawDTO(
            getPoolDraw(competitionCategoryId),
            getPlayoffForGroups(competitionCategoryId)
        )
    }

    fun getCupOnlyDraw(competitionCategoryId: Int): DrawDTO {
        val groups = mutableListOf<SingleGroupDTO>()

        val playoffRound = mutableListOf<PlayoffRound>()
        val matches = matchService.getMatchesInCategory(competitionCategoryId)
        val round = drawUtil.getRound(matches.size)
        val matchUps = mutableListOf<MatchUp>()

        for (match in matches) {
            matchUps.add(
                MatchUp(
                    player1 = match.firstPlayer[0].firstName,
                    player2 = match.secondPlayer[0].firstName
                )
            )
        }
        playoffRound.add(PlayoffRound(round, matchUps))
        return DrawDTO(GroupDrawDTO(groups), PlayoffDTO(playoffRound))
    }

    fun getPoolDraw(competitionCategoryId: Int): GroupDrawDTO {
        val groupMatches = matchService.getGroupMatchesInCategory(competitionCategoryId)
        val groups = mutableListOf<SingleGroupDTO>()

        if (groupMatches.isNotEmpty()) {
            val distinctGroups = groupMatches.map { it.groupOrRound }.distinct()
            for (group in distinctGroups) {
                val matchesInGroup = mutableListOf<MatchAndResultDTO>()
                for (match in groupMatches) {
                    if (match.groupOrRound.equals(group)) {
                        matchesInGroup.add(match)
                    }
                }
                val nrPlayers = drawUtil.getNrPlayersInGroup(matchesInGroup.size)
                val poolDraw = getPoolDraw(competitionCategoryId, group)
                groups.add(SingleGroupDTO(group, poolDraw, matchesInGroup, nrPlayers))
            }
        }
        return GroupDrawDTO(groups)
    }


    /**
     * This method is only called to create the "empty" playoff draw before group stage is played
     * So it can say that A1 will play againt E2 in the first match, D1-F2 in the second etc. After group
     * stage actual players name will be entered.
     */
    fun getPlayoffForGroups(competitionCategoryId: Int): PlayoffDTO {
        val categoryMetadata = findCompetitionCategory.byId(competitionCategoryId).settings
        val groupMatches = matchService.getGroupMatchesInCategory(competitionCategoryId)
        if (groupMatches.isEmpty()) {
            logger.error("No group matches found before attempt to make playoff draw")
        }

        // Find out how many groups there are and how many matches there should be in first round
        // Number of matches == the first 2^x that is larger than nr players proceeding to playoffs
        val distinctGroups = groupMatches.map { it.groupOrRound }.distinct()

        val playoffMatches: List<MatchUp>
        if (categoryMetadata.playersToPlayOff == 1) {
            playoffMatches = drawUtil.playoffForGroupsWhereOneProceeds(distinctGroups)
        } else if (categoryMetadata.playersToPlayOff == 2) {
            playoffMatches = drawUtilTwoProceed.playoffDrawWhereTwoProceed(distinctGroups)
        } else {
            logger.error("Unclear number of players continued from group: $categoryMetadata.nrPlayersToPlayoff")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of players going to playoff should be 1 or 2")
        }
        val round = drawUtil.getRound(playoffMatches.size)
        val playoffRound = mutableListOf<PlayoffRound>()
        playoffRound.add(PlayoffRound(round, playoffMatches))
        return PlayoffDTO(playoffRound)
    }

    fun getPoolDraw(competitionCategoryId: Int, groupName: String): List<PoolDrawDTO> {
        val poolDrawRecords = competitionDrawRepository.getPoolDraw(competitionCategoryId, groupName)
        return poolDrawRecords.map { poolDrawRecordToDTO(it) }
    }

    fun isDrawMade(competitionCategoryId: Int): Boolean {
        return matchRepository.isCategoryDrawn(competitionCategoryId)
    }

    fun poolDrawRecordToDTO(poolDrawRecord: PoolDrawRecord): PoolDrawDTO {
        return PoolDrawDTO(
            registrationService.getPlayersWithClubFromRegistrationId(poolDrawRecord.registrationId),
            poolDrawRecord.playerNumber
        )
    }

}

data class MatchSpec(
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val competitionCategoryId: Int,
    val matchType: MatchType,
    val firstRegistrationId: Int,
    val secondRegistrationId: Int,
    val matchOrderNumber: Int,
    val groupOrRound: String // Either group name (e.g. Group "A") or the round like Round of 64, Quarterfinals
)

// TODO: Move to repository. Match repository ?
enum class MatchType {
    GROUP, PLAYOFF
}

data class GroupDrawDTO(
    val groups: List<SingleGroupDTO>
)

data class SingleGroupDTO(
    val groupName: String,
    val poolDraw: List<PoolDrawDTO>,
    val matches: List<MatchAndResultDTO>,
    val nrPlayers: Int
)

// Contains player position in given group. Has no link to matches/results, is only used to display players
// in correct order when the group is presented
data class PoolDrawDTO(
    val playerDTOs: List<PlayerWithClubDTO>,
    val playerNumber: Int
)

data class PlayoffDTO(
    val rounds: List<PlayoffRound>
)

data class PlayoffRound(
    val round: Round,
    val matches: List<MatchUp>
)
