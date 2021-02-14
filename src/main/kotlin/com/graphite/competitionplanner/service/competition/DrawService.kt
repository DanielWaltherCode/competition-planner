package com.graphite.competitionplanner.service.competition

import com.graphite.competitionplanner.api.competition.DrawDTO
import com.graphite.competitionplanner.repositories.DrawTypes
import com.graphite.competitionplanner.repositories.MatchRepository
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategory
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.tables.DrawType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.lang.IllegalStateException
import java.time.LocalDateTime
import kotlin.math.log2

@Service
class DrawService(
    val categoryService: CategoryService,
    val registrationService: RegistrationService,
    val registrationRepository: RegistrationRepository,
    val matchRepository: MatchRepository,
    val matchService: MatchService,
    val competitionCategoryService: CompetitionCategoryService,
    val drawUtil: DrawUtil,
    val drawUtilTwoProceed: DrawUtilTwoProceed,
    val playerRepository: PlayerRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)


    fun createDraw(competitionCategoryId: Int): DrawDTO {
        // Check draw type and fetch players signed up in this category
        // its easier to work with registration ids since it could be doubles and then translate back to
        // players after the draw is made

        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId);
        val categoryMetadata: CategoryMetadataDTO
        try {
            categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)
        } catch (ex: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Category metadata not found for that id")
        }

        // First create seed
        createSeed(competitionCategoryId, categoryMetadata)

        if (categoryMetadata.drawType.name == DrawTypes.POOL_ONLY.name ||
            categoryMetadata.drawType.name == DrawTypes.POOL_AND_CUP.name
        ) {
            createPoolDraw(registrationIds, categoryMetadata, competitionCategoryId)
        } else {
            createPlayOffs(registrationIds, competitionCategoryId)
        }

        // Fetch the matches that have now been set up
        return getDraw(competitionCategoryId)
    }

    private fun createSeed(competitionCategoryId: Int, categoryMetadata: CategoryMetadataDTO) {
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId);
        val playerList = mutableMapOf<Int, List<PlayerDTO>>();
        for(id in registrationIds){
            playerList[id] = registrationService.getPlayersFromRegistrationId(id)
        }

        val categoryType = competitionCategoryRepository.getCategoryType(competitionCategoryId).categoryType

        val rankings = mutableMapOf<Int, Int>()

        for((registrationId, players) in playerList){

            var sum = 0
            for(player in players){
                if(categoryType == "SINGLES"){
                    sum += playerRepository.getPlayerRanking(player.id)?.rankSingle ?: 0
                }else if(categoryType == "DOUBLES"){
                    sum += playerRepository.getPlayerRanking(player.id)?.rankDouble ?: 0
                }
            }

            rankings[registrationId] = sum
        }

        val sortedRankings = rankings.toList()
                .sortedBy { (key, value) -> -value }
                .toMap()

        val numberOfSeeds = drawUtil.getNumberOfSeeds(categoryMetadata.nrPlayersPerGroup, sortedRankings.size)
        var seed = 1
        for((registrationId, _) in sortedRankings){
            registrationRepository.setSeed(registrationId, competitionCategoryId, seed)
            seed += 1

            if (seed > numberOfSeeds) break
        }
    }

    private fun createPlayOffs(
        registrationIds: List<Int>,
        competitionCategoryId: Int)
    {
        // Get seedings
        val competitionSeedings = registrationRepository.getSeeds(competitionCategoryId)

        val matches = drawUtil.createDirectToPlayoff(competitionCategoryId, registrationIds)
        for (match in matches){
            matchRepository.addMatch(match)
        }
    }

    fun createPoolDraw(
        registrationIds: List<Int>,
        categoryMetadata: CategoryMetadataDTO,
        competitionCategoryId: Int
    ) {
        // If draw has already been made, first remove old matches
        if (matchRepository.isPoolDrawn(competitionCategoryId)) {
            matchRepository.deleteMatchesForCategory(competitionCategoryId)
        }

        val playerGroups = mutableMapOf<Int, MutableList<PlayerDTO>>()
        val numberOfPlayers = registrationIds.size

        val remainder = numberOfPlayers.rem(categoryMetadata.nrPlayersPerGroup)
        var nrGroups = numberOfPlayers / categoryMetadata.nrPlayersPerGroup

        // If remainder is zero, number of registered players fits perfectly with
        // group size. Otherwise add one more group if it's groups of 4
        if (remainder != 0 && categoryMetadata.nrPlayersPerGroup == 4) {
            nrGroups += 1
        }

        val groupMap = mutableMapOf<Int, MutableList<Int>>()

        // Add lists to hold registration ids for each group
        var counter = 0
        while (counter < nrGroups) {
            groupMap.put(counter, mutableListOf())
            counter += 1
        }

        // TODO: Add seeded players first
        val competitionSeedings = registrationRepository.getSeeds(competitionCategoryId)
        val seededPlayers = mutableListOf<Int>()
        for (id in registrationIds) {
            for (competitionSeed in competitionSeedings) {
                if (competitionSeed.registrationId == id) {
                    groupMap[competitionSeed.seed]?.add(id)
                    seededPlayers.add(id)
                }
            }
        }
        // Remove ids already added for seeding
        val remainingIds = registrationIds.filter { !seededPlayers.contains(it) }
        // Add one player to each group. (If there are 4 players per group start from top, if 3 start from bottom)
        counter = 0 + competitionSeedings.size
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
                val players = registrationService.getPlayersFromRegistrationId(registrationId)
                if (players.size > 1) {
                    println("Warning, more than one player returned for singles")
                }
                playerGroups[key]?.add(players[0])
            }
        }
    }

    fun getDraw(competitionCategoryId: Int): DrawDTO {
        val metadata = categoryService.getCategoryMetadata(competitionCategoryId)
        if (metadata.drawType.id == 2){ // 2 == CUP ONLY. How is it set in database though?
            return getCupOnlyDraw(competitionCategoryId)
        }
        return DrawDTO(
            getPoolDraw(competitionCategoryId),
            getPlayoffForGroups(competitionCategoryId)
        )
    }

    fun getCupOnlyDraw(competitionCategoryId: Int): DrawDTO{
        val competitionCategory = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)
        val groupsAndPlayers = mutableMapOf<String, List<MatchDTO>>()

        val playoffRound = mutableListOf<PlayoffRound>()
        val matches = matchService.getMatchesInCategory(competitionCategoryId)
        val round = drawUtil.getRound(matches.size)
        val matchUps = mutableListOf<MatchUp>()

        for (match in matches){
            matchUps.add(
                MatchUp(
                    player1 = match.firstPlayer[0].firstName,
                    player2 = match.secondPlayer[0].firstName
                )
            )
        }
        playoffRound.add(PlayoffRound(round, matchUps))
        return DrawDTO(
            GroupDrawDTO(
                competitionCategory,
                groupsAndPlayers),
            PlayoffDTO(
                competitionCategory,
                playoffRound
            )
        )
    }

    fun getPoolDraw(competitionCategoryId: Int): GroupDrawDTO {
        val competitionCategory = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)

        val groupMatches = matchService.getGroupMatchesInCategory(competitionCategoryId)
        val groupsAndPlayers = mutableMapOf<String, List<MatchDTO>>()

        if (groupMatches.isNotEmpty()) {
            val distinctGroups = groupMatches.map { it.groupOrRound }.distinct()
            for (group in distinctGroups) {
                val matchesInGroup = mutableListOf<MatchDTO>()
                for (match in groupMatches) {
                    if (match.groupOrRound.equals(group)) {
                        matchesInGroup.add(match)
                    }
                }
                groupsAndPlayers.put(group, matchesInGroup)
            }
        }
        return GroupDrawDTO(competitionCategory, groupsAndPlayers)
    }


    /**
     * This method is only called to create the "empty" playoff draw before group stage is played
     * So it can say that A1 will play againt E2 in the first match, D1-F2 in the second etc. After group
     * stage actual players name will be entered.
     */
    fun getPlayoffForGroups(competitionCategoryId: Int): PlayoffDTO {
        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)
        val groupMatches = matchService.getGroupMatchesInCategory(competitionCategoryId)
        if (groupMatches.isEmpty()) {
            logger.error("No group matches found before attempt to make playoff draw")
        }

        // Find out how many groups there are and how many matches there should be in first round
        // Number of matches == the first 2^x that is larger than nr players proceeding to playoffs
        val distinctGroups = groupMatches.map { it.groupOrRound }.distinct()

        val playoffMatches: List<MatchUp>
        if (categoryMetadata.nrPlayersToPlayoff == 1) {
            playoffMatches = drawUtil.playoffForGroupsWhereOneProceeds(distinctGroups)
        }
        else if (categoryMetadata.nrPlayersToPlayoff == 2) {
            playoffMatches = drawUtilTwoProceed.playoffDrawWhereTwoProceed(distinctGroups)
        }
        else {
            logger.error("Unclear number of players continued from group: $categoryMetadata.nrPlayersToPlayoff")
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Number of players going to playoff should be 1 or 2")
        }
        val round = drawUtil.getRound(playoffMatches.size)
        val playoffRound = mutableListOf<PlayoffRound>()
        playoffRound.add(PlayoffRound(round, playoffMatches))
        return PlayoffDTO(competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId), playoffRound)
    }
}

data class Match(
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
    val competitionCategory: CompetitionCategory,
    // Todo: handle doubles in this draw
    val groups: MutableMap<String, List<MatchDTO>>
)

data class PlayoffDTO(
    val competitionCategory: CompetitionCategory,
    val rounds: List<PlayoffRound>
)

data class PlayoffRound(
    val round: Round,
    val matches: List<MatchUp>
)