package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.domain.CreateClub
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.AddCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.domain.ListAllPlayersInClub
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpec
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.user.api.UserSpec
import com.graphite.competitionplanner.user.repository.UserRepository
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random


@Component
class EventListener(
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
    val addCompetitionCategory: AddCompetitionCategory
) {

    @EventListener
    fun onApplicationEvent(event: ContextRefreshedEvent) {
        clearTables()
        setUpData()
    }

    fun setUpData() {
        setUpClub()
        competitionSetup()
        try {
            setUpBYEPlayer()
            setUpPlaceHolderRegistration()
        } catch (ex: DuplicateKeyException) {
            // Nothing
        }
        registerUsers()
        playerSetup()
        addPlayerRankings()
        categorySetup()
        competitionCategorySetup()
        registerPlayersSingles()
        registerMatch()
        registerPlayersDoubles()
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
        categoryRepository.addCategory("Herrar 1", "SINGLES")
        categoryRepository.addCategory("Herrar 2", "SINGLES")
        categoryRepository.addCategory("Herrar 3", "SINGLES")
        categoryRepository.addCategory("Herrar 4", "SINGLES")
        categoryRepository.addCategory("Herrar 5", "SINGLES")
        categoryRepository.addCategory("Herrar 6", "SINGLES")
        categoryRepository.addCategory("Damer 1", "SINGLES")
        categoryRepository.addCategory("Damer 2", "SINGLES")
        categoryRepository.addCategory("Damer 3", "SINGLES")
        categoryRepository.addCategory("Damer 4", "SINGLES")
        categoryRepository.addCategory("Damjuniorer 17", "SINGLES")
        categoryRepository.addCategory("Flickor 15", "SINGLES")
        categoryRepository.addCategory("Flickor 14", "SINGLES")
        categoryRepository.addCategory("Flickor 13", "SINGLES")
        categoryRepository.addCategory("Flickor 12", "SINGLES")
        categoryRepository.addCategory("Flickor 11", "SINGLES")
        categoryRepository.addCategory("Flickor 10", "SINGLES")
        categoryRepository.addCategory("Flickor 9", "SINGLES")
        categoryRepository.addCategory("Flickor 8", "SINGLES")
        categoryRepository.addCategory("Herrjuniorer 17", "SINGLES")
        categoryRepository.addCategory("Pojkar 15", "SINGLES")
        categoryRepository.addCategory("Pojkar 14", "SINGLES")
        categoryRepository.addCategory("Pojkar 13", "SINGLES")
        categoryRepository.addCategory("Pojkar 12", "SINGLES")
        categoryRepository.addCategory("Pojkar 11", "SINGLES")
        categoryRepository.addCategory("Pojkar 10", "SINGLES")
        categoryRepository.addCategory("Pojkar 9", "SINGLES")
        categoryRepository.addCategory("Pojkar 8", "SINGLES")
        categoryRepository.addCategory("Herrdubbel", "DOUBLES")
        categoryRepository.addCategory("Damdubbel", "DOUBLES")
    }

    fun setUpPlaceHolderRegistration() {
        // We need a Placeholder player and registration to return matches with place holder players.
        playerRepository.addPlayerWithId(
            1,
            PlayerSpec(
                firstName = "Placeholder",
                lastName = "Placeholder",
                clubId = util.getClubIdOrDefault("Övriga"),
                dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)
            )
        )
        registrationRepository.addRegistrationWithId(1, LocalDate.now())
        registrationRepository.registerPlayer(1, 1)
    }

    fun setUpBYEPlayer() {
        playerRepository.addPlayerWithId(
            0,
            PlayerSpec(
                firstName = "BYE",
                lastName = "BYE",
                clubId = util.getClubIdOrDefault("Övriga"),
                dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)
            )
        )
        registrationRepository.addRegistrationWithId(0, LocalDate.now())
        registrationRepository.registerPlayer(0, 0)
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
            playerRepository.addPlayerRanking(player.id, Random.nextInt(0, 100), "SINGLES")
        }
    }

    fun competitionSetup() {
        createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Eurofinans 2021",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
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
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(2)
            )
        )
        createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Svedala"),
                name = "Svedala Open",
                welcomeText = "Bonustävling!",
                organizingClubId = util.getClubIdOrDefault("Övriga"),
                startDate = LocalDate.now().plusMonths(1),
                endDate = LocalDate.now().plusMonths(1).plusDays(3)
            )
        )
    }

    fun competitionCategorySetup() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitions = findCompetitions.thatBelongsTo(lugiId)
        val lugiCompetitionId = lugiCompetitions[0].id
        val categories = categoryRepository.getAvailableCategories()

        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == "Herrar 1" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == "Herrar 2" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == "Damer 1" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == "Damer 2" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == "Damjuniorer 17" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == "Damer 3" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            lugiCompetitionId,
            categories.find { it.name == "Herrdubbel" }!!.toSpec()
        )

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = findCompetitions.thatBelongsTo(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == "Herrar 1" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == "Herrar 2" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == "Damer 1" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == "Damer 2" }!!.toSpec()
        )
        addCompetitionCategory.execute(
            umeaCompetitionId,
            categories.find { it.name == "Damer 3" }!!.toSpec()
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
        val lugiCompetitionId = competitionRepository.findCompetitionsThatBelongsTo(lugiId)[0].id
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
        val lugiCompetitionId = competitionRepository.findCompetitionsThatBelongsTo(lugiId)[0].id
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
                lugiPlayers[0].id,
                competitionCategories[1].id
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
    }

    fun registerMatch() {

    }

    // Must be in correct order depending on foreign keys
    private fun clearTables() {
        competitionDrawRepository.clearTable()
        matchRepository.clearTable()
        userRepository.clearTable()
        registrationRepository.clearPlayingIn()
        registrationRepository.clearPlayerRegistration()
        registrationRepository.clearRegistration()
        competitionCategoryRepository.clearTable()
        competitionRepository.clearTable()
        playerRepository.clearRankingTable()
        playerRepository.clearTable()
        categoryRepository.clearTable()
        clubRepository.clearClubTable()
    }


}

