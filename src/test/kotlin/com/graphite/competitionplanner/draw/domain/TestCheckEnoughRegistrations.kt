package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class TestCheckEnoughRegistrations : TestBaseCreateDraw() {

    @Test
    fun cannotDrawCupWhenFewerThanTwoRegistrations_expect_failure() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.CUP_ONLY
            )
        )
        val registrationRanks = generateRegistrationRanks(1, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }

        Assertions.assertEquals(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, exception.exceptionType)
    }

    @Test
    fun cannotDrawCupWhenFewerThanTwoRegistrations_expect_success() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.CUP_ONLY
            )
        )
        val registrationRanks = generateRegistrationRanks(4, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        Assertions.assertDoesNotThrow {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }
    }

    @Test
    fun cannotDrawPoolOnlyWhenFewerThanTwoRegistrations_expect_failure() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_ONLY
            )
        )
        val registrationRanks = generateRegistrationRanks(1, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }

        Assertions.assertEquals(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, exception.exceptionType)
    }

    @Test
    fun cannotDrawPoolOnlyWhenFewerThanTwoRegistrations_expect_success() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_ONLY,
                playersPerGroup = 4
            )
        )
        val registrationRanks = generateRegistrationRanks(4, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        Assertions.assertDoesNotThrow {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }
    }

    @Test
    fun cannotDrawPoolAndCupWhenFewerThanTwoRegistrations_expect_failure() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP
            )
        )
        val registrationRanks = generateRegistrationRanks(1, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }

        Assertions.assertEquals(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, exception.exceptionType)
    }

    @Test
    fun cannotDrawPoolAndCupWhenFewerThanTwoRegistrations_expect_success() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = generateRegistrationRanks(4, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        Assertions.assertDoesNotThrow {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }
    }

    @Test
    fun cannotDrawPoolAndCupWhenFewerThanTwoWouldHaveAdvancedToPlayoff_expect_failure() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 3,
                playersToPlayOff = 1
            )
        )
        val registrationRanks = generateRegistrationRanks(3, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }

        Assertions.assertEquals(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, exception.exceptionType)
    }

    @Test
    fun cannotDrawPoolAndCupWhenFewerThanTwoWouldHaveAdvancedToPlayoff_expect_success() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 3,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = generateRegistrationRanks(3, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        Assertions.assertDoesNotThrow {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }
    }

    @Test
    fun cannotDrawPoolAndCupWithBPlayoffWhenFewerThanTwoWouldHaveAdvancedToAPlayoff_expect_failure() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
                playersPerGroup = 3,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = generateRegistrationRanks(3, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }

        Assertions.assertEquals(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, exception.exceptionType)
    }

    @Test
    fun cannotDrawPoolAndCupWithBPlayoffWhenFewerThanTwoWouldHaveAdvancedToBPlayoff_expect_failure() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
                playersPerGroup = 3,
                playersToPlayOff = 1
            )
        )
        val registrationRanks = generateRegistrationRanks(3, competitionCategory)

        val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = drawPolicy.createSeed(registrationRanks)

        // Act & Assert
        val exception = Assertions.assertThrows(BadRequestException::class.java) {
            drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
        }

        Assertions.assertEquals(BadRequestType.DRAW_NOT_ENOUGH_REGISTRATIONS, exception.exceptionType)
    }


    private data class TestData(
        val numberOfRegistrations: Int,
        val playersToPlayoff: Int,
    )

    private val inputTestData = listOf(
        TestData(4, 2),
        TestData(4, 4) // Edge case leaving B playoff empty
    )

    @TestFactory
    fun cannotDrawPoolAndCupWithBPlayoffWhenFewerThanTwoWouldHaveAdvancedToPlayoff_success() = inputTestData
        .map { testData ->
            DynamicTest.dynamicTest("When number of registrations are ${testData.numberOfRegistrations} " +
                        "and playersToPlayoff are ${testData.playersToPlayoff}") {
                // Setup
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF,
                        playersPerGroup = 4,
                        playersToPlayOff = testData.playersToPlayoff
                    )
                )
                val registrationRanks = generateRegistrationRanks(testData.numberOfRegistrations, competitionCategory)

                val drawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = drawPolicy.createSeed(registrationRanks)

                // Act & Assert
                Assertions.assertDoesNotThrow {
                    drawPolicy.throwExceptionIfNotEnoughRegistrations(seed)
                }
            }
        }
}