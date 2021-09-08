package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.interfaces.ICategoryRepository
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import org.junit.jupiter.api.*
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
        val dto = dataGenerator.newClubDTO()
        club = clubRepository.store(dto)
        competition = competitionRepository.store(dataGenerator.newNewCompetitionDTO(organizingClubId = club.id))
    }

    @AfterEach
    fun deleteClub() {
        competitionRepository.delete(competition.id)
        clubRepository.delete(club)
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
            repository.update(dataGenerator.newCompetitionCategoryDTO(id = -1))
        }
    }

    @Test
    fun shouldBeAbleToUpdateCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == "Herrar 1" }
        val dto = dataGenerator.newCompetitionCategoryDTO(category = category!!)
        val original = repository.store(competition.id, dto)
        val updateDto = dataGenerator.newCompetitionCategoryDTO(
            id = original.id,
            settings = dataGenerator.newGeneralSettingsDTO(cost = 110f, playersToPlayOff = 1),
            gameSettings = dataGenerator.newGameSettingsDTO(numberOfSets = 4, winScore = 8, numberOfSetsFinal = 11)
        )

        // Act
        repository.update(updateDto)
        val updated = repository.getAll(competition.id).filter { it.id == original.id }.first()

        // Assert
        Assertions.assertEquals(updateDto.settings, updated.settings)
        Assertions.assertEquals(updateDto.gameSettings, updated.gameSettings)

        // Clean up
        repository.delete(updated.id)
    }


}