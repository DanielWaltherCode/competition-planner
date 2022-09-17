package com.graphite.competitionplanner.club.interfaces

data class ClubDTO(
    /**
     * ID of club
     */
    val id: Int,

    /**
     * Name of club
     */
    val name: String,

    /**
     * Address of club
     */
    val address: String
)