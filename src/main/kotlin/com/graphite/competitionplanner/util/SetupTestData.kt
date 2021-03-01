package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.api.*
import com.graphite.competitionplanner.repositories.*
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.Match
import com.graphite.competitionplanner.service.competition.MatchType
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
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
    val drawTypeRepository: DrawTypeRepository,
    val userRepository: UserRepository,
    val util: Util,
    val registrationService: RegistrationService,
    val userService: UserService,
    val matchRepository: MatchRepository
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
        addPlayerRankings()
        categorySetup()
        competitionCategorySetup()
        registerPlayersSingles()
        registerMatch()
    }

    fun registerUsers() {
        userService.addUser(UserSpec(
           username = "abraham",
           password = "anders",
            clubId = util.getClubIdOrDefault("Lugi")
        ))
        userService.addUser(UserSpec(
            username = "jasmine",
            password = "presto!",
            clubId = util.getClubIdOrDefault("Lugi")
        ))
    }

    fun setUpClub() {
        clubRepository.addClub(ClubDTO(0, "Övriga", "Empty"))
        clubRepository.addClub(ClubDTO(null, "Lugi", "Lund"))
        clubRepository.addClub(ClubDTO(null, "Umeå IK", "Umeå Ersboda"))
        clubRepository.addClub(ClubDTO(null, "Malmö", "Malmö"))
        clubRepository.addClub(ClubDTO(null, "Landskrona", "Landskrona Byaväg 9"))
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

    fun setUpBYEPlayer() {
        // Needs club, player, competition, competition category, registration
        // Should be ID 0 in all of them
        categoryRepository.addCategoryWithId(0, "BYE", "BYE")
        competitionRepository.addCompetitionWithId(
            0,
            CompetitionSpec(
                location = "BYE",
                welcomeText = "BYE",
                organizingClubId = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusYears(10)
            )
        )
        competitionCategoryService.addCompetitionCategory(0, 0)
        playerRepository.addPlayerWithId(
            0,
            PlayerSpec(
                firstName = "BYE",
                lastName = "",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Övriga"), null),
                dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)
            )
        )
        registrationRepository.addRegistrationWithId(0, LocalDate.now())
        registrationRepository.registerPlayer(0, 0)
    }

    fun playerSetup() {
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Oscar",
                lastName = "Hansson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)
            )
        )

        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Nils",
                lastName = "Hansson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(14, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Umeå IK"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Kajsa",
                lastName = "Säfsten",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Övriga"), null),
                dateOfBirth = LocalDate.now().minus(65, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Malmö"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Lennart",
                lastName = "Eriksson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Landskrona"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Mona",
                lastName = "Nilsson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Umeå IK"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Anders",
                lastName = "And",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Malmö"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Lukas",
                lastName = "Eriksson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Nina",
                lastName = "Persson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Umeå IK"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Ola",
                lastName = "Salo",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Landskrona"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Ola",
                lastName = "Larsson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Anna",
                lastName = "Lindh",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Oscar",
                lastName = "Lilja",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Landskrona"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Sam",
                lastName = "Axén",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Umeå IK"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Nils",
                lastName = "Sundling",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Landskrona"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Amanda",
                lastName = "Skiffer",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Eskil",
                lastName = "Erlandsson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Erna",
                lastName = "Solberg",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Umeå IK"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Dwight",
                lastName = "Johnson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Landskrona"), null),
                dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)
            )
        )
        playerRepository.addPlayer(
            PlayerSpec(
                firstName = "Simon",
                lastName = "Knutsson",
                club = ClubNoAddressDTO(util.getClubIdOrDefault("Umeå IK"), null),
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
        competitionRepository.addCompetition(
            CompetitionSpec(
                location = "Lund",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        )
        competitionRepository.addCompetition(
            CompetitionSpec(
                location = "Umeå",
                welcomeText = "Umeå, kallt, öde, men vi har badminton!",
                organizingClubId = util.getClubIdOrDefault("Umeå IK"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        )
        competitionRepository.addCompetition(
            CompetitionSpec(
                location = "Svedala",
                welcomeText = "Bonustävling!",
                organizingClubId = util.getClubIdOrDefault("Svedala"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusMonths(10)
            )
        )
    }

    fun competitionCategorySetup() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitions = competitionService.getByClubId(lugiId)
        val lugiCompetitionId = lugiCompetitions[0].id ?: 0
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categoryRepository.getByName("Herrar 1").id
        )
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categoryRepository.getByName("Herrar 2").id
        )
        competitionCategoryService.addCompetitionCategory(lugiCompetitionId, categoryRepository.getByName("Damer 1").id)
        competitionCategoryService.addCompetitionCategory(lugiCompetitionId, categoryRepository.getByName("Damer 2").id)
        competitionCategoryService.addCompetitionCategory(
            lugiCompetitionId,
            categoryRepository.getByName("Damjuniorer 17").id
        )
        competitionCategoryService.addCompetitionCategory(lugiCompetitionId, categoryRepository.getByName("Damer 3").id)

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = competitionService.getByClubId(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id ?: 0
        competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categoryRepository.getByName("Herrar 1").id
        )
        competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categoryRepository.getByName("Herrar 2").id
        )
        competitionCategoryService.addCompetitionCategory(umeaCompetitionId, categoryRepository.getByName("Damer 1").id)
        competitionCategoryService.addCompetitionCategory(umeaCompetitionId, categoryRepository.getByName("Damer 2").id)
        competitionCategoryService.addCompetitionCategory(umeaCompetitionId, categoryRepository.getByName("Damer 3").id)
    }

    fun registerPlayersSingles() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiPlayers = playerService.getPlayersByClubId(lugiId)
        val umePlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Umeå IK"))
        val otherPlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Övriga"))
        val competitionCategories = competitionCategoryRepository.getCompetitionCategories()

        // Have "Herrar 1" in Lugi as main competition category to play around with
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[0].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[0].id ?: 0,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[1].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                umePlayers[0].id ?: 0,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                otherPlayers[0].id ?: 0,
                competitionCategories[1].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[2].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[3].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[4].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[5].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[6].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                lugiPlayers[7].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                umePlayers[0].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                umePlayers[1].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                umePlayers[2].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                umePlayers[3].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                umePlayers[4].id ?: 0,
                competitionCategories[0].id
            )
        )
        registrationService.registerPlayerSingles(
            RegistrationSinglesDTO(
                null,
                umePlayers[5].id ?: 0,
                competitionCategories[0].id
            )
        )
    }

    fun registerMatch() {
        val competitionCategories = competitionCategoryRepository.getCompetitionCategories()
        val competitionCategoryId = competitionCategories[0].id
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId)

        matchRepository.addMatch(
            Match(
                startTime = LocalDateTime.now(),
                endTime = LocalDateTime.now().plusHours(1),
                competitionCategoryId = competitionCategoryId,
                firstRegistrationId = registrationIds[0],
                secondRegistrationId = registrationIds[1],
                matchType = MatchType.GROUP,
                matchOrderNumber = 1,
                groupOrRound = "A"
            )
        )
    }

    // Must be in correct order depending on foreign keys
    private fun clearTables() {
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

