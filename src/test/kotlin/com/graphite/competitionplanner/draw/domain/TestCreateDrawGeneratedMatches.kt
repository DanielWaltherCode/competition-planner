package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawGeneratedMatches: TestBaseCreateDraw() {

    @Test
    fun groupOfThree() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..3).toList().map {
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
        val matches = result.pools.first().matches
        Assertions.assertEquals(3, matches.size)

        for (registration in registrationRanks) {
            val res = matches.filter { it.contains(Registration.Real(registration.registrationId)) }
            Assertions.assertEquals(2, res.size, "Registration with id ${registration.registrationId} did get the correct " +
                    "number of matches.")
        }
    }

    @Test
    fun groupOfFour() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4
            )
        )
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
        val matches = result.pools.first().matches
        Assertions.assertEquals(6, matches.size)

        for (registration in registrationRanks) {
            val res = matches.filter { it.contains(Registration.Real(registration.registrationId)) }
            Assertions.assertEquals(3, res.size, "Registration with id ${registration.registrationId} did get the correct " +
                    "number of matches.")
        }
    }

    @Test
    fun groupOfFive() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 5
            )
        )
        val registrationRanks = (1..5).toList().map {
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
        val matches = result.pools.first().matches
        Assertions.assertEquals(10, matches.size)

        for (registration in registrationRanks) {
            val res = matches.filter { it.contains(Registration.Real(registration.registrationId)) }
            Assertions.assertEquals(4, res.size, "Registration with id ${registration.registrationId} did get the correct " +
                    "number of matches.")
        }
    }

    fun PoolMatch.contains(registration: Registration.Real): Boolean {
        return this.registrationOneId.id == registration.id || this.registrationTwoId.id == registration.id
    }
}