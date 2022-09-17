package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestCreateDrawCorrectCompetitionCategory : TestBaseCreateDraw() {

    @Test
    fun whenDrawingCupOnly() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.CUP_ONLY
            )
        )
        val registrationRanks = generateRegistrationRanks(10, competitionCategory)

        val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = poolDrawPolicy.createSeed(registrationRanks)

        // Act
        val draw = poolDrawPolicy.createDraw(seed) as CupDrawSpec

        // Assert
        Assertions.assertEquals(competitionCategory.id, draw.competitionCategoryId, "Wrong competition category id")
    }

    @Test
    fun whenDrawingPoolOnly() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_ONLY
            )
        )
        val registrationRanks = generateRegistrationRanks(15, competitionCategory)

        val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = poolDrawPolicy.createSeed(registrationRanks)

        // Act
        val draw = poolDrawPolicy.createDraw(seed) as PoolDrawSpec

        // Assert
        Assertions.assertEquals(competitionCategory.id, draw.competitionCategoryId, "Wrong competition category id")
    }

    @Test
    fun whenDrawingPoolAndCup() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP
            )
        )
        val registrationRanks = generateRegistrationRanks(13, competitionCategory)

        val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = poolDrawPolicy.createSeed(registrationRanks)

        // Act
        val draw = poolDrawPolicy.createDraw(seed) as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(competitionCategory.id, draw.competitionCategoryId, "Wrong competition category id")
    }
}