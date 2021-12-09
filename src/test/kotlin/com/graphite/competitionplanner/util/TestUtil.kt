package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestUtil(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val findCompetitions: FindCompetitions
) {

    fun addCompetitionCategory(name: String): Int {
        val umeaId = getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = findCompetitions.thatBelongsTo(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id
        val category = categoryRepository.getAvailableCategories().first { it.name == name }
        return competitionCategoryService.addCompetitionCategory(
            umeaCompetitionId,
            CategorySpec(category.id, category.name, category.type)
        ).id
    }

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName).id
    }
}