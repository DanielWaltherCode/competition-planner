package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubSpec
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.club.service.ClubService
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.PlayerSpec
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.player.service.PlayerService
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.user.api.UserSpec
import com.graphite.competitionplanner.user.repository.UserRepository
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.random.Random


@Component
class EventListener(
    val playerRepository: PlayerRepository,
    val competitionRepository: CompetitionRepository,
    val clubRepository: ClubRepository,
    val playerService: PlayerService,
    val categoryRepository: CategoryRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val registrationRepository: RegistrationRepository,
    val competitionService: CompetitionService,
    val competitionCategoryService: CompetitionCategoryService,
    val competitionDrawRepository: CompetitionDrawRepository,
    val userRepository: UserRepository,
    val util: Util,
    val registrationService: RegistrationService,
    val userService: UserService,
    val matchRepository: MatchRepository,
    val clubService: ClubService
) {

    @EventListener
    fun onApplicationEvent(event: ContextRefreshedEvent) {
        clearTables()
        setUpData()
    }

    fun setUpData() {
        setUpClub()
        competitionSetup()
        registerUsers()
        playerSetup()
        setUpBYEPlayer()
        setUpPlaceHolderRegistration()
        addPlayerRankings()
        categorySetup()
        competitionCategorySetup()
        registerPlayersSingles()
        registerMatch()
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
        clubService.addClub(ClubSpec("Övriga", "Empty"))
        clubService.addClub(ClubSpec("Lugi", "Lund"))
        clubService.addClub(ClubSpec("Umeå IK", "Umeå Ersboda"))
        clubService.addClub(ClubSpec("Malmö", "Malmö"))
        clubService.addClub(ClubSpec("Landskrona", "Landskrona Byaväg 9"))
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
        registrationRepository.addRegistrationWithId(1, LocalDate.now())
    }

    fun setUpBYEPlayer() {
        // Needs club, player, competition, competition category, registration
        // Should be ID 0 in all of them
        categoryRepository.addCategoryWithId(0, "BYE", "BYE")
        competitionRepository.addCompetitionWithId(
            0,
            CompetitionSpec(
                location = LocationSpec("BYE"),
                name = "BYE",
                welcomeText = "BYE",
                organizingClubId = util.getClubIdOrDefault("Övriga"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusYears(10)
            )
        )
        competitionCategoryService.addCompetitionCategory(0, CategorySpec(0, "BYE", "BYE"))
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
        competitionService.addCompetition(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Eurofinans 2021",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(3)
            )
        )
        competitionService.addCompetition(
            CompetitionSpec(
                location = LocationSpec("Umeå"),
                name = "Bollstadion Cup",
                welcomeText = "Umeå, kallt, öde, men vi har badminton!",
                organizingClubId = util.getClubIdOrDefault("Umeå IK"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(2)
            )
        )
        competitionService.addCompetition(
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
        val lugiCompetitions = competitionService.getByClubId(lugiId)
        val lugiCompetitionId = lugiCompetitions[0].id
        val categories = categoryRepository.getAvailableCategories()

        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categories.find { it.name == "Herrar 1" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categories.find { it.name == "Herrar 2" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categories.find { it.name == "Damer 1" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categories.find { it.name == "Damer 2" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categories.find { it.name == "Damjuniorer 17" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categories.find { it.name == "Damer 3" }!!.toSpec()
        )

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = competitionService.getByClubId(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id
        competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categories.find { it.name == "Herrar 1" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categories.find { it.name == "Herrar 2" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categories.find { it.name == "Damer 1" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categories.find { it.name == "Damer 2" }!!.toSpec()
        )
        competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categories.find { it.name == "Damer 3" }!!.toSpec()
        )
    }

    private fun CategoryDTO.toSpec(): CategorySpec {
        return CategorySpec(this.id, this.name, this.type)
    }

    fun registerPlayersSingles() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiPlayers = playerService.getPlayersByClubId(lugiId)
        val umePlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Umeå IK"))
        val otherPlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Övriga"))
        val lugiCompetitionId = competitionRepository.getByLocation("Lund")[0].id
        val competitionCategories = competitionCategoryService.getCompetitionCategoriesFor(lugiCompetitionId)

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
        matchRepository.clearTable()
        competitionDrawRepository.clearTable()
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

