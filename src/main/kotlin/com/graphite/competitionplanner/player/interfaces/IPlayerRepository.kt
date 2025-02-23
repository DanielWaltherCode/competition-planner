package com.graphite.competitionplanner.player.interfaces

import com.graphite.competitionplanner.common.exception.NotFoundException

interface IPlayerRepository {

    /**
     * Store a new player together with a ranking. By default, the ranking is set to zero.
     *
     * @param spec Player to be stored
     * @param rankingSpec Ranking information
     * @return Newly stored player
     */
    fun store(spec: PlayerSpec, rankingSpec: PlayerRankingSpec = PlayerRankingSpec(0, 0)): PlayerDTO

    /**
     * Store a new player as part of a competition together with a ranking. By default, the ranking is set to zero.
     *
     * @param spec Player to be stored
     * @param rankingSpec Ranking information
     * @return Newly stored player
     */
    fun storeInCompetition(competitionId: Int, spec: PlayerSpec, rankingSpec: PlayerRankingSpec = PlayerRankingSpec(0, 0)): PlayerDTO

    /**
     * Return all players for the given club
     */
    fun playersInClub(clubId: Int): List<PlayerWithClubDTO>

    /**
     * Return the Player with the given ID
     *
     * @param id: ID of the player
     * @return Player
     * @throws NotFoundException If the Player with the given ID cannot be found
     */
    @Throws(NotFoundException::class)
    fun findById(id: Int): PlayerDTO

    /**
     * Return the players that have the given ids
     *
     * Any id that does not match an actual player will be ignored.
     *
     * @param playerIds Ids of the players
     * @return List of players
     */
    fun findAllForIds(playerIds: List<Int>): List<PlayerWithClubDTO>

    /**
     * Return a list of players where the first name starts with the given search string.
     * @param competitionId
     * @param startOfName: Search string
     * @return List of Players
     */
    fun findByName(startOfName: String): List<PlayerWithClubDTO>

    /**
     * Return a list of players where the first name starts with the given search string. Returns all general players
     * not associated with a specific competition or those added specifically in this competition
     * @param competitionId
     * @param startOfName: Search string
     * @return List of Players
     */
    fun findByNameWithCompetition(competitionId: Int, startOfName: String): List<PlayerWithClubDTO>

    /**
     * Return a list of players where the first name or last name starts with the given search string, and the player
     * is registered to the competition with the given id.
     *
     * @param startOfName: Search string
     * @param competitionId: ID of the competition
     * @return List of players
     */
    fun findByNameInCompetition(startOfName: String, competitionId: Int): List<PlayerWithClubDTO>

    /**
     * Updates the Player with the given id
     *
     * @param id: ID of the player to update
     * @param spec: Specification that will be used to update the player
     * @return The updated player
     * @throws NotFoundException If the Player to updated cannot be found
     */
    @Throws(NotFoundException::class)
    fun update(id: Int, spec: PlayerSpec): PlayerDTO

    /**
     * Deletes the Player
     *
     * @param id: ID of the player to delete
     * @return The deleted player
     * @throws NotFoundException If the Player to delete cannot be found
     */
    @Throws(NotFoundException::class)
    fun delete(id: Int): PlayerDTO

    /**
     * Add ranking
     */
    fun addPlayerRanking(playerId: Int, rankToAdd: Int, categoryType: String)
}