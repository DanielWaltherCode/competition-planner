package com.graphite.competitionplanner.service.competition

import com.graphite.competitionplanner.repositories.DrawTypes
import com.graphite.competitionplanner.repositories.MatchRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategory
import com.graphite.competitionplanner.service.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CompetitionDrawService(
    val categoryService: CategoryService,
    val registrationService: RegistrationService,
    val registrationRepository: RegistrationRepository,
    val matchRepository: MatchRepository,
    val matchService: MatchService,
    val competitionCategoryService: CompetitionCategoryService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getPoolDraw(competitionCategoryId: Int): PoolDrawDTO {
        val competitionCategory = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)

        var matches = matchService.getMatchesInCategory(competitionCategoryId)
        // Keep only pool matches
        matches = matches.filter { it.matchType.toLowerCase().equals("pool") }
        val distinctGroupNames = matches.map { it.groupOrRound }.distinct()

        val groupMap = mutableMapOf<String, MutableList<List<PlayerDTO>>>()
        for (group in distinctGroupNames) {
            groupMap[group] = mutableListOf()
            val distinctRegistrationsInGroup =
                matchRepository.getDistinctRegistrationIdsInGroup(competitionCategoryId, group)
            for (id in distinctRegistrationsInGroup) {
                val players = registrationService.getPlayersFromRegistrationId(id)
                groupMap[group]?.add(players)
            }
        }
        return PoolDrawDTO(competitionCategory, groupMap)
    }

    fun createDraw(competitionCategoryId: Int): List<MatchDTO> {
        // Check draw type and fetch players signed up in this category
        // its easier to work with registration ids since it could be doubles and then translate back to
        // players after the draw is made

        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId);
        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)

        if (categoryMetadata.drawType.name == DrawTypes.POOL_ONLY.name ||
            categoryMetadata.drawType.name == DrawTypes.POOL_AND_CUP.name
        ) {
            createPoolDraw(registrationIds, categoryMetadata, competitionCategoryId)
        }
        return matchService.getMatchesInCategory(competitionCategoryId)
    }

    fun createPoolDraw(
        registrationIds: List<Int>,
        categoryMetadata: CategoryMetadataDTO,
        competitionCategoryId: Int
    ) {
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
            val group = groupMap[groupNumber]
            // TODO: General algorithm for generic group size? Hard code for now
            if (group?.size == 3) {
                setUpGroupOfThree(group, groupNumber, competitionCategoryId)

            } else if (group?.size == 4) {
                setUpGroupOfFour(group, groupNumber, competitionCategoryId)
            } else if (group?.size == 5) {
                // Match order == 2-3, 1-3, 1-2
            } else {
                logger.warn("Group size was different from 3, 4, 5. It was ${group?.size}. Pool draw failed")
            }
        }

        // TODO: Decide how to handle doubles
        // For now, replace registration ids with PlayerDTO
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

    fun setUpGroupOfThree(group: MutableList<Int>, groupNumber: Int, competitionCategoryId: Int) {
        // Match order == 2-3, 1-3, 1-2
        val player1 = group[0]
        val player2 = group[1]
        val player3 = group[2]

        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player2,
                secondRegistrationId = player3,
                matchOrderNumber = 1,
                groupOrRound = getPoolName(groupNumber)
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player1,
                secondRegistrationId = player3,
                matchOrderNumber = 2,
                groupOrRound = getPoolName(groupNumber)
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player1,
                secondRegistrationId = player2,
                matchOrderNumber = 3,
                groupOrRound = getPoolName(groupNumber)
            )
        )
    }

    fun setUpGroupOfFour(group: MutableList<Int>, groupNumber: Int, competitionCategoryId: Int) {
        // Match order == 2-3, 1-4, 2-4, 1-3, 3-4, 1-2
        val player1 = group[0]
        val player2 = group[1]
        val player3 = group[2]
        val player4 = group[3]

        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player2,
                secondRegistrationId = player3,
                matchOrderNumber = 1,
                groupOrRound = getPoolName(groupNumber)
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player1,
                secondRegistrationId = player4,
                matchOrderNumber = 2,
                groupOrRound = getPoolName(groupNumber)
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player2,
                secondRegistrationId = player4,
                matchOrderNumber = 3,
                groupOrRound = getPoolName(groupNumber)
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player1,
                secondRegistrationId = player3,
                matchOrderNumber = 4,
                groupOrRound = getPoolName(groupNumber)
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player3,
                secondRegistrationId = player4,
                matchOrderNumber = 5,
                groupOrRound = getPoolName(groupNumber)
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player1,
                secondRegistrationId = player2,
                matchOrderNumber = 6,
                groupOrRound = getPoolName(groupNumber)
            )
        )
    }

    fun poolNameMap(): MutableMap<Int, String> {
        val poolNameMap: MutableMap<Int, String> = mutableMapOf()
        poolNameMap[1] = "A"
        poolNameMap[2] = "B"
        poolNameMap[3] = "C"
        poolNameMap[4] = "D"
        poolNameMap[5] = "E"
        poolNameMap[6] = "F"
        poolNameMap[7] = "G"
        poolNameMap[8] = "H"
        poolNameMap[9] = "I"
        poolNameMap[10] = "J"
        poolNameMap[11] = "K"
        poolNameMap[12] = "L"
        poolNameMap[13] = "M"
        poolNameMap[14] = "N"
        poolNameMap[15] = "O"
        poolNameMap[16] = "P"
        poolNameMap[17] = "Q"
        poolNameMap[18] = "R"
        poolNameMap[19] = "S"
        poolNameMap[20] = "T"
        poolNameMap[21] = "U"
        poolNameMap[22] = "V"
        poolNameMap[23] = "W"
        return poolNameMap
    }

    fun getPoolName(poolNumber: Int): String {
        val map = poolNameMap()
        val name = map[poolNumber]
        if (name == null) {
            return ""
        }
        return name
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

data class PoolDrawDTO(
    val competitionCategory: CompetitionCategory,
    // Todo: handle doubles in this draw
    val pools: MutableMap<String, MutableList<List<PlayerDTO>>>
)