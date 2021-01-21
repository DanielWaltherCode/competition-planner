package com.graphite.competitionplanner.service.competition

import com.graphite.competitionplanner.repositories.MatchRepository
import org.springframework.stereotype.Service

@Service
class CompetitionDrawUtil(val matchRepository: MatchRepository) {
    fun setUpGroupOfThree(registrationIds: MutableList<Int>, groupName: String, competitionCategoryId: Int) {
        // Match order == 2-3, 1-3, 1-2
        val player1 = registrationIds[0]
        val player2 = registrationIds[1]
        val player3 = registrationIds[2]

        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player2,
                secondRegistrationId = player3,
                matchOrderNumber = 1,
                groupOrRound = groupName
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
                groupOrRound = groupName
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
                groupOrRound = groupName
            )
        )
    }

    fun setUpGroupOfFour(registrationIds: MutableList<Int>, groupName: String, competitionCategoryId: Int) {
        // Match order == 2-3, 1-4, 2-4, 1-3, 3-4, 1-2
        val player1 = registrationIds[0]
        val player2 = registrationIds[1]
        val player3 = registrationIds[2]
        val player4 = registrationIds[3]

        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player2,
                secondRegistrationId = player3,
                matchOrderNumber = 1,
                groupOrRound = groupName
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
                groupOrRound = groupName
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
                groupOrRound = groupName
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
                groupOrRound = groupName
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
                groupOrRound = groupName
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
                groupOrRound = groupName
            )
        )
    }

    fun setUpGroupOfFive(registrationIds: MutableList<Int>, groupName: String, competitionCategoryId: Int) {
        // Match order == 2-3, 4-5, 1-5, 3-4, 1-4, 2-5, 1-2, 3-5, 1-3, 2-4
        val player1 = registrationIds[0]
        val player2 = registrationIds[1]
        val player3 = registrationIds[2]
        val player4 = registrationIds[3]
        val player5 = registrationIds[4]

        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player2,
                secondRegistrationId = player3,
                matchOrderNumber = 1,
                groupOrRound = groupName
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player4,
                secondRegistrationId = player5,
                matchOrderNumber = 2,
                groupOrRound = groupName
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player1,
                secondRegistrationId = player5,
                matchOrderNumber = 3,
                groupOrRound = groupName
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
                matchOrderNumber = 4,
                groupOrRound = groupName
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
                matchOrderNumber = 5,
                groupOrRound = groupName
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player2,
                secondRegistrationId = player5,
                matchOrderNumber = 6,
                groupOrRound = groupName
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
                matchOrderNumber = 7,
                groupOrRound = groupName
            )
        )
        matchRepository.addMatch(
            Match(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.POOL,
                firstRegistrationId = player3,
                secondRegistrationId = player5,
                matchOrderNumber = 8,
                groupOrRound = groupName
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
                matchOrderNumber = 9,
                groupOrRound = groupName
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
                matchOrderNumber = 10,
                groupOrRound = groupName
            )
        )
    }

    fun getPoolNameMap(): MutableMap<Int, String> {
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
        val map = getPoolNameMap()
        val name = map[poolNumber]
        if (name == null) {
            return ""
        }
        return name
    }

    fun playoffForGroupsWhereOneProceeds(groups: List<String> ): PlayoffPlan {
        val playOrder = getPlayoffOrderWhereOneProceeds(groups.size)
        val playerPositions = playOrder.map { "BYE" }.toMutableList()

        // Create list with player and opponent (or BYE if there is no opponent)
        for (groupNumber in groups.indices) {
            print(groupNumber)
            for (i in playOrder.indices) {
                if (groupNumber + 1 == playOrder[i]) {
                    playerPositions[i] = groups[groupNumber] + "1"
                    continue
                }
            }
        }

        // Convert raw list to match up list
        val matchUps = mutableListOf<MatchUp>()
        for (i in playerPositions.indices step 2) {
                matchUps.add(MatchUp(playerPositions[i], playerPositions[i + 1]))
            }

        return PlayoffPlan(matchUps)
    }

    fun getPossiblePlayoffNumbers() = listOf(2, 4, 8, 16, 32, 64, 128, 256)

    fun getNumberOfPlaysInFirstPlayoffRound(nrPlayersToPlayoff: Int): Int {
        for (number in getPossiblePlayoffNumbers()) {
            if (number >= nrPlayersToPlayoff) {
                return number
            }
        }
        return 0
    }

    fun getPlayoffOrderWhereOneProceeds(nrGroups: Int): List<Int> {
        val numberOfPositionsInPlayoff = getNumberOfPlaysInFirstPlayoffRound(nrGroups)
        when(numberOfPositionsInPlayoff) {
            2 -> return listOf(1, 2)
            4 -> return listOf(1, 4, 3, 2)
            8 -> return listOf(1, 8, 5, 4, 3, 6, 7, 2)
            16 -> return listOf(
                1, 16,
                12, 5,
                4, 13,
                8, 9,
                7, 10,
                3, 14,
                6, 11,
                15, 2)
            else -> return emptyList()
        }
    }
}


data class PlayoffPlan(
    val matchUps: List<MatchUp>
)

data class MatchUp(
    val player1: String,
    val player2: String
)