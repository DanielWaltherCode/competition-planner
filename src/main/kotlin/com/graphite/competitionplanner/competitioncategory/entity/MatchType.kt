package com.graphite.competitionplanner.competitioncategory.entity

sealed class MatchType {
    abstract val value: String

    companion object {
        operator fun invoke(input: String): MatchType {
            return when (input) {
                "POOL" -> {
                    Pool()
                }
                "PLAYOFF" -> {
                    PlayOff()
                }
                else -> {
                    throw IllegalArgumentException("Match type has to be either POOL or PLAYOFF")
                }
            }
        }
    }
}

class Pool : MatchType() {
    override val value = "POOL"
}

class PlayOff : MatchType() {
    override val value = "PLAYOFF"
}
