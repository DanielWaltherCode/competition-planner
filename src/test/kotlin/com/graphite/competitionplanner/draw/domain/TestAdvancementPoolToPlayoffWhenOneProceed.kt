package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.NotEnoughRegistrationsException
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestAdvancementPoolToPlayoffWhenOneProceed : TestAdvancementPoolToPlayoff() {

    private val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
        id = 35,
        settings = dataGenerator.newGeneralSettingsSpec(
            drawType = DrawType.POOL_AND_CUP,
            playersPerGroup = 2,
            playersToPlayOff = 1
        )
    )

    @Test
    fun whenThereAreOnePool() {
        // Setup
        val registrationRanks = (1..2).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act & Assert
        Assertions.assertThrows(NotEnoughRegistrationsException::class.java) {
            createDraw.execute(competitionCategory.id) // There are too few that advance to playoff
        }
    }

    @Test
    fun whenThereAreTwoPools() {
        // Setup
        val registrationRanks = (1..4).toList().map {
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
        val finalExpectedName = listOf("A1", "B1")
        result.matches.assertNamesInRoundEqual(Round.FINAL, finalExpectedName)

        val final = result.matches.inRound(Round.FINAL)
        val matchUps = final.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        matchUps.assertMatchUpExist(Pair("A1", "B1"))
    }

    @Test
    fun whenThereAreThreePools() {
        // Setup
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
        val finalExpectedName = listOf("Placeholder", "Placeholder")
        result.matches.assertNamesInRoundEqual(Round.FINAL, finalExpectedName)

        val semiFinalExpectedNames = listOf("A1", "B1", "C1", "BYE")
        result.matches.assertNamesInRoundEqual(Round.SEMI_FINAL, semiFinalExpectedNames)

        val semiFinals = result.matches.inRound(Round.SEMI_FINAL)
        val matchUps = semiFinals.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        matchUps.assertMatchUpExist(Pair("A1", "BYE"))
        matchUps.assertMatchUpExist(Pair("B1", "C1"))
    }

    @Test
    fun whenThereAreFourPools() {
        // Setup
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
        val finalExpectedName = listOf("Placeholder", "Placeholder")
        result.matches.assertNamesInRoundEqual(Round.FINAL, finalExpectedName)

        val semiFinalExpectedNames = listOf("A1", "B1", "C1", "D1")
        result.matches.assertNamesInRoundEqual(Round.SEMI_FINAL, semiFinalExpectedNames)

        val semiFinals = result.matches.inRound(Round.SEMI_FINAL)
        val matchUps = semiFinals.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        matchUps.assertMatchUpExist(Pair("A1", "D1"))
        matchUps.assertMatchUpExist(Pair("B1", "C1"))
    }

    @Test
    fun whenThereAreEightPools() {
        // Setup
        val registrationRanks = (1..16).toList().map {
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
        val finalExpectedName = listOf("Placeholder", "Placeholder")
        result.matches.assertNamesInRoundEqual(Round.FINAL, finalExpectedName)

        val semiFinalExpectedNames = listOf("Placeholder", "Placeholder", "Placeholder", "Placeholder")
        result.matches.assertNamesInRoundEqual(Round.SEMI_FINAL, semiFinalExpectedNames)

        val quarterFinalExpectedNames = listOf("A1", "B1", "C1", "D1", "E1", "F1", "G1", "H1")
        result.matches.assertNamesInRoundEqual(Round.QUARTER_FINAL, quarterFinalExpectedNames)

        val quarterFinals = result.matches.inRound(Round.QUARTER_FINAL)
        val matchUps = quarterFinals.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        matchUps.assertMatchUpExist(Pair("A1", "H1"))
        matchUps.assertMatchUpExist(Pair("B1", "G1"))
        matchUps.assertMatchUpExist(Pair("C1", "E1"))
        matchUps.assertMatchUpExist(Pair("D1", "F1"))
    }
}