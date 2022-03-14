package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPoolAndCupDrawPolicySeeding {

    private val dataGenerator = DataGenerator()

    data class TestData(
        val numberOfPools: Int,
        val playersPerPool: Int,
        val expectedNumberOfSeeds: Int
    )

    private val inputTestData = listOf(
        TestData(2, 3, 2),
        TestData(3, 3, 2),
        TestData(4, 3, 4),
        TestData(5, 3, 4),
        TestData(6, 3, 4),
        TestData(7, 3, 4),
        TestData(8, 3, 8),
        TestData(9, 3, 8),
        TestData(14, 3, 8),
        TestData(15, 3, 8),
        TestData(16, 3, 16),
        TestData(17, 3, 16),
        TestData(2, 4, 2),
        TestData(3, 4, 2),
        TestData(4, 4, 4),
        TestData(5, 4, 4),
        TestData(6, 4, 4),
        TestData(7, 4, 4),
        TestData(8, 4, 8),
        TestData(9, 4, 8),
        TestData(14, 4, 8),
        TestData(15, 4, 8),
        TestData(16, 4, 16),
        TestData(17, 4, 16),
        TestData(2, 5, 2),
        TestData(3, 5, 2),
        TestData(4, 5, 4),
        TestData(5, 5, 4),
        TestData(6, 5, 4),
        TestData(7, 5, 4),
        TestData(8, 5, 8),
        TestData(9, 5, 8),
        TestData(14, 5, 8),
        TestData(15, 5, 8),
        TestData(16, 5, 16),
        TestData(17, 5, 16)
    )

    @TestFactory
    fun testingNumberOfSeededRegistrationsWithRespectToPoolSizeAndNumberOfPools() = inputTestData
        .map { testData ->
            DynamicTest.dynamicTest("When number of pools is ${testData.numberOfPools} and players per pool is ${testData.playersPerPool}") {
                // Setup
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_AND_CUP,
                        playersPerGroup = testData.playersPerPool,
                        playersToPlayOff = 2
                    )
                )
                val registrationRanks = generateRankingsToNumberOfFillPools(testData.numberOfPools, competitionCategory)
                val poolAndCupDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)

                // Act
                val seededRegistrations = poolAndCupDrawPolicy.createSeed(registrationRanks)

                // Assert
                Assertions.assertEquals(testData.expectedNumberOfSeeds, seededRegistrations.filter { it.seed != null }.size)
            }
        }

    private fun generateRankingsToNumberOfFillPools(numberOfPools: Int, competitionCategory: CompetitionCategoryDTO): List<RegistrationRankingDTO> {
        return (1..numberOfPools * competitionCategory.settings.playersPerGroup).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
    }
}