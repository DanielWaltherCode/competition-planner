package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.api.ClubDTO
import com.graphite.competitionplanner.api.ClubNoAddressDTO
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
        playingCategoryRepository.addPlayingCategory("Herrar 1")
        playingCategoryRepository.addPlayingCategory("Herrar 2")
        playingCategoryRepository.addPlayingCategory("Herrar 3")
        playingCategoryRepository.addPlayingCategory("Herrar 4")
        playingCategoryRepository.addPlayingCategory("Herrar 5")
        playingCategoryRepository.addPlayingCategory("Herrar 6")
        playingCategoryRepository.addPlayingCategory("Damer 1")
        playingCategoryRepository.addPlayingCategory("Damer 2")
        playingCategoryRepository.addPlayingCategory("Damer 3")
        playingCategoryRepository.addPlayingCategory("Damer 4")
        playingCategoryRepository.addPlayingCategory("Damjuniorer 17")
        playingCategoryRepository.addPlayingCategory("Flickor 15")
        playingCategoryRepository.addPlayingCategory("Flickor 14")
        playingCategoryRepository.addPlayingCategory("Flickor 13")
        playingCategoryRepository.addPlayingCategory("Flickor 12")
        playingCategoryRepository.addPlayingCategory("Flickor 11")
        playingCategoryRepository.addPlayingCategory("Flickor 10")
        playingCategoryRepository.addPlayingCategory("Flickor 9")
        playingCategoryRepository.addPlayingCategory("Flickor 8")
        playingCategoryRepository.addPlayingCategory("Herrjuniorer 17")
        playingCategoryRepository.addPlayingCategory("Pojkar 15")
        playingCategoryRepository.addPlayingCategory("Pojkar 14")
        playingCategoryRepository.addPlayingCategory("Pojkar 13")
        playingCategoryRepository.addPlayingCategory("Pojkar 12")
        playingCategoryRepository.addPlayingCategory("Pojkar 11")
        playingCategoryRepository.addPlayingCategory("Pojkar 10")
        playingCategoryRepository.addPlayingCategory("Pojkar 9")
        playingCategoryRepository.addPlayingCategory("Pojkar 8")
    }

    fun playerSetup() {
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Oscar", lastName = "Hansson",
                club = ClubNoAddressDTO( util.getClubIdOrDefault("Lugi"), null), dateOfBirth = LocalDate.now().minus(18, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Nils", lastName = "Hansson",
                club = ClubNoAddressDTO( util.getClubIdOrDefault("Lugi"), null), dateOfBirth = LocalDate.now().minus(14, ChronoUnit.YEARS)))
        playerRepository.addPlayer(PlayerDTO(null, firstName = "Lennart", lastName = "Eriksson",
                club = ClubNoAddressDTO( util.getClubIdOrDefault("Umeå IK"), null), dateOfBirth = LocalDate.now().minus(19, ChronoUnit.YEARS)))
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
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("Herrar 1").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("Herrar 2").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("Damer 1").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("Damer 2").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("Damjuniorer 17").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(lugiCompetitionId, playingCategoryRepository.getByName("Damer 3").id)

        val umeaId = util.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = competitionService.getByClubId(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id ?: 0
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("Herrar 1").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("Herrar 2").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("Damer 1").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("Damer 2").id)
        competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(umeaCompetitionId, playingCategoryRepository.getByName("Damer 3").id)
    }

    fun registerPlayersSingles() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiPlayers = playerService.getPlayersByClubId(lugiId)
        val umePlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Umeå IK"))
        val otherPlayers = playerService.getPlayersByClubId(util.getClubIdOrDefault("Övriga"))
        val competitionCategories  = competitionAndPlayingCategoryRepository.getCompetitionCategories()
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, lugiPlayers[0].id ?: 0, competitionCategories[0].id))
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, lugiPlayers[0].id ?: 0, competitionCategories[1].id))
        registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, lugiPlayers[1].id ?: 0, competitionCategories[0].id))
        registrationService.registerPlayerSingles( RegistrationSinglesDTO(null, umePlayers[0].id ?: 0, competitionCategories[1].id))
        registrationService.registerPlayerSingles( RegistrationSinglesDTO(null, otherPlayers[0].id ?: 0, competitionCategories[1].id))
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

