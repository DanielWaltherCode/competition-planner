package com.graphite.competitionplanner

import com.graphite.competitionplanner.api.CompetitionDTO
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.CompetitionRepository
import com.graphite.competitionplanner.tables.pojos.Competition
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestCompetitionsService(@Autowired val competitionRepository: CompetitionRepository, @Autowired val clubRepository: ClubRepository, @Autowired val util: Util) {

    @Test
    fun testGetCompetitions() {
        val competitions: List<Competition> = competitionRepository.getCompetitions(null, null)
        Assertions.assertTrue(competitions.isNotEmpty())
    }

    @Test
    fun testAddCompetition() {
        val originalSize: Int = competitionRepository.getAll().size
        val competition: Competition = competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Lund",
                welcomeText = "Välkomna till Eurofinans",
                organizingClub = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))
        Assertions.assertNotNull(competition.id)
        Assertions.assertEquals(competition.endDate, LocalDate.now().plusDays(10))
        Assertions.assertEquals(originalSize + 1, competitionRepository.getAll().size)
    }

    @Test
    fun deleteCompetition() {
        val competition: Competition = competitionRepository.addCompetition(CompetitionDTO(null,
                location = "Lund",
                welcomeText = "Välkomna till Eurofinans",
                organizingClub = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)))

        val originalSize: Int = competitionRepository.getAll().size
        competitionRepository.deleteCompetition(competition.id)
        Assertions.assertEquals(originalSize - 1, competitionRepository.getAll().size)
    }

    @Test
    fun updateCompetition() {
        val competitions = competitionRepository.getByClubName("Lugi")
        val competition = competitions[0]
        val updatedText = "My new description text"

        val competitionDTO = CompetitionDTO(competition.id, competition.location, updatedText,
        competition.organizingClub, competition.startDate, competition.endDate)

        val updatedDTO = competitionRepository.updateCompetition(competitionDTO)
        Assertions.assertEquals(updatedText, updatedDTO.welcomeText)
    }

    @Test
    fun updateWithoutId() {
        val competitions = competitionRepository.getByClubName("Lugi")
        val competition = competitions[0]

        val competitionDTO = CompetitionDTO(null, competition.location, competition.welcomeText,
                competition.organizingClub, competition.startDate, competition.endDate)

        Assertions.assertThrows(IllegalStateException::class.java) {
            competitionRepository.updateCompetition(competitionDTO)
        }
    }
}