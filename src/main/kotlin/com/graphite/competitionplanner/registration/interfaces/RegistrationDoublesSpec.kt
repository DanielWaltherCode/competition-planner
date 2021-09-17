package com.graphite.competitionplanner.registration.interfaces

import java.time.LocalDate

data class RegistrationDoublesSpec(
    val playerOneId: Int,
    val playerTwoId: Int,
    val competitionCategoryId: Int
) {
    init {
        require(playerOneId != playerTwoId) { "Player one and two cannot be the same" }
    }
}

data class RegistrationDoublesSpecWithDate(
    val date: LocalDate,
    val playerOneId: Int,
    val playerTwoId: Int,
    val competitionCategoryId: Int
) {
    init {
        require(playerOneId != playerTwoId) { "Player one and two cannot be the same" }
    }
}