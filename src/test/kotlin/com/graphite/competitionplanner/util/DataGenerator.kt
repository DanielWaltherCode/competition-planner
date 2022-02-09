package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.competition.interfaces.*
import com.graphite.competitionplanner.schedule.domain.entity.Match
import com.graphite.competitionplanner.schedule.domain.entity.MatchType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.schedule.domain.entity.ScheduleSettings
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.SimpleMatchDTO
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.*
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.MatchDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleSettingsDTO
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.time.Duration

/**
 * A class that help with the creation of DTOs and Specs
 */
class DataGenerator {

    private var playerId = 0
    private var clubId = 0
    private var matchId = 0
    private var competitionCategoryId = 0
    private var registrationId = 1

    internal fun newMatch(
        id: Int = matchId++,
        competitionCategoryId: Int = 0,
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusMinutes(15),
        matchType: MatchType = MatchType("POOL"),
        firstPlayer: List<Int> = listOf(playerId++),
        secondPlayer: List<Int> = listOf(playerId++),
        orderNumber: Int = 0,
        groupOrRound: String = "GROUP A"
    ) = Match(
        id,
        competitionCategoryId,
        startTime,
        endTime,
        matchType,
        firstPlayer,
        secondPlayer,
        orderNumber,
        groupOrRound
    )

    internal fun newScheduleSettings(
        averageMatchTime: Duration = Duration.minutes(15),
        numberOfTables: Int = 8,
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusHours(8)
    ) = ScheduleSettings(
        averageMatchTime,
        numberOfTables,
        startTime,
        endTime
    )

    internal fun newLocationSpec(
        name: String = "Svedala Arena"
    ) = LocationSpec(
        name
    )

    fun newClubDTO(
        id: Int = clubId++,
        name: String = "Lule IK",
        address: String = "Sjögatan"
    ) = ClubDTO(
        id,
        name,
        address
    )

    fun newPlayerDTO(
        id: Int = playerId++,
        firstName: String = "Nisse",
        lastName: String = "Nilsson",
        clubId: Int = 1,
        dateOfBirth: LocalDate = LocalDate.of(1999, 1, 1)
    ) = PlayerDTO(
        id,
        firstName,
        lastName,
        clubId,
        dateOfBirth
    )

    fun newPlayerWithClubDTO(
        id: Int = playerId++,
        firstName: String = "Gunnar",
        lastName: String = "Åkerberg",
        clubDTO: ClubDTO = newClubDTO(),
        dateOfBirth: LocalDate = LocalDate.of(1999, 1, 1)
    ) = PlayerWithClubDTO(
        id,
        firstName,
        lastName,
        clubDTO,
        dateOfBirth
    )

    fun newMatchDTO(
        id: Int = matchId++,
        competitionCategoryId: Int = 0,
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusMinutes(15),
        matchType: String = "POOL",
        firstPlayer: List<Int> = listOf(newPlayerDTO(firstName = "Lars", lastName = "Åkesson").id),
        secondPlayer: List<Int> = listOf(newPlayerDTO(firstName = "Lars", lastName = "Åkesson").id),
        orderNumber: Int = 0,
        groupOrRound: String = "GROUP A"
    ) = MatchDTO(
        id,
        startTime,
        endTime,
        competitionCategoryId,
        matchType,
        firstPlayer,
        secondPlayer,
        orderNumber,
        groupOrRound
    )

    fun newSimpleMatchDTO(
        id: Int = matchId++,
        competitionCategoryId: Int = this.competitionCategoryId++,
        firstRegistrationId: Int = registrationId++,
        secondRegistrationId: Int = registrationId++,
        matchType: String = "A"
    ) = SimpleMatchDTO (
        id,
        competitionCategoryId,
        firstRegistrationId,
        secondRegistrationId,
        matchType
    )

    fun newMatchAndResultDTO(
        id: Int = matchId++,
        competitionCategoryId: Int = 0,
        competitionCategoryName: String = "Herrar 9",
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusMinutes(15),
        matchType: String = "POOL",
        firstPlayer: List<PlayerWithClubDTO> = listOf(newPlayerWithClubDTO(firstName = "Lars", lastName = "Åkesson")),
        secondPlayer: List<PlayerWithClubDTO> = listOf(newPlayerWithClubDTO(firstName = "Lars", lastName = "Åkesson")),
        orderNumber: Int = 0,
        groupOrRound: String = "GROUP A",
        wasWalkover: Boolean = false,
    ) = MatchAndResultDTO(
        id,
        startTime,
        endTime,
        com.graphite.competitionplanner.registration.service.SimpleCompetitionCategoryDTO(competitionCategoryId, competitionCategoryName),
        matchType,
        firstPlayer,
        secondPlayer,
        orderNumber,
        groupOrRound,
        firstPlayer,
        wasWalkover,
        ResultDTO(emptyList())
    )


