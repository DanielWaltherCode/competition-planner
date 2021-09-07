package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.TestHelper
import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.entity.Round
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestAddCompetitionCategory {

    private final val mockedRepository = Mockito.mock(ICompetitionCategoryRepository::class.java)
    val addCompetitionCategory = AddCompetitionCategory(mockedRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldThrowExceptionIfCategoryDoesNotExist() {
        // Setup
        val dto = CategoryDTO(1, "HERRAR-13", "SINGLES") // Does not exist
        val competitionId = 0
        `when`(mockedRepository.getAvailableCategories())
            .thenReturn(
                listOf(
                    dataGenerator.newCategoryDTO(id = 0, name = "DAMER 1"),
                    dataGenerator.newCategoryDTO(id = 1, name = "HERRAR 1"),
                    dataGenerator.newCategoryDTO(id = 2, name = "HERRAR 13")
                )
            )

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            addCompetitionCategory.execute(
                competitionId,
                dto
            )
        }
    }

    @Test
    fun cannotAddSameCategoryTwiceToSameCompetition() {
        // Setup
        val dto = dataGenerator.newCategoryDTO(id = 0, name = "HERRDUBBEL")
        val competitionId = 1
        `when`(mockedRepository.getAvailableCategories()).thenReturn(listOf(dto))
        `when`(mockedRepository.getAll(competitionId))
            .thenReturn(
                listOf(dataGenerator.newCompetitionCategoryDTO(category = dto))
            )

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            addCompetitionCategory.execute(competitionId, dto)
        }
    }

    @Test
    fun shouldCallRepositoryToStoreWithDefaultSettings() {
        // Setup
        val category = dataGenerator.newCategoryDTO(id = 0, name = "HERRDUBBEL")
        val settings = dataGenerator.newGeneralSettingsDTO(cost = 150f, playersPerGroup = 4, playersToPlayOff = 2)
        val gameSettings = dataGenerator.newGameSettingsDTO(
            numberOfSets = 5,
            winScore = 11,
            winMargin = 2,
            differentNumberOfGamesFromRound = Round.UNKNOWN,
            numberOfSetsFinal = 7,
            winScoreFinal = 11,
            winMarginFinal = 2,
            winScoreTiebreak = 2,
            winMarginTieBreak = 2
        )

        val competitionId = 1
        `when`(mockedRepository.getAvailableCategories()).thenReturn(listOf(category))
        `when`(mockedRepository.getAll(competitionId)).thenReturn(emptyList())

        // Act
        addCompetitionCategory.execute(competitionId, category)

        // Assert
        val expected = CompetitionCategoryDTO(0, category, settings, gameSettings)
        verify(mockedRepository, Mockito.times(1)).store(competitionId, expected)
        verify(mockedRepository, Mockito.times(1)).store(
            anyInt(),
            TestHelper.MockitoHelper.anyObject()
        )
    }
}