package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCupDrawPolicyPlacementOfSeededInFirstRound {

    private val dataGenerator = DataGenerator()

    data class TestData(
        val numberOfRegistrations: Int,
        val matchesFirstRound: Int,
        val startRound: Round
    )

    private val TestDataWhenTwoAreSeeded = listOf(
        TestData(2, 1, Round.FINAL),
        TestData(3, 2, Round.SEMI_FINAL),
        TestData(4, 2, Round.SEMI_FINAL),
        TestData(5, 4, Round.QUARTER_FINAL),
        TestData(6, 4, Round.QUARTER_FINAL),
        TestData(7, 4, Round.QUARTER_FINAL),
        TestData(8, 4, Round.QUARTER_FINAL),
        TestData(9, 8, Round.ROUND_OF_16),
        TestData(10, 8, Round.ROUND_OF_16),
        TestData(11, 8, Round.ROUND_OF_16),
        TestData(12, 8, Round.ROUND_OF_16),
        TestData(13, 8, Round.ROUND_OF_16),
        TestData(14, 8, Round.ROUND_OF_16),
        TestData(15, 8, Round.ROUND_OF_16)
    )

    @TestFactory
    fun testPlacementOfSeededRegistrationsWhenTwoAreSeeded() = TestDataWhenTwoAreSeeded
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
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Act
                val draw = poolDrawPolicy.createDraw(seed) as CupDrawSpec

                // Assert
                val firstSeed = seed[0]
                val secondSeed = seed[1]

                val firstMatch = draw.matches.filter { it.round == testData.startRound }.first { it.order == 1 }
                val lastMatch = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound }

                Assertions.assertEquals(
                    (firstMatch.registrationOneId as Registration.Real).id,
                    firstSeed.registrationId,
                    "The best seeded player should be placed on the top of the top half."
                )
                Assertions.assertEquals(
                    (lastMatch.registrationTwoId as Registration.Real).id,
                    secondSeed.registrationId,
                    "The second best seeded player should be placed on the bottom of the bottom half."
                )
            }
        }

    private val TestDataWhenFourAreSeeded = listOf(
        TestData(16, 8, Round.ROUND_OF_16),
        TestData(17, 16, Round.ROUND_OF_32),
        TestData(18, 16, Round.ROUND_OF_32),
        TestData(19, 16, Round.ROUND_OF_32),
        TestData(20, 16, Round.ROUND_OF_32),
        TestData(21, 16, Round.ROUND_OF_32),
        TestData(22, 16, Round.ROUND_OF_32),
        TestData(23, 16, Round.ROUND_OF_32),
        TestData(24, 16, Round.ROUND_OF_32),
        TestData(25, 16, Round.ROUND_OF_32),
        TestData(26, 16, Round.ROUND_OF_32),
        TestData(27, 16, Round.ROUND_OF_32),
        TestData(28, 16, Round.ROUND_OF_32),
        TestData(29, 16, Round.ROUND_OF_32),
        TestData(30, 16, Round.ROUND_OF_32),
        TestData(31, 16, Round.ROUND_OF_32),
    )

    @TestFactory
    fun testPlacementOfSeededRegistrationsWhenFourAreSeeded() = TestDataWhenFourAreSeeded
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
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Act
                val draw = poolDrawPolicy.createDraw(seed) as CupDrawSpec

                // Assert
                val firstSeed = seed[0]
                val secondSeed = seed[1]
                val thirdSeed = seed[2]
                val fourthSeed = seed[3]

                val topHalfTopPlaceMatch =
                    draw.matches.filter { it.round == testData.startRound }.first { it.order == 1 }
                val topHalfBottomPlaceMatch = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 2 }
                val bottomHalfTopPlaceMatch = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 2 + 1 }
                val bottomHalfBottomPlaceMatch = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound }

                Assertions.assertEquals(
                    (topHalfTopPlaceMatch.registrationOneId as Registration.Real).id,
                    firstSeed.registrationId,
                    "The best seeded player should be placed on the top of the top half."
                )
                Assertions.assertTrue(
                    (topHalfBottomPlaceMatch.registrationTwoId as Registration.Real).id == thirdSeed.registrationId ||
                            (topHalfBottomPlaceMatch.registrationTwoId as Registration.Real).id == fourthSeed.registrationId,
                    "The third or fourth best seeded player should be placed randomly on the bottom of the top half."
                )
                Assertions.assertTrue(
                    (bottomHalfTopPlaceMatch.registrationOneId as Registration.Real).id == thirdSeed.registrationId ||
                            (bottomHalfTopPlaceMatch.registrationOneId as Registration.Real).id == fourthSeed.registrationId,
                    "The third or fourth best seeded player should be placed randomly on the top of the bottom half."
                )
                Assertions.assertEquals(
                    (bottomHalfBottomPlaceMatch.registrationTwoId as Registration.Real).id,
                    secondSeed.registrationId,
                    "The second best seeded player should be placed on the bottom of the bottom half."
                )
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