package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.PoolDrawStrategy
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestSnakeDraw : TestBaseCreateDraw() {

    private val inputDrawType = listOf(
        DrawType.POOL_ONLY,
        DrawType.POOL_AND_CUP,
        DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF
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

    @Test
    fun correct_number_of_matches_generated() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 45555,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_ONLY,
                poolDrawStrategy = PoolDrawStrategy.SNAKE
            )
        )

        val registrationRanks = generateRegistrationRanks(16, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act
        val draw = drawPolicy.createDraw(seed) as PoolDrawSpec

        // Assert
        Assertions.assertEquals(24, draw.pools.flatMap { it.matches }.size,
            "Not the correct number of matches generated")
    }

    @Test
    fun place_first_pool_player_left_to_right_one_in_each_pool() {
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
            "First round of players should be placed left to right")
    }

    @Test
    fun place_second_pool_player_from_right_to_left() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 45555,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                poolDrawStrategy = PoolDrawStrategy.SNAKE,
                playersPerGroup = 4
            )
        )

        val registrationRanks = generateRegistrationRanks(16, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act
        val draw = drawPolicy.createDraw(seed) as PoolAndCupDrawSpec

        // Assert
        val topEight = registrationRanks.take(8).drop(4).map { it.registration }
        val poolSeconds = draw.pools.map { it.registrationIds[1] }
        Assertions.assertEquals(topEight, poolSeconds.reversed(),
            "Second round of players should be placed right to left")
    }


    @TestFactory
    fun place_third_pool_player_from_left_to_right() {
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
        val topTwelve = registrationRanks.take(12).drop(8).map { it.registration }
        val poolSeconds = draw.pools.map { it.registrationIds[2] }
        Assertions.assertEquals(topTwelve, poolSeconds,
            "Third round of players should be placed left to right")
    }
}