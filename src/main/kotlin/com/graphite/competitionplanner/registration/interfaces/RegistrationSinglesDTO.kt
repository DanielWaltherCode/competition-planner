package com.graphite.competitionplanner.registration.interfaces

import java.time.LocalDate

data class RegistrationSinglesDTO(

    /**
     * Registration id
     */
    val id: Int,

    /**
     * The player linked to the registration
     */
    val playerId: Int,

    /**
     * The competition category that the player is registered to
     */
    val competitionCategoryId: Int,

    /**
     * The date when the registration took place
     */
    val registrationDate: LocalDate,

    /**
     * Status of this registration
     */
    val status: PlayerRegistrationStatus
)