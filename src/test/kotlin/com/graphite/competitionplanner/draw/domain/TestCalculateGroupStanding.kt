package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCalculateGroupStanding(
    @Autowired val getDraw: GetDraw,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val getCompetitionCategories: GetCompetitionCategories,
    @Autowired val util: Util
) {

    @Test
    fun testGroupStanding() {
        val lugiId = util.getClubIdOrDefault("Lugi")
        val lugiCompetitionId = competitionRepository.findCompetitionsThatBelongTo(lugiId)[0].id
        val competitionCategories = getCompetitionCategories.execute(lugiCompetitionId)
        val herrar2 = competitionCategories[1]
        val draw = getDraw.execute(herrar2.id)

        for (group in draw.groups) {
            val totalMatches = group.matches.size
            var matchParticipations = 0
            var matchesWon = 0
            var matchesLost = 0
            var gamesWon = 0
            var gamesLost = 0
            var pointsWon = 0
            var pointsLost = 0
            for (result in group.groupStandingList) {
                matchParticipations += result.matchesPlayed
                matchesWon += result.matchesWonLost.won
                matchesLost += result.matchesWonLost.lost

                gamesWon += result.gamesWonLost.won
                gamesLost += result.gamesWonLost.lost

                pointsWon += result.pointsWonLost.won
                pointsLost += result.pointsWonLost.lost
            }
            // There are 2 "participations" in each match
            Assertions.assertEquals(totalMatches * 2, matchParticipations)
            Assertions.assertEquals(totalMatches, matchesWon)
            Assertions.assertEquals(totalMatches, matchesLost)
            Assertions.assertEquals(gamesWon, gamesLost)
            Assertions.assertEquals(pointsWon, pointsLost)

        }
    }
}