package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestFindCompetitionCategory {

    private final val repository = mock(ICompetitionCategoryRepository::class.java)
    private val findCompetitionCategory = FindCompetitionCategory(repository)

    @Test
    fun shouldCallRepository() {
        // Setup
        val competitionCategoryId = 10

        // Act
        findCompetitionCategory.byId(competitionCategoryId)

        // Assert
        verify(repository, times(1)).get(10)
        verify(repository, times(1)).get(anyInt())
    }

    @Test
    fun shouldNotCatchNotFoundException() {
        // Setup
        val competitionCategoryId = 334
        `when`(repository.get(competitionCategoryId)).thenThrow(NotFoundException::class.java)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            findCompetitionCategory.byId(competitionCategoryId)
        }
    }
}