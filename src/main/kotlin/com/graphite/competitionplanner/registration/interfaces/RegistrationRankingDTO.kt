package com.graphite.competitionplanner.registration.interfaces

import com.graphite.competitionplanner.draw.domain.Registration

data class RegistrationRankingDTO(

    /**
     * The registration id
     */
    val registrationId: Registration.Real,

    /**
     * The competition category id
     */
    val competitionCategoryId: Int,

    /**
     * The total rank of this registration.
     *
     * - In case this is a singles' registration, then this is simply the player rank in singles
     * - In case this is doubles registration, then this is the sum of both players doubles rank
     */
    val rank: Int
)
