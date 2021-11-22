package com.graphite.competitionplanner.billing.repository

import com.graphite.competitionplanner.billing.domain.GetCostSummaryForClub
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestBillingRepository(
    @Autowired val clubRepository: IClubRepository,
    @Autowired val findCompetitions: FindCompetitions,
) {

    @Test
    fun testGetClubsInCompetition() {
        val competitions = findCompetitions.thatStartOrEndWithin(LocalDate.now().minusYears(2), LocalDate.now())
        val firstCompetitionId = competitions[0].id
        val clubsInCompetition = clubRepository.getClubsInCompetition(firstCompetitionId)
        Assertions.assertTrue(clubsInCompetition.isNotEmpty() && clubsInCompetition.size < 10)
    }


}