package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.PlayerEntityDTO
import java.time.LocalDate

data class Player(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val club: Club,
    val dateOfBirth: LocalDate
) {
    init {
        require(firstName.isNotEmpty()) { "Player's first name cannot be empty" }
        require(firstName.map { it.isLetter() }.all { it }) { "Player's first name <$firstName>  contains non-letters" }
        require(lastName.isNotEmpty()) { "Player's last name cannot be empty" }
        require(lastName.map { it.isLetter() }.all { it }) { "Player's last name <$lastName> contains non-letters" }
        require(dateOfBirth.isBefore(LocalDate.now())) { "Player's date of birth <$dateOfBirth> cannot be in the future" }
    }

    constructor(dto: PlayerEntityDTO) : this(dto.id, dto.firstName, dto.lastName, Club(dto.club), dto.dateOfBirth)
}
