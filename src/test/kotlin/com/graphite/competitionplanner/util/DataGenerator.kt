package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.domain.interfaces.CategoryDTO
import com.graphite.competitionplanner.club.api.ClubNoAddressDTO
import com.graphite.competitionplanner.club.api.NewClubSpec
import com.graphite.competitionplanner.club.domain.interfaces.ClubDTO
import com.graphite.competitionplanner.competition.domain.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.domain.interfaces.LocationDTO
import com.graphite.competitionplanner.competition.domain.interfaces.NewCompetitionDTO
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.*
import com.graphite.competitionplanner.domain.entity.*
import com.graphite.competitionplanner.player.api.PlayerSpec
import com.graphite.competitionplanner.player.domain.interfaces.NewPlayerDTO
import com.graphite.competitionplanner.player.domain.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.domain.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.MatchDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleSettingsDTO
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.minutes

class DataGenerator {

    private var playerId = 0
    private var clubId = 0
    private var matchId = 0
    private var competitionId = 0
    private var competitionCategoryId = 0
    private var categoryId = 0

    internal fun newClub(
        id: Int = clubId++,
        name: String = "Lule IK",
        address: String = "Sjögatan"
    ) = Club(
        id,
        name,
        address
    )

    internal fun newPlayer(
        id: Int = playerId++,
        firstName: String = "Ida",
        lastName: String = "Larsson",
        dateOfBirth: LocalDate = LocalDate.of(1999, 1, 1)
    ) = Player(
        id,
        firstName,
        lastName,
        dateOfBirth
    )

    internal fun newMatch(
        id: Int = matchId++,
        competitionCategoryId: Int = 0,
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusMinutes(15),
        matchType: MatchType = MatchType("POOL"),
        firstPlayer: List<Int> = listOf(newPlayer(firstName = "Lars", lastName = "Åkesson").id),
        secondPlayer: List<Int> = listOf(newPlayer(firstName = "Lars", lastName = "Åkesson").id),
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
        averageMatchTime: Duration = 15.minutes,
        numberOfTables: Int = 8,
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusHours(8)
    ) = ScheduleSettings(
        averageMatchTime,
        numberOfTables,
        startTime,
        endTime
    )

    internal fun newLocation(
        name: String = "Svedala Arena"
    ) = Location(
        name
    )

    internal fun newCompetition(
        id: Int = competitionId++,
        location: Location = newLocation(),
        name: String = "TestCompetition",
        welcomeText: String = "Welcome to Test Competition",
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(1)
    ) = Competition(
        id,
        location,
        name,
        welcomeText,
        startDate,
        endDate
    )

    internal fun newCategory(
        id: Int = categoryId++,
        name: String = "A Test Category",
        type: CategoryType = CategoryType.SINGLES
    ) = Category(
        id,
        name,
        type
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

    fun newNewPlayerDTO(
        firstName: String = "Lars",
        lastName: String = "Larsson",
        clubId: Int = 1,
        dateOfBirth: LocalDate = LocalDate.of(1999, 1, 1)
    ) = NewPlayerDTO(
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

    fun newScheduleSettingsDTO(
        averageMatchTime: Duration = 15.minutes,
        numberOfTables: Int = 8,
        startTime: LocalDateTime = LocalDateTime.now(),
        endTime: LocalDateTime = LocalDateTime.now().plusHours(8)
    ) = ScheduleSettingsDTO(
        averageMatchTime,
        numberOfTables,
        startTime,
        endTime
    )

    fun newNewCompetitionDTO(
        name: String = "TestCompetition",
        location: String = "Arena IK",
        welcomeText: String = "Välkommna till TestCompetition",
        organizingClubId: Int = newClub().id,
        startDate: LocalDate = LocalDate.now(),
        endDate: LocalDate = LocalDate.now().plusDays(3)
    ) = NewCompetitionDTO(
        name,
        location,
        welcomeText,
        organizingClubId,
        startDate,
        endDate
    )

    fun newCompetitionDTO(
        id: Int = 1,
        location: LocationDTO = LocationDTO("Arena IK"),
        name: String = "Test Competition",
        welcomeText: String = "Välkommna till TestCompetition",
        organizingClubId: Int = newClub().id,
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
        category: CategoryDTO = newCategoryDTO(),
        settings: GeneralSettingsDTO = newGeneralSettingsDTO(),
        gameSettings: GameSettingsDTO = newGameSettingsDTO()
    ) = CompetitionCategoryDTO(
        id,
        status,
        category,
        settings,
        gameSettings
    )

    fun newCompetitionCategoryUpdateDTO(
        id: Int = 1,
        settings: GeneralSettingsDTO = newGeneralSettingsDTO(),
        gameSettings: GameSettingsDTO = newGameSettingsDTO()
    ) = CompetitionCategoryUpdateDTO(
        id,
        settings,
        gameSettings
    )

    fun newGameSettingsDTO(
        numberOfSets: Int = 2,
        winScore: Int = 11,
        winMargin: Int = 3,
        differentNumberOfGamesFromRound: Round = Round.UNKNOWN,
        numberOfSetsFinal: Int = 5,
        winScoreFinal: Int = 7,
        winMarginFinal: Int = 3,
        tiebreakInFinalGame: Boolean = false,
        winScoreTiebreak: Int = 3,
        winMarginTieBreak: Int = 3
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
        winMarginTieBreak
    )

    fun newGeneralSettingsDTO(
        cost: Float = 150f,
        drawType: DrawTypeDTO = DrawTypeDTO(DrawType.POOL_ONLY),
        playersPerGroup: Int = 4,
        playersToPlayOff: Int = 2,
        poolDrawStrategy: PoolDrawStrategyDTO = PoolDrawStrategyDTO(PoolDrawStrategy.NORMAL)
    ) = GeneralSettingsDTO(
        cost,
        drawType,
        playersPerGroup,
        playersToPlayOff,
        poolDrawStrategy
    )


    fun newClubSpec(): NewClubSpec {
        return NewClubSpec("Club" + Random.nextLong().toString(), "Address" + Random.nextLong().toString())
    }

    fun newPlayerSpec(club: ClubNoAddressDTO): PlayerSpec {
        return PlayerSpec("Lasse", "Larrson", club.id, LocalDate.now().minusYears(20))
    }

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
            (0..numberOfPlayers).map { newPlayer(firstName = "Player" + postFixes[it], lastName = "LastName") }

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
