package com.graphite.competitionplanner.registration.interfaces

import com.graphite.competitionplanner.common.exception.NotFoundException


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
     * Return the registration for the given player and competition category
     */
    fun getRegistrationFor(spec: RegistrationSinglesSpec): RegistrationSinglesDTO

    /**
     * Return the registration for the given player and competition category
     */
    fun getRegistrationFor(spec: RegistrationDoublesSpec): RegistrationDoublesDTO

    /**
     * Remove the registration with the given Id
     *
     * @throws NotFoundException If a registration with the given id does not exist
     */
    @Throws(NotFoundException::class)
    fun remove(registrationId: Int)
}