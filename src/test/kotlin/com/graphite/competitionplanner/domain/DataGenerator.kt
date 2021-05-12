package com.graphite.competitionplanner.domain

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.MatchDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import com.graphite.competitionplanner.domain.entity.*
import java.time.LocalDate
import java.time.LocalDateTime

class DataGenerator {

    private var playerId = 0
    private var clubId = 0
    private var matchId = 0

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
            null,
            null,
            MatchType("POOL"),
            listOf(newPlayer("Lars", "Åkesson")),
            listOf(newPlayer("Nils", "Holm")),
            0,
            "Group A"
        )
    }

    fun newPlayerDTO(): PlayerDTO {
        return PlayerDTO(newPlayer())
    }

    fun newScheduleSettings(numberOfTables: Int): ScheduleSettingsDTO {
        return ScheduleSettingsDTO(15, numberOfTables, LocalDateTime.now())
    }

    /**
     * Pool with 4 players i.e. 6 matches
     */
    fun pool1(): List<MatchDTO> {
        return pool1
    }

    /**
     * Pool with 4 players i.e. 6 matches
     */
    fun pool2(): List<MatchDTO> {
        return pool2
    }

    /**
     * Pool with 3 players i.e 3 matches
     */
    fun pool3(): List<MatchDTO> {
        return pool3
    }

    private val birthDate = LocalDate.of(1999, 4, 3)
    private val club = ClubDTO(33, "Luleå", "Midsommarvägen 13")

    private val p1 = PlayerDTO(1, "Jan", "Olsson", club, birthDate)
    private val p2 = PlayerDTO(2, "Gill", "Fiskarsson", club, birthDate)
    private val p3 = PlayerDTO(3, "Sven", "Svensson", club, birthDate)
    private val p4 = PlayerDTO(4, "Sture", "Sundberg", club, birthDate)

    private val pool1 = listOf(
        MatchDTO(
            1, null, null, 1, "POOL",
            listOf(p1), listOf(p2), 0, "GROUP"
        ),
        MatchDTO(
            2, null, null, 1, "POOL",
            listOf(p1), listOf(p3), 0, "GROUP"
        ),
        MatchDTO(
            3, null, null, 1, "POOL",
            listOf(p1), listOf(p4), 0, "GROUP"
        ),
        MatchDTO(
            4, null, null, 1, "POOL",
            listOf(p2), listOf(p3), 0, "GROUP"
        ),
        MatchDTO(
            5, null, null, 1, "POOL",
            listOf(p2), listOf(p4), 0, "GROUP"
        ),
        MatchDTO(
            6, null, null, 1, "POOL",
            listOf(p3), listOf(p4), 0, "GROUP"
        )
    )

    private val p5 = PlayerDTO(5, "Elin", "Malsson", club, birthDate)
    private val p6 = PlayerDTO(6, "Ewa", "Svensson", club, birthDate)
    private val p7 = PlayerDTO(7, "Katarina", "Dalhborg", club, birthDate)
    private val p8 = PlayerDTO(8, "Lena", "Sinè", club, birthDate)

    private val pool2 = listOf(
        MatchDTO(
            7, null, null, 1, "POOL",
            listOf(p5), listOf(p6), 0, "GROUP"
        ),
        MatchDTO(
            8, null, null, 1, "POOL",
            listOf(p5), listOf(p7), 0, "GROUP"
        ),
        MatchDTO(
            9, null, null, 1, "POOL",
            listOf(p5), listOf(p8), 0, "GROUP"
        ),
        MatchDTO(
            10, null, null, 1, "POOL",
            listOf(p6), listOf(p7), 0, "GROUP"
        ),
        MatchDTO(
            11, null, null, 1, "POOL",
            listOf(p6), listOf(p8), 0, "GROUP"
        ),
        MatchDTO(
            12, null, null, 1, "POOL",
            listOf(p7), listOf(p8), 0, "GROUP"
        )
    )

    private val p9 = PlayerDTO(9, "Patrik", "Larsson", club, birthDate)
    private val p10 = PlayerDTO(10, "Enok", "Karlsson", club, birthDate)
    private val p11 = PlayerDTO(11, "Tintin", "Snäll", club, birthDate)

    private val pool3 = listOf(
        MatchDTO(
            13, null, null, 1, "POOL",
            listOf(p9), listOf(p10), 0, "GROUP"
        ),
        MatchDTO(
            14, null, null, 1, "POOL",
            listOf(p9), listOf(p11), 0, "GROUP"
        ),
        MatchDTO(
            15, null, null, 1, "POOL",
            listOf(p10), listOf(p11), 0, "GROUP"
        )
    )

}