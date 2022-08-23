package com.graphite.competitionplanner.result.interfaces

import com.graphite.competitionplanner.result.service.GameDTO
import com.graphite.competitionplanner.result.service.ResultDTO

interface IResultRepository {

    /**
     * Return the game result for a given match
     *
     * @param matchId ID of the match
     * @return List of results
     */
    fun getResult(matchId: Int): List<GameDTO>

    /**
     * Get the results from all matches.
     *
     * @param matchIds IDs of all the matches to fetch result from
     * @return A list of Pairs where the first item is the match id, and the second is the result of the match with the
     * same id.
     */
    fun getResults(matchIds: List<Int>): MutableMap<Int, ResultDTO>

    /**
     * Delete all results from the match
     */
    fun deleteResults(matchId: Int)
}