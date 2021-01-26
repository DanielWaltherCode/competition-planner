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
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.lang.IllegalStateException
import java.time.LocalDateTime

@Service
class CompetitionDrawService(
    val categoryService: CategoryService,
    val registrationService: RegistrationService,
    val registrationRepository: RegistrationRepository,
    val matchRepository: MatchRepository,
    val matchService: MatchService,
    val competitionCategoryService: CompetitionCategoryService,
    val competitionDrawUtil: CompetitionDrawUtil,
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

        if (categoryMetadata.drawType.name == DrawTypes.POOL_ONLY.name ||
            categoryMetadata.drawType.name == DrawTypes.POOL_AND_CUP.name
        ) {
            createPoolDraw(registrationIds, categoryMetadata, competitionCategoryId)
        } else {
            createPlayOffs()
        }

        // Fetch the matches that have now been set up
        return getDraw(competitionCategoryId)
    }

    private fun createSeed(competitionCategoryId: Int) {
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
                .sortedBy { (key, value) -> value }
                .toMap()

        var numberOfSeeds = 4
        var seed = 0
        for((registrationId, _) in sortedRankings){
            registrationRepository.setSeed(registrationId, competitionCategoryId, seed)
            seed += 1

            if (seed >= numberOfSeeds) break
        }
    }

    private fun createPlayOffs() {
        TODO("Not yet implemented")
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

        // If remainder is zero, number of registered players fits perfectly with
        // group size. Otherwise add one more group
        val remainder = numberOfPlayers.rem(categoryMetadata.nrPlayersPerGroup)
        var nrGroups = numberOfPlayers / categoryMetadata.nrPlayersPerGroup
        if (remainder != 0) {
            nrGroups += 1
        }

        val groupMap = mutableMapOf<Int, MutableList<Int>>()

        // Add lists to hold registration ids for each group
        var counter = 0
        while (counter < nrGroups) {
            groupMap.put(counter + 1, mutableListOf())
            counter += 1
        }

        // TODO: Add seeding
        // Add one player to each group, then start over from the top
        counter = 0
        for (id in registrationIds) {
            groupMap[counter + 1]?.add(id)
            counter += 1

            if (counter == nrGroups) {
                counter = 0
            }
        }

        // Store matches in database
        for (groupNumber in groupMap.keys) {
            val registrationIdsInGroup = groupMap[groupNumber]
            // TODO: General algorithm for generic group size? Hard code for now
            if (registrationIdsInGroup?.size == 3) {
                competitionDrawUtil.setUpGroupOfThree(
                    registrationIdsInGroup,
                    competitionDrawUtil.getPoolName(groupNumber),
                    competitionCategoryId
                )

            } else if (registrationIdsInGroup?.size == 4) {
                competitionDrawUtil.setUpGroupOfFour(
                    registrationIdsInGroup,
                    competitionDrawUtil.getPoolName(groupNumber),
                    competitionCategoryId
                )
            } else if (registrationIdsInGroup?.size == 5) {
                // Match order == 2-3, 1-3, 1-2
            } else {
                logger.warn("Group size was different from 3, 4, 5. It was ${registrationIdsInGroup?.size}. Pool draw failed")
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
        val playoffMatches = matchService.getPlayoffMatchesInCategory(competitionCategoryId)
        val roundsAndPlayers = mutableMapOf<String, List<MatchDTO>>()

        val competitionCategory = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)
        return DrawDTO(
            getPoolDraw(competitionCategoryId),
            PlayoffDTO(competitionCategory, roundsAndPlayers)
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
    fun setUpPlayoffForGroups(competitionCategoryId: Int): PlayoffPlan {
        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)
        val matches = matchService.getGroupMatchesInCategory(competitionCategoryId)
        if (matches.isEmpty()) {
            logger.error("No group matches found before attempt to make playoff draw")
        }

        // Find out how many groups there are and how many matches there should be in first round
        // Number of matches == the first 2^x that is larger than nr players proceeding to playoffs
        val distinctGroups = matches.map { it.groupOrRound }.distinct()


        if (categoryMetadata.nrPlayersToPlayoff == 1) {
            return competitionDrawUtil.playoffForGroupsWhereOneProceeds(distinctGroups)
        }
        else if (categoryMetadata.nrPlayersToPlayoff == 2) {
            return PlayoffPlan(listOf())
        }
        else {
            logger.error("Unclear number of players continued from group: $categoryMetadata.nrPlayersToPlayoff")
        }
        return PlayoffPlan(listOf())
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
    POOL, PLAYOFF
}

data class GroupDrawDTO(
    val competitionCategory: CompetitionCategory,
    // Todo: handle doubles in this draw
    val groups: MutableMap<String, List<MatchDTO>>
)

data class PlayoffDTO(
    val competitionCategory: CompetitionCategory,
    val matches: MutableMap<String, List<MatchDTO>>
)