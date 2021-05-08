package com.graphite.competitionplanner.domain.entity

data class MatchType(
    val value: String
) {
    init {
        require(value == "POOL" || value == "PLAYOFF") { "Match type has to be either POOL or PLAYOFF" }
    }
}
