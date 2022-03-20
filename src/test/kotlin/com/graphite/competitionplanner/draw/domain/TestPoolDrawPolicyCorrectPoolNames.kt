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
class TestPoolDrawPolicyCorrectPoolNames {

    private val dataGenerator = DataGenerator()

    data class TestData(
        val numberOfRegistrations: Int,
        val playersPerGroup: Int,
        val expectedPoolNames: List<String>
    )

    private val inputTestData = listOf(
        TestData(8, 3, listOf("A", "B", "C")),
        TestData(9, 3, listOf("A", "B", "C")),
        TestData(10, 3, listOf("A", "B", "C", "D")),
        TestData(11, 3, listOf("A", "B", "C", "D")),
        TestData(12, 3, listOf("A", "B", "C", "D")),
        TestData(13, 3, listOf("A", "B", "C", "D", "E")),
        TestData(14, 3, listOf("A", "B", "C", "D", "E")),
        TestData(15, 3, listOf("A", "B", "C", "D", "E")),
        TestData(16, 3, listOf("A", "B", "C", "D", "E", "F")),
        TestData(17, 3, listOf("A", "B", "C", "D", "E", "F")),
        TestData(18, 3, listOf("A", "B", "C", "D", "E", "F")),
        TestData(19, 3, listOf("A", "B", "C", "D", "E", "F", "G")),
        TestData(20, 3, listOf("A", "B", "C", "D", "E", "F", "G")),
        TestData(25, 3, listOf("A", "B", "C", "D", "E", "F", "G", "H", "I")),

        TestData(8, 4, listOf("A", "B")),
        TestData(9, 4, listOf("A", "B", "C")),
        TestData(10, 4, listOf("A", "B", "C")),
        TestData(11, 4, listOf("A", "B", "C")),
        TestData(12, 4, listOf("A", "B", "C")),
        TestData(13, 4, listOf("A", "B", "C", "D")),
        TestData(14, 4, listOf("A", "B", "C", "D")),
        TestData(15, 4, listOf("A", "B", "C", "D")),
        TestData(16, 4, listOf("A", "B", "C", "D")),
        TestData(17, 4, listOf("A", "B", "C", "D", "E")),
        TestData(18, 4, listOf("A", "B", "C", "D", "E")),
        TestData(19, 4, listOf("A", "B", "C", "D", "E")),
        TestData(20, 4, listOf("A", "B", "C", "D", "E")),
        TestData(25, 4, listOf("A", "B", "C", "D", "E", "F", "G")),
        TestData(50, 4, listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M")),
        TestData(71, 4, listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R")),

        TestData(8, 5, listOf("A", "B")),
        TestData(9, 5, listOf("A", "B")),
        TestData(10, 5, listOf("A", "B")),
        TestData(11, 5, listOf("A", "B", "C")),
        TestData(12, 5, listOf("A", "B", "C")),
        TestData(13, 5, listOf("A", "B", "C")),
        TestData(14, 5, listOf("A", "B", "C")),
        TestData(15, 5, listOf("A", "B", "C")),
        TestData(16, 5, listOf("A", "B", "C", "D")),
        TestData(17, 5, listOf("A", "B", "C", "D")),
        TestData(18, 5, listOf("A", "B", "C", "D")),
        TestData(19, 5, listOf("A", "B", "C", "D")),
        TestData(20, 5, listOf("A", "B", "C", "D")),
        TestData(25, 5, listOf("A", "B", "C", "D", "E")),
        TestData(50, 5, listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")),
        TestData(71, 5, listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O")),
        TestData(99, 5, listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T")),
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
                val poolNames = draw.pools.map { it.name }
                Assertions.assertEquals(testData.expectedPoolNames, poolNames, "Not the expected pool names")
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