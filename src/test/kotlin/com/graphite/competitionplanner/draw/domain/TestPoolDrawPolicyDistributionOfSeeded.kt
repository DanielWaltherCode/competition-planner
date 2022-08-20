package com.graphite.competitionplanner.draw.domain;

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestPoolDrawPolicyDistributionOfSeeded {

    private val dataGenerator = DataGenerator()

    private val numberOfRegistrations = listOf(
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        25,
        50,
        71,
        99
    )

    @TestFactory
    fun poolOfSizeThree() = numberOfRegistrations
        .map { numberOfRegistrations ->
            DynamicTest.dynamicTest("Asserting distribution of seeds when pool size is three") {
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_ONLY,
                        playersPerGroup = 3
                    )
                )
                val registrationRanks = generateRegistrationRanks(numberOfRegistrations, competitionCategory)

                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Act
                val draw = poolDrawPolicy.createDraw(seed) as PoolDrawSpec

                // Assert
                assertThatSeededRegistrationsArePlacedInDistinctPools(draw, seed)
            }
        }

    @TestFactory
    fun poolOfSizeFour() = numberOfRegistrations
        .map { numberOfRegistrations ->
            DynamicTest.dynamicTest("Asserting distribution of seeds when pool size is four.") {
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_ONLY,
                        playersPerGroup = 4
                    )
                )
                val registrationRanks = generateRegistrationRanks(numberOfRegistrations, competitionCategory)

                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Act
                val draw = poolDrawPolicy.createDraw(seed) as PoolDrawSpec

                // Assert
                assertThatSeededRegistrationsArePlacedInDistinctPools(draw, seed)
            }
        }

    @TestFactory
    fun poolOfSizeFive() = numberOfRegistrations
        .map { numberOfRegistrations ->
            DynamicTest.dynamicTest("Asserting distribution of seeds when pool size is five.") {
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_ONLY,
                        playersPerGroup = 5
                    )
                )
                val registrationRanks = generateRegistrationRanks(numberOfRegistrations, competitionCategory)

                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Act
                val draw = poolDrawPolicy.createDraw(seed) as PoolDrawSpec

                // Assert
                assertThatSeededRegistrationsArePlacedInDistinctPools(draw, seed)
            }
        }

    private fun assertThatSeededRegistrationsArePlacedInDistinctPools(draw: PoolDrawSpec, seed: List<RegistrationSeedDTO>) {
        val seededRegistrations = seed.filter { it.seed != null }

        val pools = draw.pools.sortedBy { it.name }
        for (i in seededRegistrations.indices) {
            val seededRegistrationId = seededRegistrations[i].registrationId
            Assertions.assertTrue(pools[i].registrationIds.contains(seededRegistrationId),
                "Expected to find id $seededRegistrationId in ${pools[i]}")
        }
    }

    private fun generateRegistrationRanks(numberOfRegistrations: Int, competitionCategory: CompetitionCategoryDTO): List<RegistrationRankingDTO> {
        return (1..numberOfRegistrations).toList().map {
            dataGenerator.newRegistrationRankDTO(
                id = it,
                competitionCategoryId = competitionCategory.id,
                rank = numberOfRegistrations - it + 1)
        }
    }
}
