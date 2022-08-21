package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.PlayerAlreadyRegisteredException
import com.graphite.competitionplanner.registration.interfaces.PlayerRegistrationStatus
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Description

@SpringBootTest
class TestRegisterPlayerToCompetition {

    private val findPlayer = mock(FindPlayer::class.java)
    private val findCompetitionCategory = mock(FindCompetitionCategory::class.java)
    private val repository = mock(IRegistrationRepository::class.java)
    private val registerPlayer = RegisterPlayerToCompetition(findPlayer, findCompetitionCategory, repository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCheckThatPlayerExist() {
        // Setup
        val spec = dataGenerator.newRegistrationSinglesSpec(playerId = 10)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES)
            )
        )

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
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES)
            )
        )

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
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES)
            )
        )

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
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES)
            )
        )

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
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES)
            )
        )

        // Act
        registerPlayer.execute(spec)

        // Assert
        verify(repository, times(1)).storeSingles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    @Description(
        "When a user tries to register itself to the same competition category we" +
                " throw error that player is already registered"
    )
    fun shouldNotRegisterIfPlayerAlreadyRegistered() {
        // Setup
        val player = dataGenerator.newPlayerWithClubDTO()
        val existingRegistration =
            dataGenerator.newRegistrationSinglesDTO(playerId = player.id, status = PlayerRegistrationStatus.PLAYING)
        val spec = dataGenerator.newRegistrationSinglesSpec(playerId = player.id)
        `when`(findPlayer.byId(player.id)).thenReturn(player)
        `when`(repository.getAllSingleRegistrations(spec.competitionCategoryId)).thenReturn(listOf(existingRegistration))
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES)
            )
        )

        // Act
        Assertions.assertThrows(PlayerAlreadyRegisteredException::class.java) {
            registerPlayer.execute(spec)
        }

        // Assert
        verify(repository, never()).storeSingles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    @Description(
        "When a user that has withdrawn from the tournament and then re-register itself again, the " +
                "status of the original registration is updated."
    )
    fun shouldChangeStatusToPlayingIfRegistrationWasPreviouslyWithdrawn() {
        // Setup
        val player = dataGenerator.newPlayerWithClubDTO()
        val existingRegistration =
            dataGenerator.newRegistrationSinglesDTO(playerId = player.id, status = PlayerRegistrationStatus.WITHDRAWN)
        val spec = dataGenerator.newRegistrationSinglesSpec(playerId = player.id)
        `when`(findPlayer.byId(player.id)).thenReturn(player)
        `when`(repository.getAllSingleRegistrations(spec.competitionCategoryId)).thenReturn(listOf(existingRegistration))
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES)
            )
        )
        val expectedRegistrationAfterUpdate = RegistrationSinglesDTO(
            existingRegistration.id,
            existingRegistration.playerId,
            existingRegistration.competitionCategoryId,
            existingRegistration.registrationDate,
            PlayerRegistrationStatus.PLAYING
        )
        `when`(repository.getRegistrationFor(spec)).thenReturn(expectedRegistrationAfterUpdate)

        // Act
        val updated = registerPlayer.execute(spec)

        // Assert
        Assertions.assertEquals(expectedRegistrationAfterUpdate, updated)
        verify(repository, never()).storeSingles(TestHelper.MockitoHelper.anyObject())
        verify(repository, times(1)).updatePlayerRegistrationStatus(
            existingRegistration.id,
            PlayerRegistrationStatus.PLAYING
        )
        verify(repository, times(1)).updatePlayerRegistrationStatus(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

}