package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.ICompetitionCategoryRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDeleteCompetitionCategory {

    private final val mockedRepository = mock(ICompetitionCategoryRepository::class.java)
    val deleteCompetitionCategory = DeleteCompetitionCategory(mockedRepository)

    @Test
    fun shouldCallRepositoryToDeleteOnce() {
        val competitionCategoryId = 13
        deleteCompetitionCategory.execute(competitionCategoryId)

        verify(mockedRepository, times(1)).delete(competitionCategoryId)
        verify(mockedRepository, times(1)).delete(anyInt())
    }

    @Test
    fun repositoryExceptionShouldPropagate() {
        val competitionCategoryId = 1337
        `when`(mockedRepository.delete(competitionCategoryId)).thenThrow(NotFoundException::class.java)

        Assertions.assertThrows(NotFoundException::class.java) {
            deleteCompetitionCategory.execute(competitionCategoryId)
        }
    }
}