package com.graphite.competitionplanner.match.domain

import com.graphite.competitionplanner.draw.service.MatchSpec

interface IMatchRepository {

    /**
     * Return the match with the given id
     *
     * @param matchId ID of the match
     * @return The match with the given id
     */
    fun getMatch2(matchId: Int): Match

    /**
     * Store a new match
     *
     * @param spec Specification of the match
     * @return The stored match
     */
    fun store(spec: MatchSpec): Match

}