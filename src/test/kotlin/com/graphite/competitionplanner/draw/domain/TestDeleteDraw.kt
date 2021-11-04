package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.times
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDeleteDraw {

    private val mockedDrawRepository = Mockito.mock(ICompetitionDrawRepository::class.java)
    private val mockedCompetitionCategoryRepository = Mockito.mock(ICompetitionCategoryRepository::class.java)

    private val deleteDraw = DeleteDraw(mockedDrawRepository, mockedCompetitionCategoryRepository)

    @Test
    fun shouldValidateThatCompetitionCategoryExist() {
        // Setup
        val competitionCategoryId = 32211

        // Act
        deleteDraw.execute(competitionCategoryId)

        // Assert
        Mockito.verify(mockedCompetitionCategoryRepository, times(1)).get(competitionCategoryId)
        Mockito.verify(mockedCompetitionCategoryRepository, times(1)).get(anyInt())
    }

    @Test
    fun shouldDelegateToRepository() {
        // Setup
        val competitionCategoryId = 55911

        // Act
        deleteDraw.execute(competitionCategoryId)

        // Assert
        Mockito.verify(mockedDrawRepository, times(1)).delete(competitionCategoryId)
        Mockito.verify(mockedDrawRepository, times(1)).delete(anyInt())
    }
}