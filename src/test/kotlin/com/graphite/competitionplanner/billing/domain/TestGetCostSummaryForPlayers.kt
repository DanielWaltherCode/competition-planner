package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestGetCostSummaryForPlayers(
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val clubRepository: ClubRepository,
    @Autowired val getCostSummaryForPlayers: GetCostSummaryForPlayers,
) {

    @Test
    fun getCostSummaryForPlayers() {
        val competitions = findCompetitions.thatStartOrEndWithin(LocalDate.now().minusYears(2), LocalDate.now())
        val firstCompetitionId = competitions[0].id
        val clubsInCompetition = clubRepository.getClubsInCompetition(firstCompetitionId)
        val costSummary = getCostSummaryForPlayers.execute(firstCompetitionId, clubsInCompetition[0].id)
        Assertions.assertTrue(costSummary.costSummaryList.isNotEmpty())
        Assertions.assertTrue(costSummary.totalPrice > 0)
    }
}