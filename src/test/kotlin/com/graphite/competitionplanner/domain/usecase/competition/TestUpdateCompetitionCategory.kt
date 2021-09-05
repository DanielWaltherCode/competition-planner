package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.TestHelper
import com.graphite.competitionplanner.domain.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.interfaces.NotFoundException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestUpdateCompetitionCategory {

    private final val mockedRepository = mock(ICompetitionCategoryRepository::class.java)
    val updateCompetitionCategory = UpdateCompetitionCategory(mockedRepository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldThrowExceptionIfDtoHasInvalidSettings() {
        // Setup
        val dto = dataGenerator.newCompetitionCategoryDTO(
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
        val dto = dataGenerator.newCompetitionCategoryDTO()

        // Act
        updateCompetitionCategory.execute(dto)

        // Assert
        verify(mockedRepository, times(1)).update(dto)
        verify(mockedRepository, times(1)).update(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCatchExceptionFromRepository() {
        // Setup
        val dto = dataGenerator.newCompetitionCategoryDTO()
        `when`(mockedRepository.update(dto)).thenThrow(NotFoundException::class.java)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            updateCompetitionCategory.execute(dto)
        }
    }
}