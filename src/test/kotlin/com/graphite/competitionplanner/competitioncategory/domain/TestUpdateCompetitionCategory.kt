package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestUpdateCompetitionCategory {

    private final val mockedRepository = mock(ICompetitionCategoryRepository::class.java)
    val updateCompetitionCategory = UpdateCompetitionCategory(mockedRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldThrowExceptionIfDtoHasInvalidSettings() {
        // Setup
        val dto = dataGenerator.newCompetitionCategoryUpdateDTO(
            settings = dataGenerator.newGeneralSettingsDTO(
                playersPerGroup = 2,
                playersToPlayOff = 3
            )
        )

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            updateCompetitionCategory.execute(dto)
        }

        verify(mockedRepository, never()).update(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallRepositoryUpdateOnce() {
        // Setup
        val dto = dataGenerator.newCompetitionCategoryUpdateDTO()

        // Act
        updateCompetitionCategory.execute(dto)

        // Assert
        verify(mockedRepository, times(1)).update(dto)
        verify(mockedRepository, times(1)).update(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCatchExceptionFromRepository() {
        // Setup
        val dto = dataGenerator.newCompetitionCategoryUpdateDTO()
        `when`(mockedRepository.update(dto)).thenThrow(NotFoundException::class.java)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            updateCompetitionCategory.execute(dto)
        }
    }
}