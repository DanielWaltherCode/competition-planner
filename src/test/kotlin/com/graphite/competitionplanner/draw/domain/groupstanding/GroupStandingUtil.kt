package com.graphite.competitionplanner.draw.domain.groupstanding

import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class GroupStandingUtil(val resultService: ResultService, val matchRepository: MatchRepository) {
    fun winAgainstPlayersX(mainPlayer: Int, playersToBeat: List<Int>, competitionCategoryId: Int) {
        val matches = matchRepository.getMatchesInCategory(competitionCategoryId)
        val matchesInGroupA = matches.filter { it.groupOrRound == "A" }
        // Select all matches where the main player is one of the parties and the other party is one of the players to beat
        val selectedMatches = matchesInGroupA.filter {
            (it.firstRegistrationId == mainPlayer || it.secondRegistrationId == mainPlayer)
                    && (playersToBeat.contains(it.firstRegistrationId) || playersToBeat.contains(it.secondRegistrationId))
                    && it.winner == null
        }

        for (match in selectedMatches) {
            if (match.firstRegistrationId == mainPlayer) {
                generateAndSaveResult(match, PlayerPosition.FIRST)
            } else if (match.secondRegistrationId == mainPlayer) {
                generateAndSaveResult(match, PlayerPosition.SECOND)
            }
        }
    }

    fun generateAndSaveResult(match: MatchRecord, playerPosition: PlayerPosition) {
        val nrGames = Random.nextInt(3, 6)

        val winningPlayerResults = mutableListOf<Int>()
        while (winningPlayerResults.size < nrGames) {
            winningPlayerResults.add(Random.nextInt(0, 12))
        }
        while (winningPlayerResults.count { it == 11 } < 3) {
            val gameToRemove = Random.nextInt(0, nrGames)
            winningPlayerResults.removeAt(gameToRemove)
            val value = Random.nextInt(4, 12)
            winningPlayerResults.add(value)
        }
        val gameResults = mutableListOf<GameSpec>()
        for ((index, winningPlayerResult) in winningPlayerResults.withIndex()) {
            val otherPlayerResult = when (winningPlayerResult) {
                10 -> 12
                11 -> Random.nextInt(0, 10)
                else -> 11
            }
            if (playerPosition == PlayerPosition.FIRST) {
                gameResults.add(GameSpec(index + 1, winningPlayerResult, otherPlayerResult))
            } else {
                gameResults.add(GameSpec(index + 1, otherPlayerResult, winningPlayerResult))
            }
        }
        val resultSpec = ResultSpec(gameResults)
        resultService.addResult(match.id, resultSpec)

    }

    fun beatPlayerWithExactScore(
        mainPlayer: Int,
        playerToBeat: Int,
        mainPlayerResults: List<Int>,
        playerToBeatResults: List<Int>,
        competitionCategoryId: Int
    ) {
        val matches = matchRepository.getMatchesInCategory(competitionCategoryId)
        val matchesInGroupA = matches.filter { it.groupOrRound == "A" }

        // Select their match
        val match = matchesInGroupA.first {
            (it.firstRegistrationId == mainPlayer || it.secondRegistrationId == mainPlayer)
                    && (playerToBeat == it.firstRegistrationId || playerToBeat == it.secondRegistrationId)
                    && it.winner == null
        }

        val gameResults: MutableList<GameSpec> = mutableListOf()
        if (match.firstRegistrationId == mainPlayer) {
            for (i in mainPlayerResults.indices) {
                gameResults.add(GameSpec(i + 1, mainPlayerResults[i], playerToBeatResults[i]))
            }
        } else if (match.secondRegistrationId == mainPlayer) {
            for (i in mainPlayerResults.indices) {
                gameResults.add(GameSpec(i + 1, playerToBeatResults[i], mainPlayerResults[i]))
            }
        }
        resultService.addResult(match.id, ResultSpec(gameResults))
    }

    enum class PlayerPosition {
        FIRST, SECOND
    }
}