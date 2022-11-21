package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDeleteDraw {

    private val mockedDrawRepository = mock(ICompetitionDrawRepository::class.java)
    private val mockedCompetitionCategoryRepository = mock(ICompetitionCategoryRepository::class.java)

    private val deleteDraw = DeleteDraw(mockedDrawRepository, mockedCompetitionCategoryRepository)

    @Test
    fun shouldValidateThatCompetitionCategoryExist() {
        // Setup
        val competitionCategoryId = 32211

        // Act
        deleteDraw.execute(competitionCategoryId)

        // Assert
        verify(mockedCompetitionCategoryRepository, times(1)).get(competitionCategoryId)
        verify(mockedCompetitionCategoryRepository, times(1)).get(anyInt())
    }
}