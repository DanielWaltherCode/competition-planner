package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawPolicy {

    private final val dataGenerator = DataGenerator()

    @Test
    fun createPoolDrawPolicy() {
        // Setup
        val competitionCategory =
            dataGenerator.newCompetitionCategoryDTO(
                settings = dataGenerator.newGeneralSettingsDTO(
                    drawType = DrawType.POOL_ONLY
                )
            )

        // Act
        val strategy = DrawPolicy.createDrawStrategy(competitionCategory)

        // Assert
        Assertions.assertTrue(strategy is PoolOnlyDrawPolicy, "Not the expected draw policy created")
    }

    @Test
    fun createCupDrawPolicy() {
        // Setup
        val competitionCategory =
            dataGenerator.newCompetitionCategoryDTO(
                settings = dataGenerator.newGeneralSettingsDTO(
                    drawType = DrawType.CUP_ONLY
                )
            )

        // Act
        val strategy = DrawPolicy.createDrawStrategy(competitionCategory)

        // Assert
        Assertions.assertTrue(strategy is CupDrawPolicy, "Not the expected draw policy created")
    }

    @Test
    fun createPoolAndCupDrawPolicy() {
        // Setup
        val competitionCategory =
            dataGenerator.newCompetitionCategoryDTO(
                settings = dataGenerator.newGeneralSettingsDTO(
                    drawType = DrawType.POOL_AND_CUP
                )
            )

        // Act
        val strategy = DrawPolicy.createDrawStrategy(competitionCategory)

        // Assert
        Assertions.assertTrue(strategy is PoolAndCupDrawPolicy, "Not the expected draw policy created")
    }

    @Test
    fun createPoolAndCubWithA_PlayoffAndB_Playoff() {
        // Setup
        val competitionCategory =
            dataGenerator.newCompetitionCategoryDTO(
                settings = dataGenerator.newGeneralSettingsDTO(
                    drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF
                )
            )

        // Act
        val strategy = DrawPolicy.createDrawStrategy(competitionCategory)

        // Assert
        Assertions.assertTrue(strategy is PoolAndCupWithBPlayoffsDrawPolicy, "Not the expected draw policy created")
    }
}
