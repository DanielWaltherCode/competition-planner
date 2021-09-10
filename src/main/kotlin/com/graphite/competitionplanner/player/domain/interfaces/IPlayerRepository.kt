package com.graphite.competitionplanner.player.domain.interfaces

import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException

interface IPlayerRepository {

    /**
     * Saves the Player
     *
     * @param dto Player to be stored
     */
    fun store(dto: NewPlayerDTO): PlayerDTO

    /**
     * Return all players for the given club
     */
    fun playersInClub(dto: ClubDTO): List<PlayerWithClubDTO>

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
     * Return a list of players where the first name or last name
     * starts with the given search string
     *
     * @param partName: Search string
     * @Return List of Players
     */
    fun findByName(startOfName: String): List<PlayerWithClubDTO>

    /**
     * Updates the Player with the given id
     *
     * @param dto: Player to update
     * @return The updated player
     * @throws NotFoundException If the Player to updated cannot be found
     */
    @Throws(NotFoundException::class)
    fun update(dto: PlayerDTO): PlayerDTO

    /**
     * Deletes the Player
     *
     * @param dto: Player to delete
     * @return The deleted player
     * @throws NotFoundException If the Player to delete cannot be found
     */
    @Throws(NotFoundException::class)
    fun delete(dto: PlayerDTO): PlayerDTO
}