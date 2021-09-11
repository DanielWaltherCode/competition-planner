package com.graphite.competitionplanner.competition.interfaces

data class LocationSpec(
    val name: String
) {
    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
    }
}