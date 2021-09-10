package com.graphite.competitionplanner.competition

import com.graphite.competitionplanner.competition.api.CompetitionSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestCompetitionsService(
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val util: Util
) {

    @Test
    fun testGetCompetitions() {
        val competitions = competitionService.getCompetitions(null, null)
        Assertions.assertTrue(competitions.isNotEmpty())
    }

    @Test
    fun testAddCompetition() {
        val originalSize: Int = competitionRepository.getAll().size
        val competition = competitionService.addCompetition(
            CompetitionSpec(
                location = "Lund",
                name = "Moneky cup",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        )
        Assertions.assertNotNull(competition.id)
        Assertions.assertEquals(competition.endDate, LocalDate.now().plusDays(10))
        Assertions.assertEquals(originalSize + 1, competitionRepository.getAll().size)
    }

    @Test
    fun addCompetitionWithoutValidClub() {
        // No organizing club with id -1
        Assertions.assertThrows(org.springframework.dao.DataIntegrityViolationException::class.java) {
            competitionService.addCompetition(
                CompetitionSpec(
                    location = "Lund",
                    name = "Monkey cup",
                    welcomeText = "Välkomna till Eurofinans",
                    organizingClubId = -1,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now().plusDays(10)
                )
            )
        }

    }

    @Test
    fun deleteCompetition() {
        val competition = competitionService.addCompetition(
            CompetitionSpec(
                location = "Lund",
                name = "Monkey cup",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        )

        val originalSize: Int = competitionRepository.getAll().size
        competitionRepository.deleteCompetition(competition.id)
        Assertions.assertEquals(originalSize - 1, competitionRepository.getAll().size)
    }

    @Test
    fun updateCompetition() {
        val competitions = competitionService.getCompetitions(null, null)
        val competition = competitions[0]
        val updatedText = "My new description text"

        val competitionSpec = CompetitionSpec(
            competition.location, competition.name, updatedText,
            competition.organizingClub.id, competition.startDate, competition.endDate
        )

        val updatedDTO = competitionService.updateCompetition(competition.id, competitionSpec)
        Assertions.assertEquals(updatedText, updatedDTO.welcomeText)
    }


}