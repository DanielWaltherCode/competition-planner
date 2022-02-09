package com.graphite.competitionplanner.result.interfaces

import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.service.GameDTO

interface IResultRepository {

    /**
     * Store a new result to the given match
     * @param matchId ID of the match
     * @return The newly stored game result
     */
    fun storeResult(matchId: Int, gameResult: GameSpec): GameDTO

    /**
     * Get the results for the given match
     * @param matchId ID of the match
     * @return List of game results for the match
     */
    fun getResults(matchId: Int): List<GameDTO>

    /**
     * Delete all results from the match
     */
    fun deleteResults(matchId: Int)
}