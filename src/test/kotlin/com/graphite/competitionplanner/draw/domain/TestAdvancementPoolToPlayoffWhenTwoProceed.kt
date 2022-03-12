package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.NotEnoughRegistrationsException
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestAdvancementPoolToPlayoffWhenTwoProceed : TestAdvancementPoolToPlayoff() {

    private val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
        id = 35,
        settings = dataGenerator.newGeneralSettingsDTO(
            drawType = DrawType.POOL_AND_CUP,
            playersPerGroup = 4,
            playersToPlayOff = 2
        )
    )

    @Test
    fun whenThereAreTooFewThatAdvanceToPlayoff() {
        // Setup
        val registrationRanks = (1..1).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act & Assert
        Assertions.assertThrows(NotEnoughRegistrationsException::class.java) {
            createDraw.execute(competitionCategory.id)
        }
    }

    @Test
    fun whenThereAreOnePool() {
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
        val final = result.matches.inRound(Round.FINAL)
        val matchUps = final.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        matchUps.assertMatchUpExist(Pair("A1", "A2"))
    }

    @Test
    fun whenThereAreTwoPools() {
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

        val semiFinalExpectedNames = listOf("A1", "A2", "B1", "B2").sorted()
        result.matches.assertNamesInRoundEqual(Round.SEMI_FINAL, semiFinalExpectedNames)

        val semiFinals = result.matches.inRound(Round.SEMI_FINAL)
        val matchUps = semiFinals.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        matchUps.assertMatchUpExist(Pair("A1", "B2"))
        matchUps.assertMatchUpExist(Pair("A2", "B1"))
    }

    @Test
    fun whenThereAreThreePools() {
        // Setup
        val registrationRanks = (1..12).toList().map {
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

        val quarterFinalExpectedNames = listOf("A1", "A2", "B1", "B2", "C1", "C2", "BYE", "BYE").sorted()
        result.matches.assertNamesInRoundEqual(Round.QUARTER_FINAL, quarterFinalExpectedNames)

        val quarterFinals = result.matches.inRound(Round.QUARTER_FINAL)
        val matchUps = quarterFinals.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        matchUps.assertMatchUpExist(Pair("A1", "BYE"))
        matchUps.assertMatchUpExist(Pair("BYE", "B1"))
        matchUps.assertMatchUpExist(Pair("C1", "B2"))
        matchUps.assertMatchUpExist(Pair("C2", "A2"))

        Assertions.assertTrue(quarterFinals.findMatchWith("A1").order <= 2)
        Assertions.assertTrue(quarterFinals.findMatchWith("B1").order > 2)
    }
}