        fun newScheduleSettingsDTO(
        averageMatchTime: Duration = Duration.minutes(15),
                numberOfTables: Int = 8,
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusHours(8)
    ) = ScheduleSettingsDTO(
        averageMatchTime,
        numberOfTables,
        startTime,
        endTime
    )

    fun newCompetitionDTO(
        id: Int = 1,
        location: LocationDTO = LocationDTO("Arena IK"),
        name: String = "Test Competition",
        welcomeText: String = "Välkommna till TestCompetition",
        organizingClubId: Int = newClubDTO().id,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(3)
    ) = CompetitionDTO(
        id,
        location,
        name,
        welcomeText,
        organizingClubId,
        startDate,
        endDate
    )

    fun newCategoryDTO(
        id: Int = 1,
        name: String = "A Custom Category",
        type: String = "SINGLES"
    ) = CategoryDTO(
        id,
        name,
        type
    )

    fun newCompetitionCategoryDTO(
        id: Int = 1,
        status: String = CompetitionCategoryStatus.ACTIVE.name,
        category: CategorySpec = newCategorySpec(),
        settings: GeneralSettingsDTO = newGeneralSettingsDTO(),
        gameSettings: GameSettingsDTO = newGameSettingsDTO()
    ) = CompetitionCategoryDTO(
        id,
        status,
        category,
        settings,
        gameSettings
    )

    fun newRegistrationSinglesDTO(
        id: Int = this.registrationId++,
        playerId: Int = this.playerId++,
        competitionCategoryId: Int = this.competitionCategoryId++,
        date: LocalDate = LocalDate.now(),
        status: PlayerRegistrationStatus = PlayerRegistrationStatus.PLAYING
    ) = RegistrationSinglesDTO(
        id,
        playerId,
        competitionCategoryId,
        date,
        status
    )

    fun newRegistrationRankDTO(
        id: Int = this.registrationId++,
        competitionCategoryId: Int = this.competitionCategoryId++,
        rank: Int = 99
    ) = RegistrationRankingDTO(
        id,
        competitionCategoryId,
        rank
    )

    fun newCompetitionCategoryUpdateSpec(
        settings: GeneralSettingsDTO = newGeneralSettingsDTO(),
        gameSettings: GameSettingsDTO = newGameSettingsDTO()
    ) = CompetitionCategoryUpdateSpec(
        settings,
        gameSettings
    )

    fun newCompetitionCategorySpec(
        status: CompetitionCategoryStatus = CompetitionCategoryStatus.ACTIVE,
        category: CategorySpec = newCategorySpec(),
        settings: GeneralSettingsDTO = newGeneralSettingsDTO(),
        gameSettings: GameSettingsDTO = newGameSettingsDTO()
    ) = CompetitionCategorySpec(
        status,
        category,
        settings,
        gameSettings
    )

    fun newGeneralSettingsDTO(
        cost: Float = 150f,
        drawType: DrawType = DrawType.POOL_ONLY,
        playersPerGroup: Int = 4,
        playersToPlayOff: Int = 2,
        poolDrawStrategy: PoolDrawStrategy = PoolDrawStrategy.NORMAL
    ) = GeneralSettingsDTO(
        cost,
        drawType,
        playersPerGroup,
        playersToPlayOff,
        poolDrawStrategy
    )

    fun newGameSettingsDTO(
        numberOfSets: Int = 2,
        winScore: Int = 11,
        winMargin: Int = 2,
        differentNumberOfGamesFromRound: Round = Round.UNKNOWN,
        numberOfSetsFinal: Int = 5,
        winScoreFinal: Int = 11,
        winMarginFinal: Int = 2,
        tiebreakInFinalGame: Boolean = false,
        winScoreTiebreak: Int = 3,
        winMarginTieBreak: Int = 3,
        useDifferentRulesInEndGame: Boolean = false
    ) = GameSettingsDTO(
        numberOfSets,
        winScore,
        winMargin,
        differentNumberOfGamesFromRound,
        numberOfSetsFinal,
        winScoreFinal,
        winMarginFinal,
        tiebreakInFinalGame,
        winScoreTiebreak,
        winMarginTieBreak,
        useDifferentRulesInEndGame
    )

    fun newCategorySpec(
        id: Int = 1,
        name: String = "Herrar 1",
        type: String = "SINGLES"
    ) = CategorySpec(
        id,
        name,
        type
    )


