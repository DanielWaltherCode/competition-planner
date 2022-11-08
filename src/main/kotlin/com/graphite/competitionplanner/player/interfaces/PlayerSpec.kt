package com.graphite.competitionplanner.player.interfaces

import java.time.LocalDate

data class PlayerSpec(
    val firstName: String,
    val lastName: String,
    val clubId: Int,
    val dateOfBirth: LocalDate
) {
    init {
        require(firstName.isNotEmpty()) { "Player's first name cannot be empty" }
        require(lastName.isNotEmpty()) { "Player's last name cannot be empty" }
        require(dateOfBirth.isBefore(LocalDate.now())) { "Player's date of birth <$dateOfBirth> cannot be in the future" }
    }
}

data class PlayerRankingSpec(
    val singles: Int,
    val doubles: Int
)