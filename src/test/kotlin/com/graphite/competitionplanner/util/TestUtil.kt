package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.CompetitionAndPlayingCategoryRepository
import com.graphite.competitionplanner.repositories.PlayingCategoryRepository
import com.graphite.competitionplanner.service.CompetitionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestUtil(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionAndPlayingCategoryRepository: CompetitionAndPlayingCategoryRepository,
    @Autowired val playingCategoryRepository: PlayingCategoryRepository
) {

    fun addCompetitionCategory(name: String): Int {
        val umeaId = getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = competitionService.getByClubId(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id ?: 0
        return competitionAndPlayingCategoryRepository.addCompetitionPlayingCategory(
            umeaCompetitionId,
            playingCategoryRepository.getByName(name).id
        )
    }

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName)?.id ?: 0
    }
}