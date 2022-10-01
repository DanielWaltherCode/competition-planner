package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.PoolDrawStrategy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestSnakeDraw : TestBaseCreateDraw() {

    private val inputDrawType = listOf(
        DrawType.POOL_ONLY,
        DrawType.POOL_AND_CUP
    )

    @TestFactory
    fun all_players_seeded() = inputDrawType
        .map { drawType ->
            DynamicTest.dynamicTest("When draw type is $drawType") {
                // Setup
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 45555,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = drawType,
                        poolDrawStrategy = PoolDrawStrategy.SNAKE
                    )
                )

                val registrationRanks = generateRegistrationRanks(16, competitionCategory)

                val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)

                // Act
                val seed = drawPolicy.createSeed(registrationRanks)

                // Assert
                Assertions.assertTrue(seed.all { it.seed != null }, "At least one registration did not get seeded")
                Assertions.assertEquals(registrationRanks.size, seed.size, "Missing seeds")
            }
        }

    @TestFactory
    fun place_first_pool_player_left_to_right_one_in_each_pool() = inputDrawType
        .map { drawType ->
            DynamicTest.dynamicTest("When draw type is $drawType") {
                // Setup
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 45555,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_ONLY,
                        poolDrawStrategy = PoolDrawStrategy.SNAKE,
                        playersPerGroup = 4
                    )
                )

                val registrationRanks = generateRegistrationRanks(16, competitionCategory)

                val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = drawPolicy.createSeed(registrationRanks)

                // Act
                val draw = drawPolicy.createDraw(seed) as PoolDrawSpec

                // Assert
                val topFour = registrationRanks.take(4).map { it.registration }
                val poolFirsts = draw.pools.map { it.registrationIds.first() }
                Assertions.assertEquals(topFour, poolFirsts,
                    "The top four registrations should be placed left to right, one in each pool")
            }
        }

    @TestFactory
    fun place_second_pool_player_from_right_to_left() = inputDrawType
        .map { drawType ->
            DynamicTest.dynamicTest("When draw type is $drawType") {
                // Setup
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 45555,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_ONLY,
                        poolDrawStrategy = PoolDrawStrategy.SNAKE,
                        playersPerGroup = 4
                    )
                )

                val registrationRanks = generateRegistrationRanks(16, competitionCategory)

                val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = drawPolicy.createSeed(registrationRanks)

                // Act
                val draw = drawPolicy.createDraw(seed) as PoolDrawSpec

                // Assert
                val topEight = registrationRanks.take(8).drop(4).map { it.registration }
                val poolSeconds = draw.pools.map { it.registrationIds[1] }
                Assertions.assertEquals(topEight, poolSeconds.reversed(),
                    "The next top four registrations should be placed right to left, one in each pool")
            }
        }
}