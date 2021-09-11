package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
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
        val category = categoryRepository.getAvailableCategories().first { it.name == name }
        return competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            category
        ).id
    }

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName).id
    }
}