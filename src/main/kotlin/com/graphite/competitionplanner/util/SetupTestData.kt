package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.api.ClubDTO
import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.api.RegistrationSinglesDTO
import com.graphite.competitionplanner.repositories.*
import com.graphite.competitionplanner.service.*
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@Component
class EventListener(val playerRepository: PlayerRepository,
                    val competitionRepository: CompetitionRepository,
                    val clubRepository: ClubRepository,
                    val playerService: PlayerService,
                    val playingCategoryRepository: PlayingCategoryRepository,
                    val competitionAndPlayingCategoryRepository: CompetitionAndPlayingCategoryRepository,
                    val registrationRepository: RegistrationRepository,
                    val competitionService: CompetitionService,
                    val util: Util,
val registrationService: RegistrationService) {

    @EventListener
    fun onApplicationEvent(event: ContextRefreshedEvent) {
        clearTables()
        setUpData()
    }

    fun setUpData() {
        setUpClub()
        competitionSetup()
        playerSetup()
        playingCategorySetup()
        competitionPlayingCategorySetup()
        registerPlayersSingles()
    }

    fun setUpClub() {
        clubRepository.addClub(ClubDTO(0, "Övriga", "Empty"))
        clubRepository.addClub(ClubDTO(null, "Lugi", "Lund"))
        clubRepository.addClub(ClubDTO(null, "Umeå IK", "Umeå Ersboda"))
    }

    fun playingCategorySetup() {
        playingCategoryRepository.addPlayingCategory("HS A")
        playingCategoryRepository.addPlayingCategory("HS B")
        playingCategoryRepository.addPlayingCategory("DS A")
        playingCategoryRepository.addPlayingCategory("DS B")
        playingCategoryRepository.addPlayingCategory("HD A")
        playingCategoryRepository.addPlayingCategory("HD B")
        playingCategoryRepository.addPlayingCategory("DD A")
        playingCategoryRepository.addPlayingCategory("DD B")
        playingCategoryRepository.addPlayingCategory("MX A")
    }

    fun playerSetup() {
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Oscar", lastName = "Hansson",
                club = ClubNoAddressDTO( util.getClubIdOrDefault("Lugi"), null), dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Nils", lastName = "Hansson",
                club = ClubNoAddressDTO( util.getClubIdOrDefault("Lugi"), null), dateOfBirth = LocalDate.now().minus(14, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Lennart", lastName = "Eriksson",
                club = ClubNoAddressDTO( util.getClubIdOrDefault("Umeå"), null), dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Kajsa", lastName = "Säfsten",
                club = ClubNoAddressDTO( util.getClubIdOrDefault("Övriga"), null), dateOfBirth = LocalDate.now().minus(65, ChronoUnit.YEARS)))
    }

    fun competitionSetup() {
        competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Lund",
                welcomeText = "Välkomna till Eurofinans",
                organizingClub = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))
        competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Umeå",
                welcomeText = "Umeå, kallt, öde, men vi har badminton!",
                organizingClub = ClubNoAddressDTO(util.getClubIdOrDefault("Umeå IK"), null),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))
        competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Svedala",
                welcomeText = "Bonustävling!",
                organizingClub = ClubNoAddressDTO(util.getClubIdOrDefault("Svedala"), null),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusMonths(10)))
    }

    fun competitionPlayingCategorySetup() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitions = competitionService.getByClubId(lugiId)
        val lugiCompetitionId = lugiCompetitions[0].id ?: 0
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("HS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("HS B").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("DS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("DD A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("MX A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("HS A").id)

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = competitionService.getByClubId(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id ?: 0
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("HS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("HS B").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("DS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("DD A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("MX A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("HS A").id)
    }

    fun registerPlayersSingles() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiPlayers = playerService.getPlayersByClubId(lugiId)
        val umePlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Umeå IK"))
        val otherPlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Övriga"))
        val competitionCategories  = competitionAndPlayingCategoryRepository.getCompetitionCategories()
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, lugiPlayers[0].id ?: 0, competitionCategories[0].id))
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, lugiPlayers[1].id ?: 0, competitionCategories[0].id))
        registrationService.registerPlayerSingles( RegistrationSinglesDTO(null, umePlayers[0].id ?: 0, competitionCategories[1].id))
        registrationService.registerPlayerSingles( RegistrationSinglesDTO(null, otherPlayers[0].id ?: 0, competitionCategories[1].id))
        println("Players registered")
    }

    // Must be in correct order depending on foreign keys
    private fun clearTables() {
        registrationRepository.clearPlayingIn()
        registrationRepository.clearPlayerRegistration()
        registrationRepository.clearRegistration()
        competitionAndPlayingCategoryRepository.clearTable()
        competitionRepository.clearTable()
        playerRepository.clearTable()
        playingCategoryRepository.clearTable()
        clubRepository.clearClubTable()
    }


}

