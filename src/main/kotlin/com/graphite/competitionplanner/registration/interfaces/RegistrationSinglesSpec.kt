package com.graphite.competitionplanner.registration.interfaces

import java.time.LocalDate

data class RegistrationSinglesSpec(
    val playerId: Int,
    val competitionCategoryId: Int
)

data class RegistrationSinglesSpecWithDate(
    val date: LocalDate,
    val playerId: Int,
    val competitionCategoryId: Int
)