    fun newClubSpec(
        name: String = "Club" + Random.nextLong().toString(),
        address: String = "Address" + Random.nextLong().toString()
    ) = ClubSpec(
        name,
        address
    )

    fun newCompetitionSpec(
        name: String = "TestCompetition",
        location: LocationSpec = LocationSpec("Arena IK"),
        welcomeText: String = "Välkommna till TestCompetition",
        organizingClubId: Int = newClubDTO().id,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(3)
    ) = CompetitionSpec(
        location,
        name,
        welcomeText,
        organizingClubId,
        startDate,
        endDate
    )

    fun newCompetitionUpdateSpec(
        name: String = "TestCompetition",
        location: LocationSpec = LocationSpec("Arena IK"),
        welcomeText: String = "Välkommna till TestCompetition",
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(3)
    ) = CompetitionUpdateSpec(
        location,
        name,
        welcomeText,
        startDate,
        endDate
    )

    fun newPlayerSpec(
        firstName: String = "Lasse",
        lastName: String = "Larsson",
        clubId: Int = this.clubId++,
        dateOfBirth: LocalDate = LocalDate.now().minusYears(20)
    ) = PlayerSpec(
        firstName,
        lastName,
        clubId,
        dateOfBirth
    )

    fun newRegistrationSinglesSpec(
        playerId: Int = this.playerId++,
        competitionCategoryId: Int = this.competitionCategoryId++
    ) = RegistrationSinglesSpec(
        playerId,
        competitionCategoryId
    )

    fun newRegistrationSinglesSpecWithDate(
        date: LocalDate = LocalDate.now(),
        playerId: Int = this.playerId++,
        competitionCategoryId: Int = this.competitionCategoryId++
    ) = RegistrationSinglesSpecWithDate(
        date,
        playerId,
        competitionCategoryId
    )

    fun newRegistrationDoublesSpec(
        playerOneId: Int = this.playerId++,
        playerTwoId: Int = this.playerId++,
        competitionCategoryId: Int = this.competitionCategoryId++
    ) = RegistrationDoublesSpec(
        playerOneId,
        playerTwoId,
        competitionCategoryId
    )

    fun newRegistrationDoublesSpecWithDate(
        date: LocalDate = LocalDate.now(),
        playerOneId: Int = this.playerId++,
        playerTwoId: Int = this.playerId++,
        competitionCategoryId: Int = this.competitionCategoryId++
    ) = RegistrationDoublesSpecWithDate(
        date,
        playerOneId,
        playerTwoId,
        competitionCategoryId
    )

    fun newGameSpec(
        gameNumber: Int = 1,
        firstRegistrationResult: Int = 11,
        secondRegistrationResult: Int = 9
    ) = GameSpec(
        gameNumber,
        firstRegistrationResult,
        secondRegistrationResult
    )

    fun newResultSpec(
        games: List<GameSpec> = listOf()
    ) = ResultSpec(
        games
    )

    fun newRegistrationDTO(
        id: Int = this.registrationId++,
        date: LocalDate = LocalDate.now()
    ) = RegistrationDTO(
        id,
        date
    )

    /**
     * Generates a set of matches for a pool with the given amount of players. The generated matches
     * are so that every player will go up against each other player exactly once.
     *
     * @param numberOfPlayers Number of players in the pool
     * @param categoryId Category the pool / players belong to
     */
    fun poolOf(numberOfPlayers: Int = 4, categoryId: Int = 2): List<MatchDTO> {
        assert(numberOfPlayers > 1) { "Yo! Think I can create a pool with less than 2 players?!" }
        assert(numberOfPlayers < 11) { "Not that many! Maximum of 10 players per pool." }

        val postFixes = ('A'..'J').toList()
        val players =
            (0..numberOfPlayers).map { newPlayerDTO(firstName = "Player" + postFixes[it], lastName = "LastName") }

        val matches = mutableListOf<MatchDTO>()
        for (i in 0..numberOfPlayers) {
            for (j in i + 1..numberOfPlayers) {
                matches.add(
                    MatchDTO(
                        matchId++,
                        null,
                        null,
                        categoryId,
                        "POOL",
                        listOf(players[i].id),
                        listOf(players[j].id),
                        0,
                        "GROUP"
                    )
                )
            }
        }
        return matches
    }


