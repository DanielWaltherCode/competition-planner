package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.TestHelper
import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
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
        val dto = CategoryDTO(1, "HERRAR-13") // Does not exist
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
        `when`(mockedRepository.getCompetitionCategoriesIn(competitionId))
            .thenReturn(
                listOf(dataGenerator.newCompetitionCategoryDTO(category = dto))
            )

        // Act
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            addCompetitionCategory.execute(competitionId, dto)
        }
    }

    @Test
    fun shouldCallRepositoryToStore() {
        // Setup
        val dto = dataGenerator.newCategoryDTO(id = 0, name = "HERRDUBBEL")
        val competitionId = 1
        `when`(mockedRepository.getAvailableCategories()).thenReturn(listOf(dto))
        `when`(mockedRepository.getCompetitionCategoriesIn(competitionId)).thenReturn(emptyList())

        // Act
        addCompetitionCategory.execute(competitionId, dto)

        // Assert
        verify(mockedRepository, Mockito.times(1)).addCompetitionCategoryTo(
            anyInt(),
            TestHelper.MockitoHelper.anyObject()
        )
        verify(mockedRepository, Mockito.times(1)).addCompetitionCategoryTo(competitionId, dto)
    }
}