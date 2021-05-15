package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestUtil(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val categoryRepository: CategoryRepository
) {

    fun addCompetitionCategory(name: String): Int {
        val umeaId = getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = competitionService.getByClubId(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id ?: 0
        return competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            categoryRepository.getByName(name).id
        )
    }

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName).id
    }
}