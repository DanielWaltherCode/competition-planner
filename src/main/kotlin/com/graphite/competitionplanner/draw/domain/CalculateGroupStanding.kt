package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.GroupStandingDTO
import com.graphite.competitionplanner.draw.interfaces.WonLostDTO
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Component

@Component
class CalculateGroupStanding {

    fun execute(groupMatches: List<MatchAndResultDTO>): List<GroupStandingDTO> {
        val uniquePlayers: MutableSet<List<PlayerWithClubDTO>> = mutableSetOf()
        for (match in groupMatches) {
            uniquePlayers.add(match.firstPlayer.sortedBy { p -> p.id })
            uniquePlayers.add(match.secondPlayer.sortedBy { p -> p.id })
        }

        val groupStandingList = mutableListOf<GroupStandingDTO>()
        for (player in uniquePlayers) {
            var matchesWon = 0
            var matchesLost = 0
            var pointsWon = 0
            var pointsLost = 0
            var gamesWon = 0
            var gamesLost = 0
            for (match in groupMatches) {
                if (match.winner.isNotEmpty()) {
                    if (player.sortedBy { p -> p.id  } == match.firstPlayer.sortedBy { fp -> fp.id }) {
                        if (match.winner.sortedBy { w -> w.id } == player.sortedBy { p -> p.id }) matchesWon += 1 else matchesLost += 1
                        for (game in match.result.gameList) {
                            val playerPointsInGame = game.firstRegistrationResult
                            val otherPlayerPointsInGame = game.secondRegistrationResult
                            if (playerPointsInGame > otherPlayerPointsInGame) gamesWon +=1 else gamesLost += 1
                            pointsWon += playerPointsInGame
                            pointsLost += otherPlayerPointsInGame
                        }
                    }
                    else if(player.sortedBy { p -> p.id  } == match.secondPlayer.sortedBy { fp -> fp.id }) {
                        if (match.winner.sortedBy { w -> w.id } == player.sortedBy { p -> p.id }) matchesWon += 1 else matchesLost += 1
                        for (game in match.result.gameList) {
                            val playerPointsInGame = game.secondRegistrationResult
                            val otherPlayerPointsInGame = game.firstRegistrationResult
                            if (playerPointsInGame > otherPlayerPointsInGame) gamesWon +=1 else gamesLost += 1
                            pointsWon += playerPointsInGame
                            pointsLost += otherPlayerPointsInGame
                        }
                    }
                }
            }
            groupStandingList.add(
                GroupStandingDTO(
                player = player,
                matchesWon = matchesWon,
                matchesPlayed = matchesWon + matchesLost,
                matchesWonLost = WonLostDTO(matchesWon, matchesLost),
                gamesWonLost = WonLostDTO(gamesWon, gamesLost),
                pointsWonLost = WonLostDTO(pointsWon, pointsLost)
                )
            )
        }
        return groupStandingList
    }
}