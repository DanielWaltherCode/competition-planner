package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.registration.domain.Registration
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

    private val testDataWhenTwoAreSeeded = listOf(
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
    fun testPlacementOfSeededRegistrationsWhenTwoAreSeeded() = testDataWhenTwoAreSeeded
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
                    (firstMatch.registrationOneId as Registration.Real),
                    firstSeed.registrationId,
                    "The best seeded player should be placed on the top of the top half."
                )
                Assertions.assertEquals(
                    (lastMatch.registrationTwoId as Registration.Real),
                    secondSeed.registrationId,
                    "The second best seeded player should be placed on the bottom of the bottom half."
                )
            }
        }

    private val testDataWhenFourAreSeeded = listOf(
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
    fun testPlacementOfSeededRegistrationsWhenFourAreSeeded() = testDataWhenFourAreSeeded
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
                    (topHalfTopPlaceMatch.registrationOneId as Registration.Real),
                    firstSeed.registrationId,
                    "The best seeded player should be placed on the top of the top half."
                )
                Assertions.assertTrue(
                    (topHalfBottomPlaceMatch.registrationTwoId as Registration.Real) == thirdSeed.registrationId ||
                            topHalfBottomPlaceMatch.registrationTwoId == fourthSeed.registrationId,
                    "The third or fourth best seeded player should be placed randomly on the bottom of the top half."
                )
                Assertions.assertTrue(
                    (bottomHalfTopPlaceMatch.registrationOneId as Registration.Real) == thirdSeed.registrationId ||
                            bottomHalfTopPlaceMatch.registrationOneId == fourthSeed.registrationId,
                    "The third or fourth best seeded player should be placed randomly on the top of the bottom half."
                )
                Assertions.assertEquals(
                    (bottomHalfBottomPlaceMatch.registrationTwoId as Registration.Real),
                    secondSeed.registrationId,
                    "The second best seeded player should be placed on the bottom of the bottom half."
                )
            }
        }

    private val testDataWhenEightAreSeeded = listOf(
        TestData(32, 16, Round.ROUND_OF_32),
        TestData(33, 32, Round.ROUND_OF_64),
        TestData(34, 32, Round.ROUND_OF_64),
        TestData(35, 32, Round.ROUND_OF_64),
        TestData(43, 32, Round.ROUND_OF_64),
        TestData(44, 32, Round.ROUND_OF_64),
        TestData(55, 32, Round.ROUND_OF_64),
        TestData(59, 32, Round.ROUND_OF_64),
        TestData(61, 32, Round.ROUND_OF_64),
        TestData(62, 32, Round.ROUND_OF_64),
        TestData(63, 32, Round.ROUND_OF_64),
    )

    @TestFactory
    fun testPlacementOfSeededRegistrationsWhenEightAreSeeded() = testDataWhenEightAreSeeded
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
                val seeded3And4 = listOf(seed[2].registrationId, seed[3].registrationId)
                val seeded5to8 = listOf(seed[4].registrationId, seed[5].registrationId, seed[6].registrationId, seed[7].registrationId)

                val firstQuarterTopPlace =
                    draw.matches.filter { it.round == testData.startRound }.first { it.order == 1 }
                val firstQuarterBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 }
                val secondQuarterTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 + 1 }
                val secondQuarterBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 2 }
                val thirdQuarterTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 2 + 1 }
                val thirdQuarterBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 * 3 }
                val fourthQuarterTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 * 3 + 1 }
                val fourthQuarterBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound }

                Assertions.assertEquals(
                    (firstQuarterTopPlace.registrationOneId as Registration.Real),
                    firstSeed.registrationId,
                    "The best seeded player should be placed on the top of the top half."
                )
                Assertions.assertTrue(
                    seeded5to8.contains((firstQuarterBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the first quarter's bottom place"
                )
                Assertions.assertTrue(
                    seeded5to8.contains((secondQuarterTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the second quarter's top place"
                )
                Assertions.assertTrue(
                    seeded3And4.contains((secondQuarterBottomPlace.registrationTwoId as Registration.Real)),
                    "The third or fourth best seeded player should be placed randomly on the bottom of the top half."
                )
                Assertions.assertTrue(
                    seeded3And4.contains((thirdQuarterTopPlace.registrationOneId as Registration.Real)),
                    "The third or fourth best seeded player should be placed randomly on the top of the bottom half."
                )
                Assertions.assertTrue(
                    seeded5to8.contains((thirdQuarterBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the third quarter's bottom place"
                )
                Assertions.assertTrue(
                    seeded5to8.contains((fourthQuarterTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the fourth quarter's top place"
                )
                Assertions.assertEquals(
                    (fourthQuarterBottomPlace.registrationTwoId as Registration.Real),
                    secondSeed.registrationId,
                    "The second best seeded player should be placed on the bottom of the bottom half."
                )
            }
        }


    private val testDataWhen16AreSeeded = listOf(
        TestData(64, 32, Round.ROUND_OF_64),
        TestData(65, 64, Round.ROUND_OF_128),
        TestData(66, 64, Round.ROUND_OF_128),
        TestData(76, 64, Round.ROUND_OF_128),
        TestData(86, 64, Round.ROUND_OF_128),
        TestData(100, 64, Round.ROUND_OF_128),
        TestData(101, 64, Round.ROUND_OF_128),
        TestData(115, 64, Round.ROUND_OF_128),
        TestData(125, 64, Round.ROUND_OF_128),
        TestData(126, 64, Round.ROUND_OF_128),
        TestData(127, 64, Round.ROUND_OF_128),
    )

    @TestFactory
    fun testPlacementOfSeededRegistrationsWhen16AreSeeded() = testDataWhen16AreSeeded
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
                val seeded3And4 = listOf(seed[2].registrationId, seed[3].registrationId)
                val seeded5to8 = listOf(seed[4].registrationId, seed[5].registrationId, seed[6].registrationId, seed[7].registrationId)
                val seeded9to16 = listOf(seed[8].registrationId, seed[9].registrationId, seed[10].registrationId, seed[11].registrationId,
                    seed[12].registrationId, seed[13].registrationId, seed[14].registrationId, seed[15].registrationId)

                val firstEighthsTopPlace =
                    draw.matches.filter { it.round == testData.startRound }.first { it.order == 1 }
                val firstEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 }
                val secondEightsTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 + 1}
                val secondEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 }
                val thirdEightsTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 + 1 }
                val thirdEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 * 3 }
                val fourthEightsTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 * 3 + 1 }
                val fourthEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 2 }
                val fifthEightsTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 2 + 1 }
                val fifthEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 * 5 }
                val sixthEightsTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 * 5 + 1 }
                val sixthEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 * 3 }
                val seventhEightsTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 4 * 3 + 1 }
                val seventhEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 * 7  }
                val lastEightsTopPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound / 8 * 7 + 1  }
                val lastEightsBottomPlace = draw.matches.filter { it.round == testData.startRound }
                    .first { it.order == testData.matchesFirstRound }

                Assertions.assertEquals(
                    (firstEighthsTopPlace.registrationOneId as Registration.Real),
                    firstSeed.registrationId,
                    "The best seeded player should be placed on the top of the top half."
                )
                Assertions.assertTrue(
                    seeded9to16.contains((firstEightsBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the 9 to 16 seeded registrations in the eights bottom place"
                )

                Assertions.assertTrue(
                    seeded9to16.contains((secondEightsTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the 9 to 16 seeded registrations in the second eights top place"
                )
                Assertions.assertTrue(
                    seeded5to8.contains((secondEightsBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the first quarter's bottom place"
                )

                Assertions.assertTrue(
                    seeded5to8.contains((thirdEightsTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the second quarter's top place"
                )
                Assertions.assertTrue(
                    seeded9to16.contains((thirdEightsBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the second quarter's top place"
                )

                Assertions.assertTrue(
                    seeded9to16.contains((fourthEightsTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the 9 to 16 seeded registrations in the second eights top place"
                )
                Assertions.assertTrue(
                    seeded3And4.contains((fourthEightsBottomPlace.registrationTwoId as Registration.Real)),
                    "The third or fourth best seeded player should be placed randomly on the bottom of the top half."
                )

                Assertions.assertTrue(
                    seeded3And4.contains((fifthEightsTopPlace.registrationOneId as Registration.Real)),
                    "The third or fourth best seeded player should be placed randomly on the top of the bottom half."
                )
                Assertions.assertTrue(
                    seeded9to16.contains((fifthEightsBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the 9 to 16 seeded registrations in the fifth eights bottom place"
                )

                Assertions.assertTrue(
                    seeded9to16.contains((sixthEightsTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the 9 to 16 seeded registrations in the sixth eights top place"
                )
                Assertions.assertTrue(
                    seeded5to8.contains((sixthEightsBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the third quarter's bottom place"
                )

                Assertions.assertTrue(
                    seeded5to8.contains((seventhEightsTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the fifth to eighth seeded registrations in the fourth quarter's top place"
                )
                Assertions.assertTrue(
                    seeded9to16.contains((seventhEightsBottomPlace.registrationTwoId as Registration.Real)),
                    "Expected to find one of the 9 to 16 seeded registrations in the seventh eights bottom place"
                )

                Assertions.assertTrue(
                    seeded9to16.contains((lastEightsTopPlace.registrationOneId as Registration.Real)),
                    "Expected to find one of the 9 to 16 seeded registrations in the last eights top place"
                )
                Assertions.assertEquals(
                    (lastEightsBottomPlace.registrationTwoId as Registration.Real),
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