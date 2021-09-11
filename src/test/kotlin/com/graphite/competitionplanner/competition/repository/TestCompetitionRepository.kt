package com.graphite.competitionplanner.competition.repository

import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.util.DataGenerator
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
        val dto = dataGenerator.newClubSpec()
        club = clubRepository.store(dto)
    }

    @AfterEach
    fun deleteClub() {
        clubRepository.delete(club.id)
    }

    @Test
    fun shouldSetIdWhenStoring() {
        // Setup
        val newCompetition = dataGenerator.newCompetitionSpec(organizingClubId = club.id)

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
        val newCompetition1 = dataGenerator.newCompetitionSpec(organizingClubId = club.id)
        val competition1 = competitionRepository.store(newCompetition1)
        val newCompetition2 = dataGenerator.newCompetitionSpec(organizingClubId = club.id)
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

    @Test
    fun shouldUpdateValues() {
        // Setup
        val spec = dataGenerator.newCompetitionSpec(organizingClubId = club.id)
        val competition = competitionRepository.store(spec)

        // Act
        val updateSpec = dataGenerator.newCompetitionSpec(
            location = LocationSpec(competition.location.name),
            name = "NewName",
            welcomeText = "New text",
            organizingClubId = competition.organizerId,
            startDate = competition.startDate,
            endDate = competition.endDate
        )

        val updatedCompetition = competitionRepository.update(competition.id, updateSpec)

        // Assert
        Assertions.assertEquals(updateSpec.name, updatedCompetition.name)
        Assertions.assertEquals(updateSpec.location.name, updatedCompetition.location.name)
        Assertions.assertEquals(updateSpec.welcomeText, updatedCompetition.welcomeText)
        Assertions.assertEquals(updateSpec.startDate, updatedCompetition.startDate)
        Assertions.assertEquals(updateSpec.endDate, updatedCompetition.endDate)

        // Clean up
        competitionRepository.delete(competition.id)
    }

    @Test
    fun shouldThrowNotFoundExceptionIfClubCannotBeFoundWhenUpdating() {
        val spec = dataGenerator.newCompetitionSpec(organizingClubId = club.id)
        Assertions.assertThrows(NotFoundException::class.java) { competitionRepository.update(-1, spec) }
    }
}