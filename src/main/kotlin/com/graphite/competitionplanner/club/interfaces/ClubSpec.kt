package com.graphite.competitionplanner.club.interfaces

data class ClubSpec(
    val name: String,
    val address: String
) {
    init {
        require(name.isNotEmpty()) { "Club name cannot be empty." }
        require(address.isNotEmpty()) { "Club address cannot be empty." }
    }
}
