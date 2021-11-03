package com.graphite.competitionplanner.player.interfaces

import com.graphite.competitionplanner.common.exception.NotFoundException

interface IPlayerRepository {

    /**
     * Saves the Player
     *
     * @param spec Player to be stored
     * @return Newly stored player
     */
    fun store(spec: PlayerSpec): PlayerDTO

    /**
     * Return all players for the given club
     */
    fun playersInClub(clubId: Int): List<PlayerWithClubDTO>

    /**
     * Return the Player with the given Id
     *
     * @param id: Id of the player
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
     * Return a list of players where the first name or last name
     * starts with the given search string
     *
     * @param startOfName: Search string
     * @Return List of Players
     */
    fun findByName(startOfName: String): List<PlayerWithClubDTO>

    /**
     * Updates the Player with the given id
     *
     * @param id: Id of the player to update
     * @param spec: Specification that will be used to update the player
     * @return The updated player
     * @throws NotFoundException If the Player to updated cannot be found
     */
    @Throws(NotFoundException::class)
    fun update(id: Int, spec: PlayerSpec): PlayerDTO

    /**
     * Deletes the Player
     *
     * @param id: Id of the player to delete
     * @return The deleted player
     * @throws NotFoundException If the Player to delete cannot be found
     */
    @Throws(NotFoundException::class)
    fun delete(id: Int): PlayerDTO

}