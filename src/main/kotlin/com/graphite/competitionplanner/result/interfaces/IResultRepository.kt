package com.graphite.competitionplanner.result.interfaces

import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.service.GameDTO
import com.graphite.competitionplanner.result.service.ResultDTO

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
     * Get the results from all matches.
     *
     * @param matchIds IDs of all the matches to fetch result from
     * @return A list of Pairs where the first item is the match id, and the second is the result of the match with the
     * same id.
     */
    fun getResults(matchIds: List<Int>): List<Pair<Int, ResultDTO>>

    /**
     * Delete all results from the match
     */
    fun deleteResults(matchId: Int)
}