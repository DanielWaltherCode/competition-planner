package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawGeneratedPoolMatches: TestBaseCreateDraw() {

    private data class TestData(
        val numberOfRegistrations: Int,
        val expectedMatchesInPool: Int,
        val expectedMatchesPerRegistration: Int
    )

    private val inputTestData = listOf(
        TestData(3, 3, 2),
        TestData(4, 6, 3),
        TestData(5, 10, 4)
    )

    @TestFactory
    fun whenPoolOnly() = inputTestData
        .map { testData ->
            DynamicTest.dynamicTest("When number of registrations are ${testData.numberOfRegistrations}") {
                Mockito.reset(mockedCompetitionDrawRepository)

                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_ONLY,
                        playersPerGroup = testData.numberOfRegistrations
                    )
                )
                val registrationRanks = (1..testData.numberOfRegistrations).toList().map {
                    dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
                }
                Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
                Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
                    .thenReturn(registrationRanks)

                // Act
                createDraw.execute(competitionCategory.id)

                // Record the spec sent to the repository for validation
                Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
                val result = classCaptor.value as PoolDrawSpec

                // Assert
                val matches = result.pools.first().matches
                Assertions.assertEquals(testData.expectedMatchesInPool, matches.size)

                for (registrationRank in registrationRanks) {
                    val res = matches.filter { it.contains(registrationRank.registration) }
                    Assertions.assertEquals(testData.expectedMatchesPerRegistration, res.size,
                        "Registration with id ${registrationRank.registration.id} did not get the correct number of matches."
                    )
                }
            }
        }

    @TestFactory
    fun whenPoolAndCup() = inputTestData
        .map { testData ->
            DynamicTest.dynamicTest("When number of registrations are ${testData.numberOfRegistrations}") {
                Mockito.reset(mockedCompetitionDrawRepository)

                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_AND_CUP,
                        playersPerGroup = testData.numberOfRegistrations
                    )
                )
                val registrationRanks = (1..testData.numberOfRegistrations).toList().map {
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
                Assertions.assertEquals(testData.expectedMatchesInPool, matches.size)

                for (registrationRank in registrationRanks) {
                    val res = matches.filter { it.contains(registrationRank.registration) }
                    Assertions.assertEquals(testData.expectedMatchesPerRegistration, res.size,
                        "Registration with id ${registrationRank.registration.id} did not get the correct number of matches."
                    )
                }
            }
        }

    @TestFactory
    fun whenPoolAndCupWithBPlayoff() = inputTestData
        .map { testData ->
            DynamicTest.dynamicTest("When number of registrations are ${testData.numberOfRegistrations}") {
                Mockito.reset(mockedCompetitionDrawRepository)

                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
                        playersPerGroup = testData.numberOfRegistrations
                    )
                )
                val registrationRanks = (1..testData.numberOfRegistrations).toList().map {
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
                val matches = result.pools.first().matches
                Assertions.assertEquals(testData.expectedMatchesInPool, matches.size)

                for (registrationRank in registrationRanks) {
                    val res = matches.filter { it.contains(registrationRank.registration) }
                    Assertions.assertEquals(testData.expectedMatchesPerRegistration, res.size,
                        "Registration with id ${registrationRank.registration.id} did not get the correct number of matches."
                    )
                }
            }
        }

    fun PoolMatch.contains(registration: Registration.Real): Boolean {
        return this.registrationOneId.id == registration.id || this.registrationTwoId.id == registration.id
    }
}