package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.domain.CreateClub
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionLevel
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.AddCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.domain.ListAllPlayersInClub
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.domain.asInt
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpec
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.user.api.UserSpec
import com.graphite.competitionplanner.user.repository.UserRepository
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.context.annotation.Profile
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random


@Component
@Profile("local", "test")
class SetupTestData(
    val playerRepository: PlayerRepository,
    val competitionRepository: CompetitionRepository,
    val clubRepository: ClubRepository,
    val listAllPlayersInClub: ListAllPlayersInClub,
    val categoryRepository: CategoryRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val registrationRepository: RegistrationRepository,
    val createCompetition: CreateCompetition,
    val competitionDrawRepository: CompetitionDrawRepository,
    val userRepository: UserRepository,
    val util: Util,
    val registrationService: RegistrationService,
    val userService: UserService,
    val matchRepository: MatchRepository,
    val createClub: CreateClub,
    val findCompetitions: FindCompetitions,
    val getCompetitionCategories: GetCompetitionCategories,
    val addCompetitionCategory: AddCompetitionCategory,
    val createDraw: CreateDraw,
    val resultService: ResultService,
    val scheduleRepository: ScheduleRepository
) {

    fun resetTestData() {
        clearTables()
        setUpData()
    }

    fun setUpData() {
        setUpClub()
        competitionSetup()
        trySetupByeAndPlaceHolder()
        registerUsers()
        playerSetup()
        addPlayerRankings()
        categorySetup()
        competitionCategorySetup()
        registerPlayersSingles()
        registerResults()
        registerPlayersDoubles()
    }

    fun trySetupByeAndPlaceHolder() {
        try {
            setUpBYEPlayer()
            setUpPlaceHolderRegistration()
        } catch (ex: DuplicateKeyException) {
            // Nothing
        }
    }

    fun registerUsers() {
        userService.addUser(
            UserSpec(
                username = "abraham",
                password = "anders",
                clubId = util.getClubIdOrDefault("Lugi")
            )
        )
        userService.addUser(
            UserSpec(
                username = "jasmine",
                password = "presto!",
                clubId = util.getClubIdOrDefault("Lugi")
            )
        )
    }

    fun setUpClub() {
        createClub.execute(ClubSpec("Övriga", "Empty"))
        createClub.execute(ClubSpec("Lugi", "Lund"))
        createClub.execute(ClubSpec("Umeå IK", "Umeå Ersboda"))
        createClub.execute(ClubSpec("Malmö", "Malmö"))
        createClub.execute(ClubSpec("Landskrona", "Landskrona Byaväg 9"))
    }

    fun categorySetup() {
        DefaultCategory.values().forEach {
            categoryRepository.addCategory(it.name, it.type)
        }
    }

    fun setUpPlaceHolderRegistration() {
        // We need a Placeholder player and registration to return matches with placeholder players.
        playerRepository.addPlayerWithId(
            Registration.Placeholder().asInt(),
            PlayerSpec(
                firstName = "Placeholder",
                lastName = "Placeholder",
                clubId = util.getClubIdOrDefault("Övriga"),
                dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)
            )
        )
        registrationRepository.addRegistrationWithId(Registration.Placeholder().asInt(), LocalDate.now())
        registrationRepository.registerPlayer(Registration.Placeholder().asInt(), Registration.Placeholder().asInt())
    }

    fun setUpBYEPlayer() {
        playerRepository.addPlayerWithId(
            Registration.Bye.asInt(),
            PlayerSpec(
                firstName = "BYE",
                lastName = "BYE",
                clubId = util.getClubIdOrDefault("Övriga"),
                dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)
            )
        )
        registrationRepository.addRegistrationWithId(Registration.Bye.asInt(), LocalDate.now())
        registrationRepository.registerPlayer(Registration.Bye.asInt(), Registration.Bye.asInt())
    }

    fun playerSetup() {
        playerRepository.store(
            PlayerSpec(
                firstName = "Oscar",
                lastName = "Hansson",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)
            )
        )

        playerRepository.store(
            PlayerSpec(
                firstName = "Nils",
                lastName = "Hansson",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(14, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                clubId = util.getClubIdOrDefault("Umeå IK"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Kajsa",
                lastName = "Säfsten",
                clubId = util.getClubIdOrDefault("Övriga"),
                dateOfBirth = LocalDate.now().minus(65, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Stefan",
                lastName = "Samuelsson",
                clubId = util.getClubIdOrDefault("Övriga"),
                dateOfBirth = LocalDate.now().minus(65, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                clubId = util.getClubIdOrDefault("Malmö"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                clubId = util.getClubIdOrDefault("Landskrona"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Mona",
                lastName = "Nilsson",
                clubId = util.getClubIdOrDefault("Umeå IK"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Anders",
                lastName = "And",
                clubId = util.getClubIdOrDefault("Malmö"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Lukas",
                lastName = "Eriksson",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Nina",
                lastName = "Persson",
                clubId = util.getClubIdOrDefault("Umeå IK"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Ola",
                lastName = "Salo",
                clubId = util.getClubIdOrDefault("Landskrona"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Ola",
                lastName = "Larsson",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Anna",
                lastName = "Lindh",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Oscar",
                lastName = "Lilja",
                clubId = util.getClubIdOrDefault("Landskrona"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Sam",
                lastName = "Axén",
                clubId = util.getClubIdOrDefault("Umeå IK"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Nils",
                lastName = "Sundling",
                clubId = util.getClubIdOrDefault("Landskrona"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Amanda",
                lastName = "Skiffer",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Eskil",
                lastName = "Erlandsson",
                clubId = util.getClubIdOrDefault("Lugi"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Erna",
                lastName = "Solberg",
                clubId = util.getClubIdOrDefault("Umeå IK"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Dwight",
                lastName = "Johnson",
                clubId = util.getClubIdOrDefault("Landskrona"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.store(
            PlayerSpec(
                firstName = "Simon",
                lastName = "Knutsson",
                clubId = util.getClubIdOrDefault("Umeå IK"),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
    }

    fun addPlayerRankings() {
        val allPlayers = playerRepository.getAll()

        for (player in allPlayers) {
            playerRepository.addPlayerRanking(player.id, Random.nextInt(0, 100), CategoryType.SINGLES.name)
        }
    }

    fun competitionSetup() {
        createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Eurofinans 2021",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                competitionLevel=CompetitionLevel.A.name,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(3)
            )
        )
        createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Umeå"),
                name = "Bollstadion Cup",
                welcomeText = "Umeå, kallt, öde, men vi har badminton!",
                organizingClubId = util.getClubIdOrDefault("Umeå IK"),
                competitionLevel=CompetitionLevel.B.name,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(2)
            )
        )
        createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Svedala"),
                name = "Svedala Open",
                welcomeText = "Bonustävling!",
                competitionLevel=CompetitionLevel.C.name,
                organizingClubId = util.getClubIdOrDefault("Övriga"),
                startDate = LocalDate.now().plusMonths(1),
                endDate = LocalDate.now().plusMonths(1).plusDays(3)
            )
        )
    }

    fun competitionCategorySetup() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitions = findCompetitions.thatBelongTo(lugiId)
        val lugiCompetitionId = lugiCompetitions[0].id
        val categories = categoryRepository.getAvailableCategories(lugiCompetitionId)

        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == DefaultCategory.MEN_1.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == DefaultCategory.MEN_2.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == DefaultCategory.WOMEN_1.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == DefaultCategory.WOMEN_2.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == DefaultCategory.WOMEN_JUNIOR_17.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == DefaultCategory.WOMEN_3.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == DefaultCategory.MEN_TEAMS.name }!!.toSpec()
        )

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = findCompetitions.thatBelongTo(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == DefaultCategory.MEN_1.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == DefaultCategory.MEN_2.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == DefaultCategory.WOMEN_1.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == DefaultCategory.WOMEN_2.name }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == DefaultCategory.WOMEN_3.name }!!.toSpec()
        )
    }

    private fun CategoryDTO.toSpec(): CategorySpec {
        return CategorySpec(this.id, this.name, this.type)
    }

    fun registerPlayersDoubles() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiPlayers = listAllPlayersInClub.execute(lugiId)
        val umePlayers = listAllPlayersInClub.execute(util.getClubIdOrDefault("Umeå IK"))
        val otherPlayers = listAllPlayersInClub.execute(util.getClubIdOrDefault("Övriga"))
        val lugiCompetitionId = competitionRepository.findCompetitionsThatBelongTo(lugiId)[0].id
        val competitionCategories = getCompetitionCategories.execute(lugiCompetitionId)

        registrationService.registerPlayersDoubles(
            RegistrationDoublesSpec(
                lugiPlayers[0].id,
                lugiPlayers[1].id,
                competitionCategories[6].id
            )
        )
        registrationService.registerPlayersDoubles(
            RegistrationDoublesSpec(
                lugiPlayers[2].id,
                umePlayers[0].id,
                competitionCategories[6].id
            )
        )
        registrationService.registerPlayersDoubles(
            RegistrationDoublesSpec(
                lugiPlayers[3].id,
                umePlayers[1].id,
                competitionCategories[6].id
            )
        )
        registrationService.registerPlayersDoubles(
            RegistrationDoublesSpec(
                lugiPlayers[4].id,
                lugiPlayers[5].id,
                competitionCategories[6].id
            )
        )
        registrationService.registerPlayersDoubles(
            RegistrationDoublesSpec(
                otherPlayers[0].id,
                otherPlayers[1].id,
                competitionCategories[6].id
            )
        )
        registrationService.registerPlayersDoubles(
            RegistrationDoublesSpec(
                umePlayers[2].id,
                umePlayers[3].id,
                competitionCategories[6].id
            )
        )
    }

    fun registerPlayersSingles() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiPlayers = listAllPlayersInClub.execute(lugiId)
        val umePlayers = listAllPlayersInClub.execute(util.getClubIdOrDefault("Umeå IK"))
        val otherPlayers = listAllPlayersInClub.execute(util.getClubIdOrDefault("Övriga"))
        val lugiCompetitionId = competitionRepository.findCompetitionsThatBelongTo(lugiId)[0].id
        val competitionCategories = getCompetitionCategories.execute(lugiCompetitionId)

        // Have "Herrar 1" in Lugi as main competition category to play around with
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[0].id,
                competitionCategories[0].id
            )
        )

        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[1].id,
                competitionCategories[0].id
            )
        )

        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[2].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[3].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[4].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[5].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[6].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[7].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[0].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[1].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[2].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[3].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[4].id,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[5].id,
                competitionCategories[0].id
            )
        )

        // Herrar 2
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[0].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[1].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[2].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[3].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                lugiPlayers[4].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[0].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                otherPlayers[0].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[2].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[3].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[4].id,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                umePlayers[5].id,
                competitionCategories[1].id
            )
        )
    }

    // Draw category and register match results in Herrar 2 (competitionCategories[1]
    fun registerResults() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitionId = competitionRepository.findCompetitionsThatBelongTo(lugiId)[0].id
        val competitionCategories = getCompetitionCategories.execute(lugiCompetitionId)
        val herrar2 = competitionCategories[1]
        val draw = createDraw.execute(herrar2.id)

        for (group in draw.groups) {
            if (group.name == "A") {
                for ((index, match) in group.matches.withIndex()) {
                    val categoryId = match.competitionCategory.id
                    val mainPlayer = if (index == 1) match.secondPlayer.first().id else match.firstPlayer.first().id
                    val playerToBeat = if (index == 1) match.firstPlayer.first().id else match.firstPlayer.first().id
                    beatPlayerWithExactScore(
                        mainPlayer = registrationRepository.getRegistrationIdForPlayerInCategory(
                            categoryId,
                            mainPlayer
                        ),
                        playerToBeat = registrationRepository.getRegistrationIdForPlayerInCategory(
                            categoryId,
                            playerToBeat
                        ),
                        mainPlayerResults = mutableListOf(11, 11, 11),
                        playerToBeatResults = mutableListOf(9, 9, 8),
                        competitionCategoryId = categoryId
                    )
                }
            }
            else {
                for (match in group.matches) {
                    val generatedGameResult = createResult()
                    resultService.addFinalMatchResult(
                        match.id,
                        ResultSpec(generatedGameResult)
                    )
                }
            }

        }

    }

    // Must be in correct order depending on foreign keys
    private fun clearTables() {
        competitionDrawRepository.clearTable()
        matchRepository.clearTable()
        scheduleRepository.clearTimeSlotTable()
        userRepository.clearTable()
        registrationRepository.clearPlayingIn()
        registrationRepository.clearPlayerRegistration()
        registrationRepository.clearRegistration()
        competitionCategoryRepository.clearTable()
        playerRepository.clearRankingTable()
        playerRepository.clearTable()
        competitionRepository.clearTable()
        categoryRepository.clearTable()
        clubRepository.clearClubTable()
        competitionDrawRepository.clearPoolTable()
    }

    fun beatPlayerWithExactScore(
        mainPlayer: Int,
        playerToBeat: Int,
        mainPlayerResults: List<Int>,
        playerToBeatResults: List<Int>,
        competitionCategoryId: Int
    ) {
        val matches = matchRepository.getMatchesInCategory(competitionCategoryId)
        val matchesInGroupA = matches.filter { it.groupOrRound == "A" }

        // Select their match
        val match = matchesInGroupA.first {
            (it.firstRegistrationId == mainPlayer || it.secondRegistrationId == mainPlayer)
                    && (playerToBeat == it.firstRegistrationId || playerToBeat == it.secondRegistrationId)
                    && it.winner == null
        }

        val gameResults: MutableList<GameSpec> = mutableListOf()
        if (match.firstRegistrationId == mainPlayer) {
            for (i in mainPlayerResults.indices) {
                gameResults.add(GameSpec(i + 1, mainPlayerResults[i], playerToBeatResults[i]))
            }
        } else if (match.secondRegistrationId == mainPlayer) {
            for (i in mainPlayerResults.indices) {
                gameResults.add(GameSpec(i + 1, playerToBeatResults[i], mainPlayerResults[i]))
            }
        }
        resultService.addFinalMatchResult(
            match.id,
            ResultSpec(gameResults)
        )
    }

    private fun createResult(): List<GameSpec> {
        val nrGames = Random.nextInt(3, 6)
        val winningPlayer = Random.nextInt(1, 3)

        val winningPlayerResults = mutableListOf<Int>()
        while (winningPlayerResults.size < nrGames) {
            winningPlayerResults.add(Random.nextInt(0, 11))
        }
        while (winningPlayerResults.count { i -> i == 11 } < 3) {
            val gameToRemove = Random.nextInt(0, nrGames)
            winningPlayerResults.removeAt(gameToRemove)
            val value = Random.nextInt(4, 12)
            winningPlayerResults.add(value)
        }
        val gameResults = mutableListOf<GameSpec>()
        for ((index, winningPlayerResult) in winningPlayerResults.withIndex()) {
            val otherPlayerResult = when (winningPlayerResult) {
                10 -> 12
                11 -> Random.nextInt(0, 10)
                else -> 11
            }
            if (winningPlayer == 1) {
                gameResults.add(GameSpec(index + 1, winningPlayerResult, otherPlayerResult))
            } else {
                gameResults.add(GameSpec(index + 1, otherPlayerResult, winningPlayerResult))
            }
        }
        return gameResults
    }
}