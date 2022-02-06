package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawPoolPlayOffTreeSize: TestBaseCreateDraw() {

    @Test
    fun whenAllGroupsAreFull() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = (1..8).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(3, result.matches.size, "Expected in total 3 playoff matches.")

        val semifinals = result.matches.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinals.size)

        val finals = result.matches.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finals.size)
    }

    @Test
    fun whenGroupsAreNotFull() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = (1..6).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(3, result.matches.size, "Expected in total 3 playoff matches.")

        val semifinals = result.matches.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinals.size)

        val finals = result.matches.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finals.size)
    }

    @Test
    fun whenThreeAdvanceToPlayOff() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 3,
                playersToPlayOff = 1
            )
        )
        val registrationRanks = (1..9).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(3, result.matches.size, "Expected in total 3 playoff matches.")

        val semifinals = result.matches.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinals.size)

        val finals = result.matches.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finals.size)
    }

    @Test
    fun whenSevenAdvanceToPlayOff() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4,
                playersToPlayOff = 1
            )
        )
        val registrationRanks = (1..28).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(7, result.matches.size, "Expected in total 7 playoff matches.")

        val quarterFinals = result.matches.filter { it.round == Round.QUARTER_FINAL }
        Assertions.assertEquals(4, quarterFinals.size)

        val semifinals = result.matches.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinals.size)

        val finals = result.matches.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finals.size)
    }
}