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
        require(firstName.map { it.isLetter() }.all { it }) { "Player's first name <$firstName>  contains non-letters" }
        require(lastName.isNotEmpty()) { "Player's last name cannot be empty" }
        require(lastName.map { it.isLetter() }.all { it }) { "Player's last name <$lastName> contains non-letters" }
        require(dateOfBirth.isBefore(LocalDate.now())) { "Player's date of birth <$dateOfBirth> cannot be in the future" }
    }
}