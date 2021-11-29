package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestGetCostSummaryForClub(
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val clubRepository: ClubRepository,
    @Autowired val getCostSummaryForClub: GetCostSummaryForClub
) {

    @Test
    fun getCostSummaryForClub() {
        val competitions = findCompetitions.thatStartOrEndWithin(LocalDate.now().minusYears(2), LocalDate.now())
        val firstCompetitionId = competitions[0].id
        val clubsInCompetition = clubRepository.getClubsInCompetition(firstCompetitionId)
        val costSummary = getCostSummaryForClub.execute(firstCompetitionId, clubsInCompetition[0].id)
        Assertions.assertNotNull(costSummary.club)
        Assertions.assertTrue(costSummary.costSummaries.isNotEmpty())
    }
}