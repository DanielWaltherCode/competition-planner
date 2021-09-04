package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.interfaces.IClubRepository
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
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
    @Autowired val competitionCategoryRepository: ICompetitionCategoryRepository
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
        val categories = competitionCategoryRepository.getCompetitionCategoriesIn(competition.id)

        // Assert
        Assertions.assertEquals(0, categories.size)
    }

    @Test
    fun shouldBeAbleToStoreCompetitionCategory() {
        // Setup
        val category = competitionCategoryRepository.getAvailableCategories().find { it.name == "Herrar 1" }
        val dto = dataGenerator.newCompetitionCategoryDTO(category = category!!)
        val addedCompetitionCategory = competitionCategoryRepository.addCompetitionCategoryTo(competition.id, dto)

        // Act
        val categories = competitionCategoryRepository.getCompetitionCategoriesIn(competition.id)
        val actualCategory = categories.find { it.id == addedCompetitionCategory.id }

        // Assert
        Assertions.assertEquals(addedCompetitionCategory, actualCategory)

        // Clean up
        competitionCategoryRepository.deleteCompetitionCategory(addedCompetitionCategory.id)
    }

    @Test
    fun shouldReturnNewlyStoredCompetitionCategory() {
        // Setup
        val category = competitionCategoryRepository.getAvailableCategories().find { it.name == "Herrar 1" }
        val dto = dataGenerator.newCompetitionCategoryDTO(category = category!!)

        // Act
        val competitionCategory = competitionCategoryRepository.addCompetitionCategoryTo(competition.id, dto)

        // Assert
        Assertions.assertTrue(competitionCategory.id > 0)
        Assertions.assertEquals(dto.category, competitionCategory.category)
        Assertions.assertEquals(dto.settings, competitionCategory.settings)
        Assertions.assertEquals(dto.gameSettings, competitionCategory.gameSettings)

        // Clean up
        competitionCategoryRepository.deleteCompetitionCategory(competitionCategory.id)
    }

    @Test
    fun shouldBeAbleToGetDrawType() {
        competitionCategoryRepository.getDrawType("POOL_ONLY")
        competitionCategoryRepository.getDrawType("CUP_ONLY")
        competitionCategoryRepository.getDrawType("POOL_AND_CUP")
    }

    @Test
    fun shouldBeAbleToGetPoolDrawStrategyTypes() {
        competitionCategoryRepository.getPoolDrawStrategy("normal")
        competitionCategoryRepository.getPoolDrawStrategy("snake")
    }

    @Test
    fun shouldGetAllAvailableCategories() {
        val availableCategories = competitionCategoryRepository.getAvailableCategories()

        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 1" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 2" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 3" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 4" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 5" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 6" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 1" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 2" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 3" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 4" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damjuniorer 17" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 15" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 14" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 13" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 12" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 11" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 10" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 9" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 8" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrjuniorer 17" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 15" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 14" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 13" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 12" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 11" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 10" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 9" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 8" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrdubbel" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damdubbel" })
    }
}