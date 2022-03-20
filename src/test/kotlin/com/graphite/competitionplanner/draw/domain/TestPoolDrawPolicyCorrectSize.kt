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
class TestPoolDrawPolicyCorrectSize {

    private val dataGenerator = DataGenerator()

    data class TestData(
        val numberOfRegistrations: Int,
        val playersPerGroup: Int,
        val expectedPoolsOfSizeFive: Int,
        val expectedPoolsOfSizeFour: Int,
        val expectedPoolsOfSizeThree: Int,
        val expectedPoolsOfSizeTwo: Int
    )

    private val inputTestData = listOf(
        TestData(8, 3, 0, 0, 2, 1),
        TestData(9, 3, 0, 0, 3, 0),
        TestData(10, 3, 0, 0, 2, 2),
        TestData(11, 3, 0, 0, 3, 1),
        TestData(12, 3, 0, 0, 4, 0),
        TestData(13, 3, 0, 0, 3, 2),
        TestData(14, 3, 0, 0, 4, 1),
        TestData(15, 3, 0, 0, 5, 0),
        TestData(16, 3, 0, 0, 4, 2),
        TestData(17, 3, 0, 0, 5, 1),
        TestData(18, 3, 0, 0, 6, 0),
        TestData(19, 3, 0, 0, 5, 2),
        TestData(20, 3, 0, 0, 6, 1),
        TestData(25, 3, 0, 0, 7, 2),
        TestData(50, 3, 0, 0, 16, 1),
        TestData(71, 3, 0, 0, 23, 1),
        TestData(99, 3, 0, 0, 33, 0),

        TestData(8, 4, 0, 2, 0, 0),
        TestData(9, 4, 0, 0, 3, 0),
        TestData(10, 4, 0, 1, 2, 0),
        TestData(11, 4, 0, 2, 1, 0),
        TestData(12, 4, 0, 3, 0, 0),
        TestData(13, 4, 0, 1, 3, 0),
        TestData(14, 4, 0, 2, 2, 0),
        TestData(15, 4, 0, 3, 1, 0),
        TestData(16, 4, 0, 4, 0, 0),
        TestData(17, 4, 0, 2, 3, 0),
        TestData(18, 4, 0, 3, 2, 0),
        TestData(19, 4, 0, 4, 1, 0),
        TestData(20, 4, 0, 5, 0, 0),
        TestData(25, 4, 0, 4, 3, 0),
        TestData(50, 4, 0, 11, 2, 0),
        TestData(71, 4, 0, 17, 1, 0),
        TestData(99, 4, 0, 24, 1, 0),

        TestData(8, 5, 0, 2, 0, 0),
        TestData(9, 5, 1, 1, 0, 0),
        TestData(10, 5, 2, 0, 0, 0),
        TestData(11, 5, 0, 2, 1, 0),
        TestData(12, 5, 0, 3, 0, 0),
        TestData(13, 5, 1, 2, 0, 0),
        TestData(14, 5, 2, 1, 0, 0),
        TestData(15, 5, 3, 0, 0, 0),
        TestData(16, 5, 0, 4, 0, 0),
        TestData(17, 5, 1, 3, 0, 0),
        TestData(18, 5, 2, 2, 0, 0),
        TestData(19, 5, 3, 1, 0, 0),
        TestData(20, 5, 4, 0, 0, 0),
        TestData(25, 5, 5, 0, 0, 0),
        TestData(50, 5, 10, 0, 0, 0),
        TestData(71, 5, 11, 4, 0, 0),
        TestData(99, 5, 19, 1, 0, 0),
    )

    @TestFactory
    fun testPoolSizeRespectingNumberOfRegistrationsAndPlayersPerGroup() = inputTestData
        .map { testData ->
            DynamicTest.dynamicTest("When players per group is ${testData.playersPerGroup} and number of registrations are ${testData.numberOfRegistrations}") {
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_ONLY,
                        playersPerGroup = testData.playersPerGroup
                    )
                )
                val registrationRanks = generateRegistrationRanks(testData.numberOfRegistrations, competitionCategory)

                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Act
                val draw = poolDrawPolicy.createDraw(seed) as PoolDrawSpec

                // Assert
                val poolsWithSize2 = draw.pools.filter { it.registrationIds.size == 2 }
                val poolsWithSize3 = draw.pools.filter { it.registrationIds.size == 3 }
                val poolsWithSize4 = draw.pools.filter { it.registrationIds.size == 4 }
                val poolsWithSize5 = draw.pools.filter { it.registrationIds.size == 5 }

                Assertions.assertEquals(testData.expectedPoolsOfSizeTwo, poolsWithSize2.size, "Not the correct number of pools of size 2")
                Assertions.assertEquals(testData.expectedPoolsOfSizeThree, poolsWithSize3.size, "Not the correct number of pools of size 3")
                Assertions.assertEquals(testData.expectedPoolsOfSizeFour, poolsWithSize4.size, "Not the correct number of pools of size 4")
                Assertions.assertEquals(testData.expectedPoolsOfSizeFive, poolsWithSize5.size, "Not the correct number of pools of size 5")
            }
        }

    private fun generateRegistrationRanks(
        numberOfRegistrations: Int,
        competitionCategory: CompetitionCategoryDTO
    ): List<RegistrationRankingDTO> {
        return (1..numberOfRegistrations).toList().map {
            dataGenerator.newRegistrationRankDTO(
                id = it,
                competitionCategoryId = competitionCategory.id,
                rank = numberOfRegistrations - it + 1
            )
        }
    }
}