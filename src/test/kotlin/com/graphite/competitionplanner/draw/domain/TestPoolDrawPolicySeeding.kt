package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPoolDrawPolicySeeding {

    private val dataGenerator = DataGenerator()

    private val seedingTestData = listOf(
        2 to 2,
        3 to 2,
        4 to 4,
        5 to 4,
        6 to 4,
        7 to 4,
        8 to 8,
        9 to 8,
        10 to 8,
        11 to 8,
        12 to 8,
        13 to 8,
        14 to 8,
        15 to 8,
        16 to 16,
        17 to 16
    )

    @TestFactory
    fun poolOfSizeThree() = seedingTestData
        .map { (numberOfPools, expectedNumberOfSeeds) ->
            DynamicTest.dynamicTest("When we have $numberOfPools pools, then we expect $expectedNumberOfSeeds number of seeded") {
                // Setup
                val competitionCategory =
                    dataGenerator.newCompetitionCategoryDTO(
                        settings = dataGenerator.newGeneralSettingsDTO(
                            drawType = DrawType.POOL_ONLY,
                            playersPerGroup = 3
                        )
                    )
                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val registrationRanks = generateRankingsToNumberOfFillPools(numberOfPools, competitionCategory)

                // Act
                val seededRegistrations = poolDrawPolicy.createSeed(registrationRanks)

                // Assert
                Assertions.assertEquals(expectedNumberOfSeeds, seededRegistrations.filter { it.seed != null }.size)
            }
        }

    @TestFactory
    fun poolOfSizeFour() = seedingTestData
        .map { (numberOfPools, expectedNumberOfSeeds) ->
            DynamicTest.dynamicTest("When we have $numberOfPools pools, then we expect $expectedNumberOfSeeds number of seeded") {
                // Setup
                val competitionCategory =
                    dataGenerator.newCompetitionCategoryDTO(
                        settings = dataGenerator.newGeneralSettingsDTO(
                            drawType = DrawType.POOL_ONLY,
                            playersPerGroup = 4
                        )
                    )
                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val registrationRanks = generateRankingsToNumberOfFillPools(numberOfPools, competitionCategory)

                // Act
                val seededRegistrations = poolDrawPolicy.createSeed(registrationRanks)

                // Assert
                Assertions.assertEquals(expectedNumberOfSeeds, seededRegistrations.filter { it.seed != null }.size)
            }
        }

    @TestFactory
    fun poolOfSizeFive() = seedingTestData
        .map { (numberOfPools, expectedNumberOfSeeds) ->
            DynamicTest.dynamicTest("When we have $numberOfPools pools, then we expect $expectedNumberOfSeeds number of seeded") {
                // Setup
                val competitionCategory =
                    dataGenerator.newCompetitionCategoryDTO(
                        settings = dataGenerator.newGeneralSettingsDTO(
                            drawType = DrawType.POOL_ONLY,
                            playersPerGroup = 5
                        )
                    )
                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val registrationRanks = generateRankingsToNumberOfFillPools(numberOfPools, competitionCategory)

                // Act
                val seededRegistrations = poolDrawPolicy.createSeed(registrationRanks)

                // Assert
                Assertions.assertEquals(expectedNumberOfSeeds, seededRegistrations.filter { it.seed != null }.size)
            }
        }

    private fun generateRankingsToNumberOfFillPools(numberOfPools: Int, competitionCategory: CompetitionCategoryDTO): List<RegistrationRankingDTO> {
        return (1..numberOfPools * competitionCategory.settings.playersPerGroup).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
    }
}