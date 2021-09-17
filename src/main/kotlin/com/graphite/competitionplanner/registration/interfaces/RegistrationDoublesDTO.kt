package com.graphite.competitionplanner.registration.interfaces

import java.time.LocalDate

data class RegistrationDoublesDTO(

    /**
     * Registration id
     */
    val id: Int,

    /**
     * The first player linked to the registration
     */
    val playerOneId: Int,

    /**
     * The second player linked to the registration
     */
    val playerTwoId: Int,

    /**
     * The competition category that the player is registered to
     */
    val competitionCategoryId: Int,

    /**
     * The date when the registration took place
     */
    val registrationDate: LocalDate
)

