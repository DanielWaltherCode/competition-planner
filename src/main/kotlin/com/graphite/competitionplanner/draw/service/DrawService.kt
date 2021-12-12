package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.GeneralSettingsDTO
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.draw.api.DrawDTO
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
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

    fun createDraw(competitionCategoryId: Int): DrawDTO {
        // Check draw type and fetch players signed up in this category
        // its easier to work with registration ids since it could be doubles and then translate back to
        // players after the draw is made

        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId)
        val categoryMetadata: GeneralSettingsDTO
        try {
            categoryMetadata = findCompetitionCategory.byId(competitionCategoryId).settings
        } catch (ex: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Category metadata not found for that id")
        }

        // Check if draw has already been made. If so remove and create again.
        if(isDrawMade(competitionCategoryId)) {
            deleteDraw(competitionCategoryId)
        }

        // Create draw
        createSeed(competitionCategoryId, categoryMetadata)

        if (categoryMetadata.drawType.name == DrawType.POOL_ONLY.name ||
            categoryMetadata.drawType.name == DrawType.POOL_AND_CUP.name
        ) {
            createPoolDraw(registrationIds, categoryMetadata, competitionCategoryId)
        } else {
            createPlayOffs(registrationIds, competitionCategoryId)
        }

        // Fetch the matches that have now been set up
        return getDraw(competitionCategoryId)
    }

    private fun createSeed(competitionCategoryId: Int, categoryMetadata: GeneralSettingsDTO) {
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId)
        val playerList = mutableMapOf<Int, List<PlayerDTO>>()
        for (id in registrationIds) {
            // TODO: Remove this N+1 query, we should make a repository function that takes an array of registration ids instead
            playerList[id] = registrationService.getPlayersFromRegistrationId(id)
        }

        val categoryType = competitionCategoryRepository.getCategoryType(competitionCategoryId).categoryType

        val rankings = mutableMapOf<Int, Int>()

        for ((registrationId, players) in playerList) {

            var sum = 0
            for (player in players) {
                if (categoryType == "SINGLES") {
                    sum += playerRepository.getPlayerRanking(player.id)?.rankSingle ?: 0
                } else if (categoryType == "DOUBLES") {
                    sum += playerRepository.getPlayerRanking(player.id)?.rankDouble ?: 0
                }
            }

            rankings[registrationId] = sum
        }

        val sortedRankings = rankings.toList()
            .sortedBy { (_, value) -> -value }
            .toMap()

        val numberOfSeeds = drawUtil.getNumberOfSeeds(categoryMetadata.playersPerGroup, sortedRankings.size)
        var seed = 1
        for ((registrationId, _) in sortedRankings) {
            registrationRepository.setSeed(registrationId, competitionCategoryId, seed)
            seed += 1

            if (seed > numberOfSeeds) break
        }
    }

    private fun createPlayOffs(
        registrationIds: List<Int>,
        competitionCategoryId: Int
    ) {
        val matches = drawUtil.createDirectToPlayoff(competitionCategoryId, registrationIds)
        for (match in matches) {
            matchRepository.addMatch(match)
        }
    }

    fun createPoolDraw(
        registrationIds: List<Int>,
        categoryMetadata: GeneralSettingsDTO,
        competitionCategoryId: Int
    ) {
        // If draw has already been made, first remove old matches
        if (matchRepository.isCategoryDrawn(competitionCategoryId)) {
            matchRepository.deleteMatchesForCategory(competitionCategoryId)
        }

        val playerGroups = mutableMapOf<Int, MutableList<PlayerDTO>>()
        val numberOfPlayers = registrationIds.size

        val remainder = numberOfPlayers.rem(categoryMetadata.playersPerGroup)
        var nrGroups = numberOfPlayers / categoryMetadata.playersPerGroup

        // If remainder is zero, number of registered players fits perfectly with
        // group size. Otherwise add one more group if it's groups of 4
        if (remainder != 0 && categoryMetadata.playersPerGroup == 4) {
            nrGroups += 1
        }

        val groupMap = mutableMapOf<Int, MutableList<Int>>()

        // Add lists to hold registration ids for each group
        var counter = 0
        while (counter < nrGroups) {
            groupMap.put(counter, mutableListOf())
            counter += 1
        }

        // Add seeded players to groups
        val categorySeedings = registrationRepository.getSeeds(competitionCategoryId)
        val seededPlayers = mutableListOf<Int>()
        for (id in registrationIds) {
            for (competitionSeed in categorySeedings) {
                if (competitionSeed.registrationId == id) {
                    groupMap[competitionSeed.seed - 1]?.add(id)
                    seededPlayers.add(id)
                }
            }
        }
        // Remove ids already added for seeding
        val remainingIds = registrationIds.filter { !seededPlayers.contains(it) }.toMutableList()
        // Add one player to each group. Start after nr of seeds.
        // If categoryseedings == nrGroups start from 0
        remainingIds.shuffle()
        counter = if (categorySeedings.size == nrGroups) 0 else categorySeedings.size
        for (id in remainingIds) {
            groupMap[counter]?.add(id)
            counter += 1

            if (counter == nrGroups) {
                counter = 0
            }
        }

        // Store matches in database
        for (groupNumber in groupMap.keys) {
            val registrationIdsInGroup = groupMap[groupNumber]
            if (registrationIdsInGroup == null) {
                logger.error("Draw failed, no registration ids found for group")
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Draw failed")
            }

            if (registrationIdsInGroup.size == 3) {
                drawUtil.setUpGroupOfThree(
                    registrationIdsInGroup,
                    drawUtil.getPoolName(groupNumber),
                    competitionCategoryId
                )
            } else if (registrationIdsInGroup.size == 4) {
                drawUtil.setUpGroupOfFour(
                    registrationIdsInGroup,
                    drawUtil.getPoolName(groupNumber),
                    competitionCategoryId
                )
            } else if (registrationIdsInGroup.size == 5) {
                // Match order == 2-3, 1-3, 1-2
            } else {
                logger.warn("Group size was different from 3, 4, 5. It was ${registrationIdsInGroup.size}. Pool draw failed")
            }
        }

        for (key in groupMap.keys) {
            playerGroups[key] = mutableListOf()
            for (registrationId in groupMap[key]!!) {
                // TODO: Remove this N+1 query, we should make a repository function that takes an array of registration ids instead
                val players = registrationService.getPlayersFromRegistrationId(registrationId)
                if (players.size > 1) {
                    println("Warning, more than one player returned for singles")
                }
                playerGroups[key]?.add(players[0])
            }
        }
    }

    fun deleteDraw(competitionCategoryId: Int) {
        matchRepository.deleteMatchesForCategory(competitionCategoryId)
        competitionDrawRepository.deleteGroupsInCategory(competitionCategoryId)
    }

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
