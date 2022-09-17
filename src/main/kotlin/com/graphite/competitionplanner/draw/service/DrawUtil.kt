package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.match.repository.MatchRepository
import org.springframework.stereotype.Service

@Service
class DrawUtil(
    val matchRepository: MatchRepository
)
{

    fun getNrPlayersInGroup(nrGroupMatches: Int): Int {
        return when(nrGroupMatches) {
            3 -> 3
            6 -> 4
            10 -> 5
            else -> 0
        }
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

