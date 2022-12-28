package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestGetCostSummaryForClub(
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val clubRepository: ClubRepository,
    @Autowired val getCostSummaryForClub: GetCostSummaryForClub,
    @Autowired val util: TestUtil
) {

    @Test
    fun getCostSummaryForClub() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitions = findCompetitions.thatBelongTo(lugiId)
        val lugiCompetitionId = lugiCompetitions[0].id
        val clubsInCompetition = clubRepository.getClubsInCompetition(lugiCompetitionId)
        val costSummary = getCostSummaryForClub.execute(lugiCompetitionId, clubsInCompetition[0].id)
        Assertions.assertNotNull(costSummary.club)
        Assertions.assertTrue(costSummary.costSummaries.isNotEmpty())
    }
}