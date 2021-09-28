package com.graphite.competitionplanner.draw.interfaces

data class RegistrationSeedDTO(
    val id: Int,
    val competitionCategoryId: Int,
    var seed: Int?
)