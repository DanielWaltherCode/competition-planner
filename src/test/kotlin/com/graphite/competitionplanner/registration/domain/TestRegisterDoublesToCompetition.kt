package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.PlayerAlreadyRegisteredException
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Description

@SpringBootTest
class TestRegisterDoublesToCompetition {

    private val findPlayer = Mockito.mock(FindPlayer::class.java)
    private val findCompetitionCategory = Mockito.mock(FindCompetitionCategory::class.java)
    private val repository = Mockito.mock(IRegistrationRepository::class.java)
    private val registerDoubles = RegisterDoubleToCompetition(findPlayer, findCompetitionCategory, repository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldCheckThatPlayerExists() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec(playerOneId = 88, playerTwoId = 99)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES.name)))

        // Act
        registerDoubles.execute(spec)

        // Assert
        Mockito.verify(findPlayer, Mockito.times(1)).byId(spec.playerOneId)
        Mockito.verify(findPlayer, Mockito.times(1)).byId(spec.playerTwoId)
        Mockito.verify(findPlayer, Mockito.times(2)).byId(Mockito.anyInt())
    }

    @Test
    fun shouldThrowExceptionIfCategoryIsNotOfTypeDoubles() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec(playerOneId = 88, playerTwoId = 99)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.SINGLES.name)))

        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            registerDoubles.execute(spec)
        }
    }

    @Test
    fun shouldNotThrowExceptionIfCategoryIsOfTypeDoubles() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec(playerOneId = 88, playerTwoId = 99)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES.name)))

        // Act & Assert
        Assertions.assertDoesNotThrow {
            registerDoubles.execute(spec)
        }
    }

    @Test
    fun shouldCheckThatCompetitionCategoryExist() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec(competitionCategoryId = 333)
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES.name)))

        // Act
        registerDoubles.execute(spec)

        // Assert
        Mockito.verify(findCompetitionCategory, Mockito.times(1)).byId(spec.competitionCategoryId)
        Mockito.verify(findCompetitionCategory, Mockito.times(1)).byId(Mockito.anyInt())
    }

    @Test
    fun shouldNotCallRepositoryIfPlayerOneCanNotBeFound() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec(playerOneId = 88, playerTwoId = 99)
        `when`(findPlayer.byId(spec.playerOneId)).thenThrow(NotFoundException::class.java)

        // Act
        Assertions.assertThrows(NotFoundException::class.java) {
            registerDoubles.execute(spec)
        }

        // Assert
        Mockito.verify(repository, Mockito.never()).storeDoubles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCallRepositoryIfPlayerTwoCanNotBeFound() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec(playerOneId = 88, playerTwoId = 99)
        `when`(findPlayer.byId(spec.playerTwoId)).thenThrow(NotFoundException::class.java)

        // Act
        Assertions.assertThrows(NotFoundException::class.java) {
            registerDoubles.execute(spec)
        }

        // Assert
        Mockito.verify(repository, Mockito.never()).storeDoubles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldNotCallRepositoryIfCompetitionCategoryCannotBeFound() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec()
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenThrow(NotFoundException::class.java)

        // Act & Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            registerDoubles.execute(spec)
        }

        Mockito.verify(repository, Mockito.never()).storeDoubles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun shouldCallRepository() {
        // Setup
        val spec = dataGenerator.newRegistrationDoublesSpec()
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES.name)))

        // Act
        registerDoubles.execute(spec)

        // Assert
        Mockito.verify(repository, Mockito.times(1)).storeDoubles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    @Description("If any of the player are already registered to the competition category then abort registration.")
    fun shouldNotRegisterIfPlayerOneAlreadyRegistered() {
        // Setup
        val playerOne = dataGenerator.newPlayerWithClubDTO()
        val playerTwo = dataGenerator.newPlayerWithClubDTO()
        val spec = dataGenerator.newRegistrationDoublesSpec(playerOneId = playerOne.id, playerTwoId = playerTwo.id)
        `when`(findPlayer.byId(playerOne.id)).thenReturn(playerOne)
        `when`(findPlayer.byId(playerTwo.id)).thenReturn(playerTwo)
        `when`(repository.getAllPlayerIdsRegisteredTo(spec.competitionCategoryId)).thenReturn(listOf(playerOne.id))
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES.name)))

        // Act
        Assertions.assertThrows(PlayerAlreadyRegisteredException::class.java) {
            registerDoubles.execute(spec)
        }

        // Assert
        Mockito.verify(repository, Mockito.never()).storeDoubles(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    @Description("If any of the player are already registered to the competition category then abort registration.")
    fun shouldNotRegisterIfPlayerTwoAlreadyRegistered() {
        // Setup
        val playerOne = dataGenerator.newPlayerWithClubDTO()
        val playerTwo = dataGenerator.newPlayerWithClubDTO()
        val spec = dataGenerator.newRegistrationDoublesSpec(playerOneId = playerOne.id, playerTwoId = playerTwo.id)
        `when`(findPlayer.byId(playerOne.id)).thenReturn(playerOne)
        `when`(findPlayer.byId(playerTwo.id)).thenReturn(playerTwo)
        `when`(repository.getAllPlayerIdsRegisteredTo(spec.competitionCategoryId)).thenReturn(listOf(playerTwo.id))
        `when`(findCompetitionCategory.byId(spec.competitionCategoryId)).thenReturn(
            dataGenerator.newCompetitionCategoryDTO(
                category = dataGenerator.newCategorySpec(type = CategoryType.DOUBLES.name)))

        // Act
        Assertions.assertThrows(PlayerAlreadyRegisteredException::class.java) {
            registerDoubles.execute(spec)
        }

        // Assert
        Mockito.verify(repository, Mockito.never()).storeDoubles(TestHelper.MockitoHelper.anyObject())
    }
}