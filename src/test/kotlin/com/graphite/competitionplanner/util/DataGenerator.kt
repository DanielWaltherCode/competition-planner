package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.competition.interfaces.*
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.*
import com.graphite.competitionplanner.draw.service.MatchSpec
import com.graphite.competitionplanner.match.domain.GameResult
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.domain.PlayoffMatch
import com.graphite.competitionplanner.match.domain.PoolMatch
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.*
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.schedule.interfaces.MatchToTimeTableSlot
import com.graphite.competitionplanner.schedule.interfaces.ScheduleMatchDto
import com.graphite.competitionplanner.schedule.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotMatchInfo
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotToMatch
import com.graphite.competitionplanner.schedule.interfaces.MapMatchToTimeTableSlotSpec
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

//    internal fun newMatch(
//        id: Int = matchId++,
//        competitionCategoryId: Int = 0,
//        startTime: LocalDateTime = LocalDateTime.now(),
//        endTime: LocalDateTime = LocalDateTime.now().plusMinutes(15),
//        matchType: MatchType = MatchType("POOL"),
//        firstPlayer: List<Int> = listOf(playerId++),
//        secondPlayer: List<Int> = listOf(playerId++),
//        orderNumber: Int = 0,
//        groupOrRound: String = "GROUP A"
//    ) = Match(
//        id,
//        competitionCategoryId,
//        startTime,
//        endTime,
//        matchType,
//        firstPlayer,
//        secondPlayer,
//        orderNumber,
//        groupOrRound
//    )
//
//    internal fun newScheduleSettings(
//        averageMatchTime: Duration = Duration.minutes(15),
//        numberOfTables: Int = 8,
//        startTime: LocalDateTime = LocalDateTime.now(),
//        endTime: LocalDateTime = LocalDateTime.now().plusHours(8)
//    ) = ScheduleSettings(
//        averageMatchTime,
//        numberOfTables,
//        startTime,
//        endTime
//    )

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

    fun newMatchSpec(
        startTime: LocalDateTime? = null,
        endTime: LocalDateTime? = null,
        competitionCategoryId: Int = this.competitionCategoryId++,
        matchType: com.graphite.competitionplanner.draw.service.MatchType = com.graphite.competitionplanner.draw.service.MatchType.GROUP,
        firstRegistrationId: Int = this.registrationId++,
        secondRegistrationId: Int = this.registrationId++,
        matchOrderNumber: Int = 1,
        groupOrRound: String = "A"
    ) = MatchSpec(
        startTime,
        endTime,
        competitionCategoryId,
        matchType,
        firstRegistrationId,
        secondRegistrationId,
        matchOrderNumber,
        groupOrRound
    )

    fun newPoolMatch(
        name: String = "A",
        id: Int = matchId++,
        competitionCategoryId: Int = this.competitionCategoryId++,
        firstRegistrationId: Int = registrationId++,
        secondRegistrationId: Int = registrationId++,
        wasWalkover: Boolean = false,
        winner: Int? = null
    ) = PoolMatch (
        name,
        id,
        competitionCategoryId,
        firstRegistrationId,
        secondRegistrationId,
        wasWalkover,
        winner,
    )

    fun newPlayOffMatch(
        round: Round = Round.ROUND_OF_16,
        orderNumber: Int = 1,
        id: Int = matchId++,
        competitionCategoryId: Int = this.competitionCategoryId++,
        firstRegistrationId: Int = registrationId++,
        secondRegistrationId: Int = registrationId++,
        wasWalkover: Boolean = false,
        winner: Int? = null
    ) = PlayoffMatch(
        round,
        orderNumber,
        id,
        competitionCategoryId,
        firstRegistrationId,
        secondRegistrationId,
        wasWalkover,
        winner
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
        competitionLevel: CompetitionLevel = CompetitionLevel.A,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(3)
    ) = CompetitionDTO(
        id,
        location,
        name,
        welcomeText,
        competitionLevel,
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
        numberOfSets: Int = 5,
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
        type: CategoryType = CategoryType.SINGLES
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
        competitionLevel: String = "A",
        organizingClubId: Int = newClubDTO().id,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(3)
    ) = CompetitionSpec(
        location,
        name,
        welcomeText,
        organizingClubId,
        competitionLevel,
        startDate,
        endDate
    )

    fun newCompetitionUpdateSpec(
        name: String = "TestCompetition",
        location: LocationSpec = LocationSpec("Arena IK"),
        welcomeText: String = "Välkommna till TestCompetition",
        competitionLevel: String = "A",
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(3)
    ) = CompetitionUpdateSpec(
        location,
        name,
        welcomeText,
        competitionLevel,
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

    fun newGameResult(
        id: Int = 1,
        number: Int = 1,
        firstRegistrationResult: Int = 11,
        secondRegistrationResult: Int = 8
    ) = GameResult(
        id,
        number,
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

    fun newScheduleMatchDTO(
        id: Int = this.matchId++,
        competitionCategoryId: Int = this.competitionCategoryId++,
        firstPlayer: List<Int> = listOf(1),
        secondPlayer: List<Int> = listOf(2),
        groupOrRound: String = "A"
    ) = ScheduleMatchDto(
        id,
        competitionCategoryId,
        firstPlayer,
        secondPlayer,
        groupOrRound
    )

    fun newMatchToTimeTableSlot(
        matchId: Int = this.matchId++,
        competitionCategoryId: Int = this.competitionCategoryId++,
        timeTableSlotId: Int = 1,
        startTime: LocalDateTime = LocalDateTime.now(),
        tableNumber: Int = 7,
        location: String = "Lule Energi Arena",
    ) = MatchToTimeTableSlot(
        matchId,
        competitionCategoryId,
        timeTableSlotId,
        startTime,
        tableNumber,
        location
    )

    fun newMapMatchToTimeTableSlotSpec(
        matchId: Int = this.matchId++,
        timeTableSlotId: Int = 1
    ) = MapMatchToTimeTableSlotSpec(
        matchId,
        timeTableSlotId
    )

    fun newTimeTableSlotToMatch(
        id: Int = 1,
        startTime: LocalDateTime = LocalDateTime.now(),
        tableNumber: Int = 1,
        location: String = "Hall A",
        matchInfo: TimeTableSlotMatchInfo? = null
    ) = TimeTableSlotToMatch(
        id,
        startTime,
        tableNumber,
        location,
        matchInfo
    )

    fun newTimeTableMatchInfo(
        id: Int = 44,
        competitionCategoryId: Int = 33
    ) = TimeTableSlotMatchInfo(
        id,
        competitionCategoryId
    )

    /**
     * Generates a set of matches for a pool with the given amount of players. The generated matches
     * are so that every player will go up against each other player exactly once.
     *
     * @param numberOfPlayers Number of players in the pool
     * @param categoryId Category the pool / players belong to
     */
    fun poolOf(numberOfPlayers: Int = 4, categoryId: Int = 2): List<ScheduleMatchDto> {
        assert(numberOfPlayers > 1) { "Yo! Think I can create a pool with less than 2 players?!" }
        assert(numberOfPlayers < 11) { "Not that many! Maximum of 10 players per pool." }

        val postFixes = ('A'..'J').toList()
        val players =
            (0..numberOfPlayers).map { newPlayerDTO(firstName = "Player" + postFixes[it], lastName = "LastName") }

        val matches = mutableListOf<ScheduleMatchDto>()
        for (i in 0..numberOfPlayers) {
            for (j in i + 1..numberOfPlayers) {
                matches.add(
                    ScheduleMatchDto(
                        matchId++,
                        categoryId,
                        listOf(players[i].id),
                        listOf(players[j].id),
                        "A"
                    )
                )
            }
        }
        return matches
    }


    /**
     * Pool with 4 players i.e. 6 matches
     */
    fun pool1(categoryId: Int = 1): List<ScheduleMatchDto> {
        return pool1.map {
            ScheduleMatchDto(
                it.id,
                categoryId,
                it.firstTeamPlayerIds,
                it.secondTeamPlayerIds,
                "A"
            )
        }
    }

    /**
     * Pool with 4 players i.e. 6 matches
     */
    fun pool2(categoryId: Int = 1): List<ScheduleMatchDto> {
        return pool2.map {
            ScheduleMatchDto(
                it.id,
                categoryId,
                it.firstTeamPlayerIds,
                it.secondTeamPlayerIds,
                "B"
            )
        }
    }

    /**
     * Pool with 3 players i.e 3 matches
     */
    fun pool3(categoryId: Int = 1): List<ScheduleMatchDto> {
        return pool3.map {
            ScheduleMatchDto(
                it.id,
                categoryId,
                it.firstTeamPlayerIds,
                it.secondTeamPlayerIds,
                "C"
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
        ScheduleMatchDto(1, 0, listOf(p1.id), listOf(p2.id), "A"),
        ScheduleMatchDto(2, 0, listOf(p1.id), listOf(p3.id), "A"),
        ScheduleMatchDto(3, 0, listOf(p1.id), listOf(p4.id), "A"),
        ScheduleMatchDto(4, 0, listOf(p2.id), listOf(p3.id), "A"),
        ScheduleMatchDto(5, 0, listOf(p2.id), listOf(p4.id), "A"),
        ScheduleMatchDto(6, 0, listOf(p3.id), listOf(p4.id), "A")
    )

    private val p5 = PlayerDTO(5, "Elin", "Malsson", club.id, birthDate)
    private val p6 = PlayerDTO(6, "Ewa", "Svensson", club.id, birthDate)
    private val p7 = PlayerDTO(7, "Katarina", "Dalhborg", club.id, birthDate)
    private val p8 = PlayerDTO(8, "Lena", "Sinè", club.id, birthDate)

    private val pool2 = listOf(
        ScheduleMatchDto(7, 0, listOf(p5.id), listOf(p6.id), "B"),
        ScheduleMatchDto(8, 0, listOf(p5.id), listOf(p7.id), "B"),
        ScheduleMatchDto(9, 0, listOf(p5.id), listOf(p8.id), "B"),
        ScheduleMatchDto(10, 0, listOf(p6.id), listOf(p7.id), "B"),
        ScheduleMatchDto(11, 0, listOf(p6.id), listOf(p8.id), "B"),
        ScheduleMatchDto(12, 0, listOf(p7.id), listOf(p8.id), "B")
    )

    private val p9 = PlayerDTO(9, "Patrik", "Larsson", club.id, birthDate)
    private val p10 = PlayerDTO(10, "Enok", "Karlsson", club.id, birthDate)
    private val p11 = PlayerDTO(11, "Tintin", "Snäll", club.id, birthDate)

    private val pool3 = listOf(
        ScheduleMatchDto(13, 0, listOf(p9.id), listOf(p10.id), "C"),
        ScheduleMatchDto(14, 0, listOf(p9.id), listOf(p11.id), "C"),
        ScheduleMatchDto(15, 0, listOf(p10.id), listOf(p11.id), "C")
    )

}
