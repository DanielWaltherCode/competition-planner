package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategorySpec
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.schedule.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestAddCompetitionCategory {

    private val mockedRepository = mock(ICompetitionCategoryRepository::class.java)
    private val mockedCategoryRepository = mock(ICategoryRepository::class.java)
    private val mockedScheduleService = mock(ScheduleService::class.java)
    val addCompetitionCategory = AddCompetitionCategory(
        mockedRepository,
        mockedCategoryRepository,
        mockedScheduleService
    )

    val dataGenerator = DataGenerator()

    @Test
    fun shouldThrowExceptionIfCategoryDoesNotExist() {
        // Setup
        val spec = dataGenerator.newCategorySpec(1, "HERRAR-13", "SINGLES") // Does not exist
        val competitionId = 0
        `when`(mockedCategoryRepository.getAvailableCategories())
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
                spec
            )
        }
    }

    @Test
    fun cannotAddSameCategoryTwiceToSameCompetition() {
        // Setup
        val spec = dataGenerator.newCategorySpec(id = 0, name = "HERRDUBBEL")
        val competitionId = 1
        `when`(mockedCategoryRepository.getAvailableCategories()).thenReturn(
            listOf(
                CategoryDTO(
                    spec.id,
                    spec.name,
                    spec.type
                )
            )
        )
        `when`(mockedRepository.getAll(competitionId))
            .thenReturn(
                listOf(dataGenerator.newCompetitionCategoryDTO(category = spec))
            )

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            addCompetitionCategory.execute(competitionId, spec)
        }
    }

    @Test
    fun shouldCallRepositoryToStoreWithDefaultSettings() {
        // Setup
        val spec = dataGenerator.newCategorySpec(id = 0, name = "HERRDUBBEL")
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        val settings = dataGenerator.newGeneralSettingsSpec(
            cost = 150f,
            playersPerGroup = 4,
            playersToPlayOff = 2,
            drawType = DrawType.POOL_AND_CUP
        )
        val gameSettings = dataGenerator.newGameSettingsSpec(
            numberOfSets = 5,
            winScore = 11,
            winMargin = 2,
            differentNumberOfGamesFromRound = Round.UNKNOWN,
            numberOfSetsFinal = 7,
            winScoreFinal = 11,
            winMarginFinal = 2,
            tiebreakInFinalGame = false,
            winScoreTiebreak = 7,
            winMarginTieBreak = 2,
            useDifferentRulesInEndGame = false
        )

        val competitionId = 1
        `when`(mockedCategoryRepository.getAvailableCategories()).thenReturn(
            listOf(
                CategoryDTO(
                    spec.id,
                    spec.name,
                    spec.type
                )
            )
        )
        `when`(mockedRepository.getAll(competitionId)).thenReturn(emptyList())
        `when`(mockedRepository.store(eq(competitionId), TestHelper.MockitoHelper.anyObject())).thenReturn(
            competitionCategory
        )

        // Act
        addCompetitionCategory.execute(competitionId, spec)

        // Assert
        val expected =
            CompetitionCategorySpec(
                CompetitionCategoryStatus.ACTIVE,
                CategorySpec(spec.id, spec.name, spec.type),
                settings,
                gameSettings
            )
        verify(mockedRepository, times(1)).store(competitionId, expected)
        verify(mockedRepository, times(1)).store(
            anyInt(),
            TestHelper.MockitoHelper.anyObject()
        )
    }

    @Test
    fun shouldDelegateToScheduleServiceToAddCategoryStartTime() {
        // Setup
        val spec = dataGenerator.newCategorySpec(id = 0, name = "HERRDUBBEL")
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()

        val competitionId = 1
        `when`(mockedCategoryRepository.getAvailableCategories()).thenReturn(
            listOf(
                CategoryDTO(
                    spec.id,
                    spec.name,
                    spec.type
                )
            )
        )
        `when`(mockedRepository.getAll(competitionId)).thenReturn(emptyList())
        `when`(mockedRepository.store(eq(competitionId), TestHelper.MockitoHelper.anyObject())).thenReturn(
            competitionCategory
        )

        // Act
        addCompetitionCategory.execute(competitionId, spec)

        // Assert
        verify(mockedScheduleService, times(1)).addCategoryStartTime(
            competitionCategory.id,
            CategoryStartTimeSpec(null, null, null)
        )
    }
}