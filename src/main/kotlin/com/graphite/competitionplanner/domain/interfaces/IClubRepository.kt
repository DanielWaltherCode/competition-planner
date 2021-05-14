package com.graphite.competitionplanner.domain.interfaces

import com.graphite.competitionplanner.domain.dto.ClubDTO

interface IClubRepository {

    /**
     * Stores the Club
     *
     * @param dto Club to be stored
     * @return A new club with identical values as the given dto except the Id
     */
    fun store(dto: ClubDTO): ClubDTO

    /**
     * Check whether a club already exist with the given name
     *
     * @param name Club name to search for
     * @return True if club was found, false if not.
     */
    fun doesClubExist(name: String): Boolean

    /**
     * Find and returns the club with the given name.
     *
     * @param name Club name to search for
     * @return Club
     * @throws NotFoundException When Club with given name cannot be found
     */
    @Throws(NotFoundException::class)
    // TODO: Rename to findByName when legacy function has been removed
    fun findClubByName(name: String): ClubDTO

    /**
     * Find the club with the given id
     *
     * @param id Club id
     * @return Club with the given id
     * @throws NotFoundException when Club with given id cannot be found
     */
    @Throws(NotFoundException::class)
    fun findClubById(id: Int): ClubDTO

    /**
     * Deletes the Club
     *
     * @param dto Club to delete
     * @return Club that was deleted
     * @throws NotFoundException When the club to be deleted cannot be found
     */
    @Throws(NotFoundException::class)
    fun delete(dto: ClubDTO): ClubDTO

    /**
     * Updates the Club
     *
     * @param dto Club to update
     * @return The updated club
     * @throws NotFoundException When the club to update cannot be found
     */
    @Throws(NotFoundException::class)
    fun update(dto: ClubDTO): ClubDTO

    /**
     * Returns all stored Clubs
     */
    fun getAll(): List<ClubDTO>
}