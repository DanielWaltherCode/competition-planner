package com.graphite.competitionplanner

import com.graphite.competitionplanner.domain.dto.*
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

    fun newClubDTO(): ClubDTO {
        return ClubDTO(newClub())
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

    fun newPlayerEntityDTO(): PlayerEntityDTO {
        return PlayerEntityDTO(newPlayer())
    }

    fun newPlayerDTO(): PlayerDTO {
        val player = newPlayer()
        return PlayerDTO(player.id, player.firstName, player.lastName, player.club.id, player.dateOfBirth)
    }

    fun newNewPlayerDTO(): NewPlayerDTO {
        val player = newPlayer()
        return NewPlayerDTO(player.firstName, player.lastName, player.club.id, player.dateOfBirth)
    }

    fun newScheduleSettings(numberOfTables: Int): ScheduleSettingsDTO {
        return ScheduleSettingsDTO(15, numberOfTables, LocalDateTime.now())
    }

    fun newInvalidPlayerDTO(id: Int = 0): PlayerDTO {
        return PlayerDTO(id, "Lisa12", "Svansson", newClub().id, LocalDate.of(2001, 10, 1))
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

    private val p1 = PlayerEntityDTO(1, "Jan", "Olsson", club, birthDate)
    private val p2 = PlayerEntityDTO(2, "Gill", "Fiskarsson", club, birthDate)
    private val p3 = PlayerEntityDTO(3, "Sven", "Svensson", club, birthDate)
    private val p4 = PlayerEntityDTO(4, "Sture", "Sundberg", club, birthDate)

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

    private val p5 = PlayerEntityDTO(5, "Elin", "Malsson", club, birthDate)
    private val p6 = PlayerEntityDTO(6, "Ewa", "Svensson", club, birthDate)
    private val p7 = PlayerEntityDTO(7, "Katarina", "Dalhborg", club, birthDate)
    private val p8 = PlayerEntityDTO(8, "Lena", "Sinè", club, birthDate)

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

    private val p9 = PlayerEntityDTO(9, "Patrik", "Larsson", club, birthDate)
    private val p10 = PlayerEntityDTO(10, "Enok", "Karlsson", club, birthDate)
    private val p11 = PlayerEntityDTO(11, "Tintin", "Snäll", club, birthDate)

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