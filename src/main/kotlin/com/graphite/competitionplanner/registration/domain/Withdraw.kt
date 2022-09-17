package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.PlayerRegistrationStatus
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.springframework.stereotype.Component

@Component
class Withdraw(
    val registrationRepository: IRegistrationRepository,
    val matchRepository: MatchRepository,
    val resultService: ResultService,
    val drawService: DrawService,
    val findCompetitionCategory: FindCompetitionCategory,
) {

    /**
     * This method is called before competition begins. If draw is made, matches are lost.
     * Otherwise, player is just removed from competition but can still be billed for participating.
     * A player withdraws for a given registration in one category. To withdraw from the entire competition
     * the player must withdraw from each category he/she is registered in.
     */
    fun beforeCompetition(competitionId: Int, categoryId: Int, registrationId: Int) {
        // Withdrawing means losing all registered matches
        if (drawService.isDrawMade(categoryId)) {
            loseRemainingMatches(competitionId, categoryId, registrationId)
        }

        // Set registration status to withdraw
        registrationRepository.updatePlayerRegistrationStatus(
            registrationId,
            PlayerRegistrationStatus.WITHDRAWN
        )
    }

    fun walkOver(competitionId: Int, competitionCategoryId: Int, registrationId: Int) {
        loseRemainingMatches(competitionId, competitionCategoryId, registrationId)

        // Set registration status to walkover
        registrationRepository.updatePlayerRegistrationStatus(
            registrationId,
            PlayerRegistrationStatus.WALK_OVER
        )
    }

    private fun loseRemainingMatches(competitionId: Int, categoryId: Int, registrationId: Int) {
        val matches: List<MatchRecord> = matchRepository.getMatchesInCompetitionForRegistration(competitionId, registrationId)
        val gameRules = findCompetitionCategory.byId(categoryId).gameSettings
        for (match in matches) {
            // If match already has a winner, this method is called for a walkover, then ignore this match and continue.
            if (match.winner != null) {
                continue
            }
            val gameResults = mutableListOf<GameSpec>()
            if (match.firstRegistrationId == registrationId) {
                for (i in 1..(gameRules.numberOfSets / 2) + 1) {
                    gameResults.add(
                        GameSpec(gameNumber = i,
                            firstRegistrationResult = 0,
                            secondRegistrationResult = gameRules.winScore)
                    )
                }
            }
            else if (match.secondRegistrationId == registrationId) {
                for (i in 1..(gameRules.numberOfSets / 2) + 1) {
                    gameResults.add(
                        GameSpec(gameNumber = i,
                            firstRegistrationResult = gameRules.winScore,
                            secondRegistrationResult = 0)
                    )
                }
            }
            /* Both withdrawal and walkover are coded as walkover in match table since they have same
             effect on ranking points
             The value in player_registration table shows which of them it actually was
             */
            match.wasWalkover = true
            match.update()
            resultService.addFinalMatchResult(match.id, ResultSpec(gameResults))
        }
    }
}