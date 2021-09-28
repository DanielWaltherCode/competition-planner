package com.graphite.competitionplanner.draw.interfaces

interface ISeedRepository {

    /**
     * Set seed value on the given registrations
     */
    fun setSeeds(registrationSeeds: List<RegistrationSeedDTO>)
}