    /**
     * Pool with 4 players i.e. 6 matches
     */
    fun pool1(categoryId: Int = 1): List<MatchDTO> {
        return pool1.map {
            MatchDTO(
                it.id,
                it.startTime,
                it.endTime,
                categoryId,
                it.matchType,
                it.firstPlayer,
                it.secondPlayer,
                it.matchOrderNumber,
                it.groupOrRound
            )
        }
    }

    /**
     * Pool with 4 players i.e. 6 matches
     */
    fun pool2(categoryId: Int = 1): List<MatchDTO> {
        return pool2.map {
            MatchDTO(
                it.id,
                it.startTime,
                it.endTime,
                categoryId,
                it.matchType,
                it.firstPlayer,
                it.secondPlayer,
                it.matchOrderNumber,
                it.groupOrRound
            )
        }
    }

    /**
     * Pool with 3 players i.e 3 matches
     */
    fun pool3(categoryId: Int = 1): List<MatchDTO> {
        return pool3.map {
            MatchDTO(
                it.id,
                it.startTime,
                it.endTime,
                categoryId,
                it.matchType,
                it.firstPlayer,
                it.secondPlayer,
                it.matchOrderNumber,
                it.groupOrRound
            )
        }
    }

    private val birthDate = LocalDate.of(1999, 4, 3)
    private val club = ClubDTO(33, "Luleå", "Midsommarvägen 13")

    private val p1 = PlayerDTO(1, "Jan", "Olsson", club.id, birthDate)
    private val p2 = PlayerDTO(2, "Gill", "Fiskarsson", club.id, birthDate)
    private val p3 = PlayerDTO(3, "Sven", "Svensson", club.id, birthDate)
    private val p4 = PlayerDTO(4, "Sture", "Sundberg", club.id, birthDate)

    private val pool1 = listOf(
        MatchDTO(
            1, null, null, 1, "POOL",
            listOf(p1.id), listOf(p2.id), 0, "GROUP"
        ),
        MatchDTO(
            2, null, null, 1, "POOL",
            listOf(p1.id), listOf(p3.id), 0, "GROUP"
        ),
        MatchDTO(
            3, null, null, 1, "POOL",
            listOf(p1.id), listOf(p4.id), 0, "GROUP"
        ),
        MatchDTO(
            4, null, null, 1, "POOL",
            listOf(p2.id), listOf(p3.id), 0, "GROUP"
        ),
        MatchDTO(
            5, null, null, 1, "POOL",
            listOf(p2.id), listOf(p4.id), 0, "GROUP"
        ),
        MatchDTO(
            6, null, null, 1, "POOL",
            listOf(p3.id), listOf(p4.id), 0, "GROUP"
        )
    )

    private val p5 = PlayerDTO(5, "Elin", "Malsson", club.id, birthDate)
    private val p6 = PlayerDTO(6, "Ewa", "Svensson", club.id, birthDate)
    private val p7 = PlayerDTO(7, "Katarina", "Dalhborg", club.id, birthDate)
    private val p8 = PlayerDTO(8, "Lena", "Sinè", club.id, birthDate)

    private val pool2 = listOf(
        MatchDTO(
            7, null, null, 1, "POOL",
            listOf(p5.id), listOf(p6.id), 0, "GROUP"
        ),
        MatchDTO(
            8, null, null, 1, "POOL",
            listOf(p5.id), listOf(p7.id), 0, "GROUP"
        ),
        MatchDTO(
            9, null, null, 1, "POOL",
            listOf(p5.id), listOf(p8.id), 0, "GROUP"
        ),
        MatchDTO(
            10, null, null, 1, "POOL",
            listOf(p6.id), listOf(p7.id), 0, "GROUP"
        ),
        MatchDTO(
            11, null, null, 1, "POOL",
            listOf(p6.id), listOf(p8.id), 0, "GROUP"
        ),
        MatchDTO(
            12, null, null, 1, "POOL",
            listOf(p7.id), listOf(p8.id), 0, "GROUP"
        )
    )

    private val p9 = PlayerDTO(9, "Patrik", "Larsson", club.id, birthDate)
    private val p10 = PlayerDTO(10, "Enok", "Karlsson", club.id, birthDate)
    private val p11 = PlayerDTO(11, "Tintin", "Snäll", club.id, birthDate)

    private val pool3 = listOf(
        MatchDTO(
            13, null, null, 1, "POOL",
            listOf(p9.id), listOf(p10.id), 0, "GROUP"
        ),
        MatchDTO(
            14, null, null, 1, "POOL",
            listOf(p9.id), listOf(p11.id), 0, "GROUP"
        ),
        MatchDTO(
            15, null, null, 1, "POOL",
            listOf(p10.id), listOf(p11.id), 0, "GROUP"
        )
    )

}
