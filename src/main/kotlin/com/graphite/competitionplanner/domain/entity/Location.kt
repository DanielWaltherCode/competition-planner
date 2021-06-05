package com.graphite.competitionplanner.domain.entity

class Location(
    val name: String
) {
    init {
        require(name.isNotEmpty())
        require(name.isNotBlank())
    }
}
