package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import org.springframework.stereotype.Service

@Service
class DrawUtil(val matchRepository: MatchRepository, val competitionDrawRepository: CompetitionDrawRepository) {

    fun getNrPlayersInGroup(nrGroupMatches: Int): Int {
        return when(nrGroupMatches) {
            3 -> 3
            6 -> 4
            10 -> 5
            else -> 0
        }
    }

    fun getPoolNameMap(): MutableMap<Int, String> {
        val poolNameMap: MutableMap<Int, String> = mutableMapOf()
        poolNameMap[0] = "A"
        poolNameMap[1] = "B"
        poolNameMap[2] = "C"
        poolNameMap[3] = "D"
        poolNameMap[4] = "E"
        poolNameMap[5] = "F"
        poolNameMap[6] = "G"
        poolNameMap[7] = "H"
        poolNameMap[8] = "I"
        poolNameMap[9] = "J"
        poolNameMap[10] = "K"
        poolNameMap[11] = "L"
        poolNameMap[12] = "M"
        poolNameMap[13] = "N"
        poolNameMap[14] = "O"
        poolNameMap[15] = "P"
        poolNameMap[16] = "Q"
        poolNameMap[17] = "R"
        poolNameMap[18] = "S"
        poolNameMap[19] = "T"
        poolNameMap[20] = "U"
        poolNameMap[21] = "V"
        poolNameMap[22] = "W"
        return poolNameMap
    }

    fun getNumberOfSeeds(nrPlayersPerGroup: Int, numberOfRegisteredPlayers: Int): Int {
        when(numberOfRegisteredPlayers) {
            in 0..5 -> return 1
            in 6..11 -> return 2
            in 12..24 -> return 4
            else -> return 8
        }
    }

    fun getPoolName(poolNumber: Int): String {
        val map = getPoolNameMap()
        val name = map[poolNumber]
        if (name == null) {
            return ""
        }
        return name
    }

    fun playoffForGroupsWhereOneProceeds(groups: List<String> ): List<MatchUp> {
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

        return matchUps
    }

    fun createDirectToPlayoff(competitionCategoryId: Int, registrationIds: List<Int>): List<MatchSpec> {
        val playOrder = getPlayoffOrderWhereOneProceeds(registrationIds.size)
        val registrationPositions = playOrder.map { 0 }.toMutableList()    // Create list with player and opponent (or BYE if there is no opponent)
        for (idOrder in registrationIds.indices) {
            for (i in playOrder.indices) {
                if (idOrder + 1 == playOrder[i]) {
                    registrationPositions[i] = registrationIds[idOrder]
                    continue
                }
            }
        }
        // Convert raw list to match up list
        val round = getRound(playOrder.size / 2)
        val matches = mutableListOf<MatchSpec>()
        var matchOrderNumber = 1
        for (i in registrationPositions.indices step 2) {
            val match = MatchSpec(
                startTime = null,
                endTime = null,
                competitionCategoryId = competitionCategoryId,
                matchType = MatchType.PLAYOFF,
                firstRegistrationId = registrationPositions[i],
                secondRegistrationId = registrationPositions[i+1],
                matchOrderNumber = matchOrderNumber,
                groupOrRound = round.name
            )
            matches.add(match)
            matchOrderNumber += 1
        }
        return matches
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
        when (numberOfPositionsInPlayoff) {
            2 -> return listOf(1, 2)
            4 -> return listOf(1, 4, 3, 2) //   4, 1, 2, 3
            8 -> return listOf(1, 8, 5, 4, 3, 6, 7, 2)  //     4, 1, 2, 3
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

    fun getRound(nrMatches: Int): Round {
       return when(nrMatches) {
           1 -> Round.FINAL
           2 -> Round.SEMI_FINAL
           4 -> Round.QUARTER_FINAL
           8 -> Round.ROUND_OF_16
           16 -> Round.ROUND_OF_32
           32 -> Round.ROUND_OF_64
               else -> Round.UNKNOWN
       }
    }
}


data class MatchUp(
    val player1: String,
    val player2: String
)

data class PoolDrawHelper(
    val registrationId: Int,
    val competitionCategoryId: Int,
    val groupName: String,
    val playerNumber: Int
)
