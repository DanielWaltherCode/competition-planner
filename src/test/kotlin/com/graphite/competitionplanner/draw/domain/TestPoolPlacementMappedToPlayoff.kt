package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPoolPlacementMappedToPlayoff: TestBaseCreateDraw() {

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
        assertPlayOffSize(result.matches)
    }

    @Test
    fun whenAllGroupsAreFullWithBPlayOff() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
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
        val result = classCaptor.value as PoolAndCupDrawWithBPlayoffSpec

        // Assert
        assertPlayOffSize(result.aPlayoff)
        assertPlayOffSize(result.bPlayoff)
    }

    private fun assertPlayOffSize(playOff: List<PlayOffMatch>) {
        Assertions.assertEquals(3, playOff.size, "Expected in total 3 playoff matches.")

        val semifinalsB = playOff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinalsB.size)

        val finalsB = playOff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finalsB.size)
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
    fun whenGroupsAreNotFullWithBPlayOff() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
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
        val result = classCaptor.value as PoolAndCupDrawWithBPlayoffSpec

        // Assert
        Assertions.assertEquals(3, result.aPlayoff.size, "Expected in total 3 playoff matches.")

        val semifinalsA = result.aPlayoff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinalsA.size)

        val finalsA = result.aPlayoff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finalsA.size)

        Assertions.assertEquals(1, result.bPlayoff.size, "Expected in total 1 B-playoff match")
        val finalsB = result.aPlayoff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finalsB.size)
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
    fun whenTreeAdvanceToAPlayOffAndTwoToBPlayOff() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
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
        val result = classCaptor.value as PoolAndCupDrawWithBPlayoffSpec

        // Assert
        Assertions.assertEquals(3, result.aPlayoff.size, "Expected in total 3 playoff matches.")

        val semifinalsA = result.aPlayoff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinalsA.size)

        val finalsA = result.aPlayoff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finalsA.size)

        Assertions.assertEquals(7, result.bPlayoff.size, "Expected in total 7 playoff matches.")

        val quarterFinalsB = result.bPlayoff.filter { it.round == Round.QUARTER_FINAL }
        Assertions.assertEquals(4, quarterFinalsB.size)

        val semifinalsB = result.bPlayoff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinalsB.size)

        val finalsB = result.bPlayoff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finalsB.size)
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

        val firstQuarterFinal = quarterFinals.minByOrNull { it.order }!!
        Assertions.assertTrue(firstQuarterFinal.contains(Registration.Placeholder("A1")))
        val lastQuarterFinal = quarterFinals.maxByOrNull { it.order }!!
        Assertions.assertTrue(lastQuarterFinal.contains(Registration.Placeholder("B1")))

        val semifinals = result.matches.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinals.size)

        val finals = result.matches.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finals.size)
    }

    @Test
    fun whenEightAdvanceToAPlayOffAndSevenToBPlayoff() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
                playersPerGroup = 4,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = (1..15).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawWithBPlayoffSpec

        // Assert
        Assertions.assertEquals(7, result.aPlayoff.size, "Expected in total 7 playoff matches.")

        val quarterFinalsA = result.aPlayoff.filter { it.round == Round.QUARTER_FINAL }
        Assertions.assertEquals(4, quarterFinalsA.size)

        val firstQuarterFinal = quarterFinalsA.minByOrNull { it.order }!!
        Assertions.assertTrue(firstQuarterFinal.contains(Registration.Placeholder("A1")))
        val lastQuarterFinal = quarterFinalsA.maxByOrNull { it.order }!!
        Assertions.assertTrue(lastQuarterFinal.contains(Registration.Placeholder("B1")))

        val semifinalsA = result.aPlayoff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinalsA.size)

        val finalsA = result.aPlayoff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finalsA.size)

        val quarterFinalsB = result.aPlayoff.filter { it.round == Round.QUARTER_FINAL }
        Assertions.assertEquals(4, quarterFinalsB.size)

        val semifinalsB = result.aPlayoff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinalsB.size)

        val finalsB = result.aPlayoff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, finalsB.size)
    }

    fun PlayOffMatch.contains(registration: Registration.Placeholder): Boolean {
        return if (this.registrationOneId is Registration.Placeholder && this.registrationTwoId is Registration.Placeholder) {
            val first = this.registrationOneId as Registration.Placeholder
            val second = this.registrationTwoId as Registration.Placeholder
            (registration.name == first.name || registration.name == second.name)
        } else if (this.registrationOneId is Registration.Placeholder) {
            val first = this.registrationOneId as Registration.Placeholder
            registration.name == first.name
        } else if (this.registrationTwoId is Registration.Placeholder) {
            val second = this.registrationOneId as Registration.Placeholder
            registration.name == second.name
        } else {
            false
        }
    }
}