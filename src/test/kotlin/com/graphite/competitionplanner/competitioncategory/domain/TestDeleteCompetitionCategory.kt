package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
class TestDeleteCompetitionCategory {

    private val mockedCompetitionCategoryRepository = mock(ICompetitionCategoryRepository::class.java)
    private val mockedRegistrationRepository = mock(IRegistrationRepository::class.java)
    val deleteCompetitionCategory = DeleteCompetitionCategory(
        mockedCompetitionCategoryRepository,
        mockedRegistrationRepository)

    private val dataGenerator = DataGenerator()

    @Test
    fun shouldCallRepositoryToDeleteOnce() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()

        // Act
        deleteCompetitionCategory.execute(competitionCategory.id)

        // Assert
        verify(mockedCompetitionCategoryRepository, times(1)).delete(competitionCategory.id)
        verify(mockedCompetitionCategoryRepository, times(1)).delete(anyInt())
    }

    @Test
    fun repositoryExceptionShouldPropagate() {
        val competitionCategoryId = 1337
        `when`(mockedCompetitionCategoryRepository.delete(competitionCategoryId)).thenThrow(NotFoundException::class.java)

        Assertions.assertThrows(NotFoundException::class.java) {
            deleteCompetitionCategory.execute(competitionCategoryId)
        }
    }

    @Test
    fun shouldThrowExceptionIfCategoryHasRegisteredPlayers() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        val registration = dataGenerator.newRegistrationDTO()
        `when`(mockedRegistrationRepository.getRegistrationsIn(competitionCategory.id)).thenReturn(listOf(registration))

        // Act & Assert
        Assertions.assertThrows(ResponseStatusException::class.java) {
            deleteCompetitionCategory.execute(competitionCategory.id)
        }
    }
}