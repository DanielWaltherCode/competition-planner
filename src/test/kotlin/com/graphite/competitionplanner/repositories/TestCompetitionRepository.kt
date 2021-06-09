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
}