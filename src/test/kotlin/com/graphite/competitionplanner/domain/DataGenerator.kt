package com.graphite.competitionplanner.domain

import com.graphite.competitionplanner.domain.entity.*
import java.time.LocalDate

class DataGenerator {

    var playerId = 0
    var clubId = 0
    var matchId = 0

    internal fun newClub(): Club {
        return Club(clubId, "Lule IK", "Sjögatan")
    }

    internal fun newPlayer(): Player {
        return newPlayer("Ida", "Larsson")
    }

    internal fun newPlayer(firstName: String, lastName: String): Player {
        return Player(playerId++, firstName, lastName, newClub(), LocalDate.of(1999, 1, 1))
    }

    internal fun newMatch(): Match {
        return Match(
            matchId++,
            CompetitionCategory(0),
            MatchType("POOL"),
            listOf(newPlayer("Lars", "Åkesson")),
            listOf(newPlayer("Nils", "Holm")),
            0,
            "Group A"
        )
    }

}