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
class TestCupDrawPolicyNumberOfMatches {

    private val dataGenerator = DataGenerator()

    data class TestData(
        val numberOfRegistrations: Int,
        val expectedNumberOfMatchesPerRound: List<NumberOfMatchesPerRound>
    )

    data class NumberOfMatchesPerRound(
        val numberOfMatches: Int,
        val round: Round
    )

    private val inputTestData = listOf(
        TestData(
            2,
            listOf(NumberOfMatchesPerRound(1, Round.FINAL))
        ),
        TestData(
            3,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL)
            )
        ),
        TestData(
            4,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL)
            )
        ),
        TestData(
            5,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL)
            )
        ),
        TestData(
            6,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL)
            )
        ),
        TestData(
            7,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL)
            )
        ),
        TestData(
            8,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL)
            )
        ),
        TestData(
            9,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
            )
        ),
        TestData(
            16,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
            )
        ),
        TestData(
            17,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
            )
        ),
        TestData(
            32,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
            )
        ),
        TestData(
            33,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
                NumberOfMatchesPerRound(32, Round.ROUND_OF_64),
            )
        ),
        TestData(
            64,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
                NumberOfMatchesPerRound(32, Round.ROUND_OF_64),
            )
        ),
        TestData(
            65,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
                NumberOfMatchesPerRound(32, Round.ROUND_OF_64),
                NumberOfMatchesPerRound(64, Round.ROUND_OF_128),
            )
        ),
        TestData(
            128,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
                NumberOfMatchesPerRound(32, Round.ROUND_OF_64),
                NumberOfMatchesPerRound(64, Round.ROUND_OF_128),
            )
        ),
        TestData(
            129,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
                NumberOfMatchesPerRound(32, Round.ROUND_OF_64),
                NumberOfMatchesPerRound(64, Round.ROUND_OF_128),
                NumberOfMatchesPerRound(128, Round.UNKNOWN),
            )
        ),
        TestData(
            256,
            listOf(
                NumberOfMatchesPerRound(1, Round.FINAL),
                NumberOfMatchesPerRound(2, Round.SEMI_FINAL),
                NumberOfMatchesPerRound(4, Round.QUARTER_FINAL),
                NumberOfMatchesPerRound(8, Round.ROUND_OF_16),
                NumberOfMatchesPerRound(16, Round.ROUND_OF_32),
                NumberOfMatchesPerRound(32, Round.ROUND_OF_64),
                NumberOfMatchesPerRound(64, Round.ROUND_OF_128),
                NumberOfMatchesPerRound(128, Round.UNKNOWN),
            )
        ),
    )

    @TestFactory
    fun testNumberOfMatchesRespectingNumberOfPlayers() = inputTestData
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
                for (expected in testData.expectedNumberOfMatchesPerRound) {
                    val actualNumberOfMatches = draw.matches.filter { it.round == expected.round }
                    Assertions.assertEquals(
                        expected.numberOfMatches,
                        actualNumberOfMatches.size,
                        "Not the expected number of matches in round ${expected.round}"
                    )
                }

                for (expected in testData.expectedNumberOfMatchesPerRound.dropLast(1)) {
                    val matchesInRound = draw.matches.filter { it.round == expected.round }
                    val registrationsInRund =
                        matchesInRound.flatMap { listOf(it.registrationOneId, it.registrationTwoId) }
                    Assertions.assertTrue(
                        registrationsInRund.all { it is Registration.Placeholder },
                        "Found a non-placeholder registration in round ${expected.round}"
                    )
                }

                val expected = testData.expectedNumberOfMatchesPerRound.last()
                val registrationsFirstRound = draw.matches.filter { it.round == expected.round }
                    .flatMap { listOf(it.registrationOneId, it.registrationTwoId) }
                val realRegistrations = registrationsFirstRound.filterIsInstance<Registration.Real>()
                val placeholderRegistrations = registrationsFirstRound.filterIsInstance<Registration.Placeholder>()

                Assertions.assertEquals(
                    testData.numberOfRegistrations,
                    realRegistrations.size,
                    "Not the expected number of real registrations in first round"
                )
                Assertions.assertTrue(
                    realRegistrations.distinctBy { it.id }.size == realRegistrations.size,
                    "Not all ids of real registrations in first round are distinct"
                )
                Assertions.assertTrue(
                    placeholderRegistrations.isEmpty(),
                    "There where placeholder registrations in first round"
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