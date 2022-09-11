package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.interfaces.RegistrationRankingDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TestCupDrawByeMatches : TestBaseCreateDraw() {

    @Test
    fun allPlayersInFirstRoundThatIsUpAgainstByesShouldAutomaticallyBeMovedToNextRound() {
        // Setup
        val numberOfRegistrations = 10
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsDTO(
                drawType = DrawType.CUP_ONLY,

                )
        )
        val registrationRanks = generateRegistrationRanks(numberOfRegistrations, competitionCategory)

        val poolDrawPolicy = DrawPolicy.createDrawStrategy(competitionCategory)
        val seed = poolDrawPolicy.createSeed(registrationRanks)

        // Act
        val draw = poolDrawPolicy.createDraw(seed) as CupDrawSpec

        // Act
        val roundOf16Matches = draw.matches.filter { it.round == Round.ROUND_OF_16 }
        val quarterFinals = draw.matches.filter { it.round == Round.QUARTER_FINAL }

        val byeMatches =
            roundOf16Matches.filter { it.registrationOneId == Registration.Bye || it.registrationTwoId == Registration.Bye }

        val realRegistrations = byeMatches.flatMap { listOf(it.registrationOneId, it.registrationTwoId) }
            .filterIsInstance<Registration.Real>().sortedBy { it.id }

        val quarterFinalRealRegistrations = quarterFinals.flatMap { listOf(it.registrationOneId, it.registrationTwoId) }
            .filterIsInstance<Registration.Real>().sortedBy { it.id }

        Assertions.assertEquals(realRegistrations, quarterFinalRealRegistrations,
            "We expected all real registrations that was matched against a BYE in first round to " +
                    "automatically advance to next round")
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