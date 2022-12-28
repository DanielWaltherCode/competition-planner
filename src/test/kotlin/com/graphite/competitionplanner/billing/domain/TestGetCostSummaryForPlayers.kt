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
class TestGetCostSummaryForPlayers(
        @Autowired val findCompetitions: FindCompetitions,
        @Autowired val clubRepository: ClubRepository,
        @Autowired val getCostSummaryForPlayers: GetCostSummaryForPlayers,
        @Autowired val util: TestUtil
) {

    @Test
    fun getCostSummaryForPlayers() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitions = findCompetitions.thatBelongTo(lugiId)
        val lugiCompetitionId = lugiCompetitions[0].id
        val clubsInCompetition = clubRepository.getClubsInCompetition(lugiCompetitionId)
        val costSummary = getCostSummaryForPlayers.execute(lugiCompetitionId, clubsInCompetition[0].id)
        Assertions.assertTrue(costSummary.costSummaryList.isNotEmpty())
        Assertions.assertTrue(costSummary.totalPrice > 0)
    }
}