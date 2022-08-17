package com.graphite.competitionplanner.registration.interfaces

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.draw.domain.Registration
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.tables.records.PlayerRegistrationRecord


interface IRegistrationRepository {

    /**
     * Store a single registration
     *
     * @param spec Information about the registration about to be saved
     * @return The newly stored registration
     */
    fun storeSingles(spec: RegistrationSinglesSpecWithDate): RegistrationSinglesDTO

    /**
     * Store a double registration
     *
     */
    fun storeDoubles(spec: RegistrationDoublesSpecWithDate): RegistrationDoublesDTO

    /**
     * Get all the player ids that are currently registered to the given competition category
     *
     * @return A list of player ids
     */
    fun getAllPlayerIdsRegisteredTo(competitionCategoryId: Int): List<Int>

    /**
     * Get all players that are currently registered in the given competition category paired together with
     * its registration id for that category.
     */
    fun getAllRegisteredPlayersInCompetitionCategory(competitionCategoryId: Int): List<Pair<Registration, PlayerWithClubDTO>>

    /**
     * Get all players that are currently registered in the given competition
     */
    fun getAllRegisteredPlayersInCompetition(competitionId: Int): List<PlayerWithClubDTO>

    /**
     * Return all single registrations
     */
    fun getAllSingleRegistrations(competitionCategoryId: Int): List<RegistrationSinglesDTO>

    /**
     * Get all player and their respective competition category that are currently registered in a given competition
     */
    fun getCategoriesAndPlayersInCompetition(competitionId: Int): List<Pair<CategoryDTO, PlayerWithClubDTO>>

    /**
     * Return the registration for the given player and competition category
     */
    fun getRegistrationFor(spec: RegistrationSinglesSpec): RegistrationSinglesDTO

    /**
     * Return the registration for the given player and competition category
     */
    fun getRegistrationFor(spec: RegistrationDoublesSpec): RegistrationDoublesDTO

    /**
     * Return the registrations for the given competition category.
     *
     * @param competitionCategoryId ID of the competition category
     * @return A list of registrations
     */
    fun getRegistrationsIn(competitionCategoryId: Int): List<RegistrationDTO>

    /**
     * Return the players that are associated with the given registration id.
     *
     * If the registration id does not exist, then this function
     * will return an empty list.
     *
     * @return A list of player objects.
     */
    fun getPlayersFrom(registrationId: Int): List<PlayerDTO>

    /**
     * Remove the registration with the given id
     *
     * @throws NotFoundException If a registration with the given id does not exist
     */
    @Throws(NotFoundException::class)
    fun remove(registrationId: Int)

    /**
     * Remove player registration -- i.e. a specific player id and registration id combination. This is helpful
     * for doubles where two people share a registration id
     */
    fun unregisterIndividualPlayer(registrationId: Int, playerId: Int)

    /**
     * Return the rankings of each registration for the given competition category
     */
    fun getRegistrationRanking(competitionCategory: CompetitionCategoryDTO): List<RegistrationRankingDTO>

    fun updatePlayerRegistrationStatus(registrationId: Int, status: PlayerRegistrationStatus)

    fun getPlayerRegistration(registrationId: Int): PlayerRegistrationRecord

    fun getRegistrationIdForPlayerInCategory(categoryId: Int, playerId: Int): Int
}