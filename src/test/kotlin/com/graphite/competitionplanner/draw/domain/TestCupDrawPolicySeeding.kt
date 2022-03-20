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
class TestCupDrawPolicySeeding {

    private val dataGenerator = DataGenerator()

    data class TestData(
        val numberOfRegistrations: Int,
        val expectedNumberOfSeeded: Int
    )

    private val inputTestData = listOf(
        TestData(2, 2),
        TestData(3, 2),
        TestData(4, 2),
        TestData(5, 2),
        TestData(6, 2),
        TestData(7, 2),
        TestData(8, 2),
        TestData(9, 2),
        TestData(10, 2),
        TestData(11, 2),
        TestData(12, 2),
        TestData(13, 2),
        TestData(14, 2),
        TestData(15, 2),
        TestData(16, 4),
        TestData(17, 4),
        TestData(30, 4),
        TestData(31, 4),
        TestData(32, 8),
        TestData(33, 8),
        TestData(62, 8),
        TestData(63, 8),
        TestData(64, 16),
        TestData(65, 16),
        TestData(126, 16),
        TestData(127, 16),
        TestData(128, 32),
        TestData(129, 32),
        TestData(256, 32),
        TestData(324, 32)
    )

    @TestFactory
    fun testSeededPlayersRespectingNumberOfRegistrations() = inputTestData
        .map { testData ->
            DynamicTest.dynamicTest("When number of registrations are ${testData.numberOfRegistrations}") {
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.CUP_ONLY
                    )
                )
                val registrationRanks = generateRegistrationRanks(testData.numberOfRegistrations, competitionCategory)

                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)

                // Act
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Assert
                val seededRegistrations = seed.filter { it.seed != null }
                Assertions.assertEquals(testData.expectedNumberOfSeeded, seededRegistrations.size, "Not the expected number of seeded registrations")
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