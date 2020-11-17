package com.graphite.competitionplanner

import com.graphite.competitionplanner.api.ClubNoAddressDTO
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.CompetitionRepository
import com.graphite.competitionplanner.service.CompetitionDTO
import com.graphite.competitionplanner.service.CompetitionService
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestCompetitionsService(@Autowired val competitionService: CompetitionService, @Autowired val competitionRepository: CompetitionRepository, @Autowired val clubRepository: ClubRepository, @Autowired val util: Util) {

    @Test
    fun testGetCompetitions() {
        val competitions = competitionService.getCompetitions(null, null)
        Assertions.assertTrue(competitions.isNotEmpty())
    }

    @Test
    fun testAddCompetition() {
        val originalSize: Int = competitionRepository.getAll().size
        val competition = competitionService.addCompetition(CompetitionDTO(null,
                location = "Lund",
                welcomeText = "Välkomna till Eurofinans",
                organizingClub = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))
        Assertions.assertNotNull(competition.id)
        Assertions.assertEquals(competition.endDate, LocalDate.now().plusDays(10))
        Assertions.assertEquals(originalSize + 1, competitionRepository.getAll().size)
    }

    @Test
    fun deleteCompetition() {
        val competition = competitionService.addCompetition(CompetitionDTO(null,
                location = "Lund",
                welcomeText = "Välkomna till Eurofinans",
                organizingClub = ClubNoAddressDTO(util.getClubIdOrDefault("Lugi"), null),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))

        val originalSize: Int = competitionRepository.getAll().size
        competitionRepository.deleteCompetition(competition.id ?: 0)
        Assertions.assertEquals(originalSize - 1, competitionRepository.getAll().size)
    }

    @Test
    fun updateCompetition() {
        val competitions = competitionService.getCompetitions(null, null)
        val competition = competitions[0]
        val updatedText = "My new description text"

        val competitionDTO = CompetitionDTO(competition.id, competition.location, updatedText,
                competition.organizingClub, competition.startDate, competition.endDate)

        val updatedDTO = competitionService.updateCompetition(competitionDTO)
        Assertions.assertEquals(updatedText, updatedDTO.welcomeText)
    }
}