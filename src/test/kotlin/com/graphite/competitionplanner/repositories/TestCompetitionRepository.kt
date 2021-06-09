package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionRepository(
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val clubRepository: IClubRepository
) {

    private val dataGenerator = DataGenerator()
    lateinit var club: ClubDTO

    @BeforeEach
    fun saveAClub() {
        val dto = dataGenerator.newClubDTO()
        club = clubRepository.store(dto)
    }

    @AfterEach
    fun deleteClub() {
        clubRepository.delete(club)
    }

    @Test
    fun shouldSetIdWhenStoring() {
        // Setup
        val newCompetition = dataGenerator.newNewCompetitionDTO(organizingClubId = club.id)

        // Act
        val competition = competitionRepository.store(newCompetition)

        // Assert
        Assertions.assertTrue(competition.id != 0)

        // Clean up
        competitionRepository.delete(competition.id)
    }

    @Test
    fun shouldThrowNotFoundWhenTryingToDeleteCompetitionThatDoesNotExist() {
        Assertions.assertThrows(NotFoundException::class.java) { competitionRepository.delete(-10) }
    }

    @Test
    fun shouldReturnAllCompetitionsForTheGivenClub() {
        // Setup
        val newCompetition1 = dataGenerator.newNewCompetitionDTO(organizingClubId = club.id)
        val competition1 = competitionRepository.store(newCompetition1)
        val newCompetition2 = dataGenerator.newNewCompetitionDTO(organizingClubId = club.id)
        val competition2 = competitionRepository.store(newCompetition2)

        // Act
        val competitions = competitionRepository.findCompetitionsFor(club.id)

        // Assert
        Assertions.assertTrue(competitions.contains(competition1), "Expected to find competition 1")
        Assertions.assertTrue(competitions.contains(competition2), "Expected to find competition 2")

        // Clean up
        competitionRepository.delete(competition1.id)
        competitionRepository.delete(competition2.id)
    }

    @Test
    fun shouldGetEmptyListIfClubDoesNotExist() {
        // Act
        val competitions = competitionRepository.findCompetitionsFor(-10)

        // Assert
        Assertions.assertTrue(competitions.isEmpty())
    }

    @Test
    fun shouldGetEmptyListIfClubDoesNotHaveAnyCompetitions() {
        // Act
        val competitions = competitionRepository.findCompetitionsFor(club.id)

        // Assert
        Assertions.assertTrue(competitions.isEmpty())
    }
}