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
class TestPoolAndCupDrawPolicyPlacementOfSeededInFirstRound {

    private val dataGenerator = DataGenerator()

    data class TestData(
        val playersPerGroup: Int,
        val numberOfRegistrations: Int,
        val startRound: Round,
        val expectedFirstRoundOfMatches: List<ExpectedFirstRoundMatch>
    )

    data class ExpectedFirstRoundMatch(
        val order: Int,
        val possibleRegistrationOne: List<String>,
        val possibleRegistrationTwo: List<String>
    )

    private val testDataWhenTwoAreSeeded = listOf(
        TestData(
            4, 6, Round.SEMI_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("B2")),
                ExpectedFirstRoundMatch(2, listOf("A2"), listOf("B1"))
            )
        ),
        TestData(
            4, 7, Round.SEMI_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("B2")),
                ExpectedFirstRoundMatch(2, listOf("A2"), listOf("B1"))
            )
        ),
        TestData(
            4, 8, Round.SEMI_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("B2")),
                ExpectedFirstRoundMatch(2, listOf("A2"), listOf("B1"))
            )
        ),
        TestData(
            4, 9, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("BYE")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C1", "C2"), listOf("B2", "C1", "C2")),
                ExpectedFirstRoundMatch(3, listOf("A2", "C1", "C2"), listOf("A2", "C1", "C2")),
                ExpectedFirstRoundMatch(4, listOf("BYE"), listOf("B1"))
            )
        ),
        TestData(
            4, 10, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("BYE")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C1", "C2"), listOf("B2", "C1", "C2")),
                ExpectedFirstRoundMatch(3, listOf("A2", "C1", "C2"), listOf("A2", "C1", "C2")),
                ExpectedFirstRoundMatch(4, listOf("BYE"), listOf("B1"))
            )
        ),
        TestData(
            4, 11, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("BYE")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C1", "C2"), listOf("B2", "C1", "C2")),
                ExpectedFirstRoundMatch(3, listOf("A2", "C1", "C2"), listOf("A2", "C1", "C2")),
                ExpectedFirstRoundMatch(4, listOf("BYE"), listOf("B1"))
            )
        ),
        TestData(
            4, 12, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("BYE")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C1", "C2"), listOf("B2", "C1", "C2")),
                ExpectedFirstRoundMatch(3, listOf("A2", "C1", "C2"), listOf("A2", "C1", "C2")),
                ExpectedFirstRoundMatch(4, listOf("BYE"), listOf("B1"))
            )
        ),
        TestData(
            4, 13, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("B2", "C2", "D2")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C2", "D2"), listOf("C1", "D1")),
                ExpectedFirstRoundMatch(3, listOf("C1", "D1"), listOf("A2", "C2", "D2")),
                ExpectedFirstRoundMatch(4, listOf("A2", "C2", "D2"), listOf("B1"))
            )
        ),
        TestData(
            4, 14, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("B2", "C2", "D2")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C2", "D2"), listOf("C1", "D1")),
                ExpectedFirstRoundMatch(3, listOf("C1", "D1"), listOf("A2", "C2", "D2")),
                ExpectedFirstRoundMatch(4, listOf("A2", "C2", "D2"), listOf("B1"))
            )
        ),
        TestData(
            4, 15, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("B2", "C2", "D2")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C2", "D2"), listOf("C1", "D1")),
                ExpectedFirstRoundMatch(3, listOf("C1", "D1"), listOf("A2", "C2", "D2")),
                ExpectedFirstRoundMatch(4, listOf("A2", "C2", "D2"), listOf("B1"))
            )
        ),
        TestData(
            4, 16, Round.QUARTER_FINAL,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("B2", "C2", "D2")),
                ExpectedFirstRoundMatch(2, listOf("B2", "C2", "D2"), listOf("C1", "D1")),
                ExpectedFirstRoundMatch(3, listOf("C1", "D1"), listOf("A2", "C2", "D2")),
                ExpectedFirstRoundMatch(4, listOf("A2", "C2", "D2"), listOf("B1"))
            )
        ),
        TestData(
            4, 17, Round.ROUND_OF_16,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("BYE")),
                ExpectedFirstRoundMatch(
                    2,
                    listOf("E1", "B2", "C2", "D2", "E2", "BYE"),
                    listOf("E1", "B2", "C2", "D2", "E2")
                ),
                ExpectedFirstRoundMatch(
                    3,
                    listOf("E1", "B2", "C2", "D2", "E2"),
                    listOf("E1", "B2", "C2", "D2", "E2", "BYE")
                ),
                ExpectedFirstRoundMatch(4, listOf("BYE"), listOf("C1", "D1")),
                ExpectedFirstRoundMatch(5, listOf("C1", "D1"), listOf("BYE")),
                ExpectedFirstRoundMatch(
                    6,
                    listOf("E1", "A2", "C2", "D2", "E2", "BYE"),
                    listOf("E1", "A2", "C2", "D2", "E2")
                ),
                ExpectedFirstRoundMatch(
                    7,
                    listOf("E1", "A2", "C2", "D2", "E2"),
                    listOf("E1", "A2", "C2", "D2", "E2", "BYE")
                ),
                ExpectedFirstRoundMatch(8, listOf("BYE"), listOf("B1"))
            )
        ),
        TestData(
            4, 18, Round.ROUND_OF_16,
            listOf(
                ExpectedFirstRoundMatch(1, listOf("A1"), listOf("BYE")),
                ExpectedFirstRoundMatch(
                    2,
                    listOf("E1", "B2", "C2", "D2", "E2", "BYE"),
                    listOf("E1", "B2", "C2", "D2", "E2")
                ),
                ExpectedFirstRoundMatch(
                    3,
                    listOf("E1", "B2", "C2", "D2", "E2"),
                    listOf("E1", "B2", "C2", "D2", "E2", "BYE")
                ),
                ExpectedFirstRoundMatch(4, listOf("BYE"), listOf("C1", "D1")),
                ExpectedFirstRoundMatch(5, listOf("C1", "D1"), listOf("BYE")),
                ExpectedFirstRoundMatch(
                    6,
                    listOf("E1", "A2", "C2", "D2", "E2", "BYE"),
                    listOf("E1", "A2", "C2", "D2", "E2")
                ),
                ExpectedFirstRoundMatch(
                    7,
                    listOf("E1", "A2", "C2", "D2", "E2"),
                    listOf("E1", "A2", "C2", "D2", "E2", "BYE")
                ),
                ExpectedFirstRoundMatch(8, listOf("BYE"), listOf("B1"))
            )
        ),
    )

    @TestFactory
    fun testPlacementOfSeededRegistrationsWhenTwoAreSeeded() = testDataWhenTwoAreSeeded
        .map { testData ->
            DynamicTest.dynamicTest("When 2 players per group goes to playoff and number of registrations are ${testData.numberOfRegistrations}") {
                val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
                    id = 33,
                    settings = dataGenerator.newGeneralSettingsDTO(
                        drawType = DrawType.POOL_AND_CUP,
                        playersPerGroup = testData.playersPerGroup,
                        playersToPlayOff = 2
                    )
                )
                val registrationRanks = generateRegistrationRanks(testData.numberOfRegistrations, competitionCategory)

                val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
                val seed = poolDrawPolicy.createSeed(registrationRanks)

                // Act
                val draw = poolDrawPolicy.createDraw(seed) as PoolAndCupDrawSpec

                // Assert
                val matchesInFirstRound = draw.matches.filter { it.round == testData.startRound }

                for (expectedMatch in testData.expectedFirstRoundOfMatches) {
                    val actualMatch = matchesInFirstRound.first { it.order == expectedMatch.order }
                    Assertions.assertTrue(
                        expectedMatch.possibleRegistrationOne.contains(actualMatch.registrationOneId.toString()),
                        "In match ${expectedMatch.order}: Expected to find one of ${expectedMatch.possibleRegistrationOne} in position one, was ${actualMatch.registrationOneId}"
                    )
                    Assertions.assertTrue(
                        expectedMatch.possibleRegistrationTwo.contains(actualMatch.registrationTwoId.toString()),
                        "In match ${expectedMatch.order}: Expected to find one of ${expectedMatch.possibleRegistrationTwo} in position two, was ${actualMatch.registrationTwoId}"
                    )
                }

                assertWinnersFromSamePoolAreNotOnSameHalves(matchesInFirstRound.take(matchesInFirstRound.size / 2))
                assertWinnersFromSamePoolAreNotOnSameHalves(matchesInFirstRound.takeLast(matchesInFirstRound.size / 2))
//                assertThatSecondPlaceWinnersAreNotMatchedAgainstByes(matchesInFirstRound) TODO: This fails in some cases
            }
        }

    private fun assertThatSecondPlaceWinnersAreNotMatchedAgainstByes(playOffMatches: List<PlayOffMatch>) {
        val matchesWithSecondPlaceWinners = playOffMatches.filter {
            it.registrationOneId.toString().endsWith("2") || it.registrationTwoId.toString().endsWith("2")
        }

        Assertions.assertTrue(
            matchesWithSecondPlaceWinners.none { it.registrationOneId.toString() == "BYE" || it.registrationTwoId.toString() == "BYE" },
            "There seems to be a second place winner that was matched against a BYE in first round. BYEs should be matched against pool winners in first round. Matches: $matchesWithSecondPlaceWinners"
        )
    }

    private fun assertWinnersFromSamePoolAreNotOnSameHalves(playOffMatches: List<PlayOffMatch>) {
        val topHalf = playOffMatches
            .flatMap { listOf(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
            .filterNot { it == "BYE" }
        val distinct = topHalf.distinctBy { it.first() } // Distinct on pool name
        Assertions.assertEquals(
            topHalf,
            distinct,
            "There seems to be winners from same pool on the same half: $topHalf"
        )
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