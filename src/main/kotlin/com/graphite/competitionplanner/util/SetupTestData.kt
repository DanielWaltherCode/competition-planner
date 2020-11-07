package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.api.ClubDTO
import com.graphite.competitionplanner.api.CompetitionDTO
import com.graphite.competitionplanner.api.PlayerDTO
import com.graphite.competitionplanner.api.RegistrationSinglesDTO
import com.graphite.competitionplanner.repositories.*
import com.graphite.competitionplanner.service.RegistrationService
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.temporal.ChronoUnit


@Component
class EventListener(val playerRepository: PlayerRepository,
                    val competitionRepository: CompetitionRepository,
                    val clubRepository: ClubRepository,
                    val playingCategoryRepository: PlayingCategoryRepository,
                    val competitionAndPlayingCategoryRepository: CompetitionAndPlayingCategoryRepository,
                    val registrationRepository: RegistrationRepository,
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
                clubId = util.getClubIdOrDefault("Lugi"), dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Nils", lastName = "Hansson",
                clubId = util.getClubIdOrDefault("Lugi"), dateOfBirth = LocalDate.now().minus(14, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Lennart", lastName = "Eriksson",
                clubId = util.getClubIdOrDefault("Umeå IK"), dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Kajsa", lastName = "Säfsten",
                clubId = util.getClubIdOrDefault("Övriga"), dateOfBirth = LocalDate.now().minus(65, ChronoUnit.YEARS)))
    }

    fun competitionSetup() {
        competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Lund",
                welcomeText = "Välkomna till Eurofinans",
                organizingClub = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))
        competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Umeå",
                welcomeText = "Umeå, kallt, öde, men vi har badminton!",
                organizingClub = util.getClubIdOrDefault("Umeå IK"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))
        competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Svedala",
                welcomeText = "Bonustävling!",
                organizingClub = util.getClubIdOrDefault("Svedala"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusMonths(10)))
    }

    fun competitionPlayingCategorySetup() {
        val competitionsByClub = competitionRepository.getByClubName("Lugi")
        val lugiCompetitionId = competitionsByClub[0].id
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("HS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("HS B").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("DS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("DD A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("MX A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("HS A").id)

        val competitionsForUmea = competitionRepository.getByClubName("Umeå IK")
        val umeaCompetitionId = competitionsForUmea[0].id
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("HS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("HS B").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("DS A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("DD A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("MX A").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("HS A").id)
    }

    fun registerPlayersSingles() {
        val players = playerRepository.getPlayers()
        val competitionCategories  = competitionAndPlayingCategoryRepository.getCompetitionCategories()
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, players.get(0).id, competitionCategories.get(0).id))
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, players.get(1).id, competitionCategories.get(0).id))
        registrationService.registerPlayerSingles( RegistrationSinglesDTO(null, players.get(2).id, competitionCategories.get(1).id))
        registrationService.registerPlayerSingles( RegistrationSinglesDTO(null, players.get(3).id, competitionCategories.get(1).id))
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

