package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Description

@SpringBootTest
class TestRegisterPlayerToCompetition {

    private final val findPlayer = mock(FindPlayer::class.java)
    private final val findCompetitionCategory = mock(FindCompetitionCategory::class.java)
    private final val repository = mock(IRegistrationRepository::class.java)
    private val registerPlayer = RegisterPlayerToCompetition(findPlayer, findCompetitionCategory, repository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCheckThatPlayerExist() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(playerId = 10)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES.name)))

        // Act
        registerPlayer.execute(spec)

        // Assert
        verify(findPlayer, times(1)).byId(spec.playerId)
        verify(findPlayer, times(1)).byId(anyInt())
    }

    @Test
    fun shouldThrowExceptionIfCategoryIsNotOfTypeSingles() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(competitionCategoryId = 333)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES.name)))

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            registerPlayer.execute(spec)
        }
    }

    @Test
    fun shouldNotThrowExceptionIfCategoryIsOfTypeSingles() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(competitionCategoryId = 333)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES.name)))

        // Act & Assert
        Assertions.assertDoesNotThrow {
            registerPlayer.execute(spec)
        }
    }

    @Test
    fun shouldCheckThatCompetitionCategoryExist() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(competitionCategoryId = 333)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES.name)))

        // Act
        registerPlayer.execute(spec)

        // Assert
        verify(findCompetitionCategory, times(1)).byId(spec.competitionCategoryId)
        verify(findCompetitionCategory, times(1)).byId(anyInt())
    }

    @Test
    fun shouldNotCallRepositoryIfPlayerCannotBeFound() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(playerId = 4444)
        `when`(findPlayer.byId(spec.playerId)).thenThrow(NotFoundException::class.java)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            registerPlayer.execute(spec)
        }

        verify(repository, never()).storeSingles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCallRepositoryIfCompetitionCategoryCannotBeFound() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(playerId = 4444)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenThrow(NotFoundException::class.java)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            registerPlayer.execute(spec)
        }

        verify(repository, never()).storeSingles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallRepository() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec()
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES.name)))

        // Act
        registerPlayer.execute(spec)

        // Assert
        verify(repository, times(1)).storeSingles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    @Description("When a user tries to register itself to the same competition category, we instead return the" +
            "already existing registration.")
    fun shouldNotRegisterIfPlayerAlreadyRegistered() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(playerId = 53)
        `when`(repository.getAllPlayerIdsRegisteredTo(spec.competitionCategoryId)).thenReturn(listOf(spec.playerId))
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES.name)))

        // Act
        registerPlayer.execute(spec)

        // Assert
        verify(repository, never()).storeSingles(TestHelper.MockitoHelper.anyObject())
        verify(repository, times(1)).getRegistrationFor(spec)
    }

}