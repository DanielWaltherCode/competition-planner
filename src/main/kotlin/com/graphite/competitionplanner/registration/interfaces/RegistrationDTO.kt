package com.graphite.competitionplanner.registration.interfaces

import java.time.LocalDate

data class RegistrationDTO(
    /**
     * Registration id
     */
    val id: Int,

    /**
     * The date when the registration took place
     */
    val registrationDate: LocalDate
)
