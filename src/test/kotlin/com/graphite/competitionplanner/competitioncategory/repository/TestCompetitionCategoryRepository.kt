package com.graphite.competitionplanner.competitioncategory.repository

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.util.DataGenerator
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
        competition = competitionRepository.store(dataGenerator.newCompetitionSpec(organizingClubId = club.id))
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
        val category = categoryRepository.getAvailableCategories().find { it.name == DefaultCategory.MEN_1.name }!!
        val dto =
            dataGenerator.newCompetitionCategorySpec(category = CategorySpec(category.id, category.name, category.type))
        val addedCompetitionCategory = repository.store(competition.id, dto)

        // Act
        val categories = repository.getAll(competition.id)
        val actualCategory = categories.find { it.id == addedCompetitionCategory.id }

        // Assert
        Assertions.assertEquals(addedCompetitionCategory, actualCategory)
    }

    @Test
    fun shouldReturnNewlyStoredCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == DefaultCategory.MEN_1.name }!!
        val spec =
            dataGenerator.newCompetitionCategorySpec(category = CategorySpec(category.id, category.name, category.type))

        // Act
        val competitionCategory = repository.store(competition.id, spec)

        // Assert
        Assertions.assertTrue(competitionCategory.id > 0)
        Assertions.assertEquals(spec.category, competitionCategory.category)
        Assertions.assertEquals(spec.settings, competitionCategory.settings)
        Assertions.assertEquals(spec.gameSettings, competitionCategory.gameSettings)
    }

    @Test
    fun shouldNotBeAbleToFindDeletedCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == DefaultCategory.MEN_1.name }!!
        val spec =
            dataGenerator.newCompetitionCategorySpec(category = CategorySpec(category.id, category.name, category.type))
        val competitionCategory = repository.store(competition.id, spec)

        // Act
        repository.delete(competitionCategory.id)

        // Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            repository.get(competitionCategory.id)
        }
    }

    @Test
    fun shouldThrowExceptionWhenTryingToUpdateCompetitionCategoryThatDoesNotExist() {
        Assertions.assertThrows(NotFoundException::class.java) {
            repository.update(-1, dataGenerator.newCompetitionCategoryUpdateSpec())
        }
    }

    @Test
    fun shouldBeAbleToUpdateCompetitionCategory() {
        // Setup
        val category = categoryRepository.getAvailableCategories().find { it.name == DefaultCategory.MEN_1.name }!!
        val spec =
            dataGenerator.newCompetitionCategorySpec(
                category = CategorySpec(category.id, category.name, category.type),
                gameSettings = dataGenerator.newGameSettingsDTO(useDifferentRulesInEndGame = false)
            )
        val original = repository.store(competition.id, spec)
        val updateSpec = dataGenerator.newCompetitionCategoryUpdateSpec(
            settings = dataGenerator.newGeneralSettingsDTO(cost = 110f, playersToPlayOff = 1),
            gameSettings = dataGenerator.newGameSettingsDTO(
                numberOfSets = 7, winScore = 8, numberOfSetsFinal = 11, useDifferentRulesInEndGame = true
            )
        )

        // Act
        repository.update(original.id, updateSpec)
        val updated = repository.getAll(competition.id).first { it.id == original.id }

        // Assert
        Assertions.assertEquals(
            updateSpec.settings.cost,
            updated.settings.cost,
            "Cost wast not updated."
        )
        Assertions.assertEquals(
            updateSpec.settings.drawType,
            updated.settings.drawType,
            "Draw type was not updated."
        )
        Assertions.assertEquals(
            updateSpec.settings.playersPerGroup,
            updated.settings.playersPerGroup,
            "Players per group was not updated."
        )
        Assertions.assertEquals(
            updateSpec.settings.playersToPlayOff,
            updated.settings.playersToPlayOff,
            "Players to playoff was not updated."
        )
        Assertions.assertEquals(
            updateSpec.settings.poolDrawStrategy,
            updated.settings.poolDrawStrategy,
            "Pooldraw strategry was not updated."
        )

        Assertions.assertEquals(
            updateSpec.gameSettings.numberOfSets,
            updated.gameSettings.numberOfSets,
            "Number of sets was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.winScore,
            updated.gameSettings.winScore,
            "Win score was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.winMargin,
            updated.gameSettings.winMargin,
            "Win margin was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.differentNumberOfGamesFromRound,
            updated.gameSettings.differentNumberOfGamesFromRound,
            "Different number of games from round was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.numberOfSetsFinal,
            updated.gameSettings.numberOfSetsFinal,
            "Number of sets in final was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.winScoreFinal,
            updated.gameSettings.winScoreFinal,
            "Win score in final was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.winMarginFinal,
            updated.gameSettings.winMarginFinal,
            "Win margin in final was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.tiebreakInFinalGame,
            updated.gameSettings.tiebreakInFinalGame,
            "Tie break in final game was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.winScoreTiebreak,
            updated.gameSettings.winScoreTiebreak,
            "Win score in tie break was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.winMarginTieBreak,
            updated.gameSettings.winMarginTieBreak,
            "Win margin in tie break was not updated."
        )
        Assertions.assertEquals(
            updateSpec.gameSettings.useDifferentRulesInEndGame,
            updated.gameSettings.useDifferentRulesInEndGame,
            "Use of different rules in end game was not updated"
        )
    }
}