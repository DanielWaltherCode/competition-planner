package com.graphite.competitionplanner.registration.interfaces

interface IRegistrationRepository {

    /**
     * Store a registration
     *
     * @param spec Information about the registration about to be saved
     * @return The newly stored registration
     */
    fun store(spec: RegistrationSinglesSpecWithDate): RegistrationSinglesDTO
}