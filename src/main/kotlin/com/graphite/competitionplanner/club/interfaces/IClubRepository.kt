package com.graphite.competitionplanner.club.interfaces

import com.graphite.competitionplanner.common.exception.NotFoundException

interface IClubRepository {

    /**
     * Stores the Club
     *
     * @param spec Specification of the new club
     * @return A new club with identical values as the given dto except the Id
     */
    fun store(spec: ClubSpec): ClubDTO

    /**
     * Find and returns the club with the given name.
     *
     * @param name Club name to search for
     * @return Club
     * @throws NotFoundException When Club with given name cannot be found
     */
    @Throws(NotFoundException::class)
    fun findByName(name: String): ClubDTO

    /**
     * Find the club with the given id
     *
     * @param id Club id
     * @return Club with the given id
     * @throws NotFoundException when Club with given id cannot be found
     */
    @Throws(NotFoundException::class)
    fun findById(id: Int): ClubDTO

    /**
     * Deletes the Club
     *
     * @param clubId Id of the club to be deleted
     * @return True if club was deleted, else false
     * @throws NotFoundException When the club to be deleted cannot be found
     */
    @Throws(NotFoundException::class)
    fun delete(clubId: Int): Boolean

    /**
     * Updates the Club
     *
     * @param clubId Id of the club to be updated
     * @param spec Specification that will be applied on the club
     * @return The updated club
     * @throws NotFoundException When the club to update cannot be found
     */
    @Throws(NotFoundException::class)
    fun update(clubId: Int, spec: ClubSpec): ClubDTO

    /**
     * Returns all stored Clubs
     */
    fun getAll(): List<ClubDTO>
}