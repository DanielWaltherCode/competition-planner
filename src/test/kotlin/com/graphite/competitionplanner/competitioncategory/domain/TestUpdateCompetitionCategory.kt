package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
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
    fun shouldCallRepositoryUpdateOnce() {
        // Setup
        val competitionCategoryId = 10
        val spec = dataGenerator.newCompetitionCategoryUpdateSpec()

        // Act
        updateCompetitionCategory.execute(competitionCategoryId, spec)

        // Assert
        verify(mockedRepository, times(1)).update(competitionCategoryId, spec)
        verify(mockedRepository, times(1)).update(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCatchExceptionFromRepository() {
        // Setup
        val competitionCategoryId = 10
        val spec = dataGenerator.newCompetitionCategoryUpdateSpec()
        `when`(mockedRepository.update(competitionCategoryId, spec)).thenThrow(NotFoundException::class.java)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            updateCompetitionCategory.execute(competitionCategoryId, spec)
        }
    }
}