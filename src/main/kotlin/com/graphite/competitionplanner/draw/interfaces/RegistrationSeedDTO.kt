package com.graphite.competitionplanner.draw.interfaces

data class RegistrationSeedDTO(
    /**
     * Id of the registration
     */
    val id: Int,
    /**
     * Id of the competition category
     */
    val competitionCategoryId: Int,
    /**
     * The seed of this registration. It represents a relative rank compared to other registrations in the same
     * competition category.
     *
     * Given two registrations in the same competition category, the registration with lowest seed is considered the
     * better registration.
     *
     * A value of Null can be considered as a value higher than any other Int i.e. one say that registration was not
     * seeded and is among the relative worse ranked registrations in the competition category.
     */
    var seed: Int?
)