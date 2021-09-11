package com.graphite.competitionplanner.competitioncategory.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.domain.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@SpringBootTest
class TestCompetitionCategoryRepository(
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val clubRepository: IClubRepository,
    @Autowired val repository: ICompetitionCategoryRepository,
    @Autowired val categoryRepository: ICategoryRepository
) {
    private val dataGenerator = DataGenerator()
    lateinit var club: ClubDTO
    lateinit var competition: CompetitionDTO

    @BeforeEach
    fun saveAClub() {
        val dto = dataGenerator.newClubSpec()
        club = clubRepository.store(dto)
        competition = competitionRepository.store(dataGenerator.newNewCompetitionDTO(organizingClubId = club.id))
    }

    @AfterEach
    fun deleteClub() {
        competitionRepository.delete(competition.id)
        clubRepository.delete(club.id)
    }

    @Test
    fun shouldNotReturnAnyCategories() {
        // Act
        val categories = repository.getAll(competition.id)

        // Assert
        Assertions.assertEquals(0, categories.size)
    }

    @Test
    fun shouldBeAbleToStoreCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == "Herrar 1" }
        val dto = dataGenerator.newCompetitionCategoryDTO(category = category!!)
        val addedCompetitionCategory = repository.store(competition.id, dto)

        // Act
        val categories = repository.getAll(competition.id)
        val actualCategory = categories.find { it.id == addedCompetitionCategory.id }

        // Assert
        Assertions.assertEquals(addedCompetitionCategory, actualCategory)

        // Clean up
        repository.delete(addedCompetitionCategory.id)
    }

    @Test
    fun shouldReturnNewlyStoredCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == "Herrar 1" }
        val dto = dataGenerator.newCompetitionCategoryDTO(category = category!!)

        // Act
        val competitionCategory = repository.store(competition.id, dto)

        // Assert
        Assertions.assertTrue(competitionCategory.id > 0)
        Assertions.assertEquals(dto.category, competitionCategory.category)
        Assertions.assertEquals(dto.settings, competitionCategory.settings)
        Assertions.assertEquals(dto.gameSettings, competitionCategory.gameSettings)

        // Clean up
        repository.delete(competitionCategory.id)
    }

    @Test
    fun shouldThrowExceptionWhenDeletingCompetitionCategoryThatDoesNotExist() {
        Assertions.assertThrows(NotFoundException::class.java) {
            repository.delete(-1)
        }
    }

    @Test
    fun shouldThrowExceptionWhenTryingToUpdateCompetitionCategoryThatDoesNotExist() {
        Assertions.assertThrows(NotFoundException::class.java) {
            repository.update(dataGenerator.newCompetitionCategoryUpdateDTO(id = -1))
        }
    }

    @Test
    fun shouldBeAbleToUpdateCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == "Herrar 1" }
        val dto = dataGenerator.newCompetitionCategoryDTO(category = category!!)
        val original = repository.store(competition.id, dto)
        val updateDto = dataGenerator.newCompetitionCategoryUpdateDTO(
            id = original.id,
            settings = dataGenerator.newGeneralSettingsDTO(cost = 110f, playersToPlayOff = 1),
            gameSettings = dataGenerator.newGameSettingsDTO(numberOfSets = 4, winScore = 8, numberOfSetsFinal = 11)
        )

        // Act
        repository.update(updateDto)
        val updated = repository.getAll(competition.id).first { it.id == original.id }

        // Assert
        Assertions.assertEquals(updateDto.settings, updated.settings)
        Assertions.assertEquals(updateDto.gameSettings, updated.gameSettings)

        // Clean up
        repository.delete(updated.id)
    }


}