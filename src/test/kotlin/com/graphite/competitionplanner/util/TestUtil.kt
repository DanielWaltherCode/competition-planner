package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.domain.AddCompetitionCategory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class TestUtil(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val addCompetitionCategory: AddCompetitionCategory
) {

    fun addCompetitionCategory(name: String): Int {
        val umeaId = getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = findCompetitions.thatBelongTo(umeaId)
        val umeaCompetitionId = umeaCompetitions[0].id
        val category = categoryRepository.getAvailableCategories().first { it.name == name }
        return addCompetitionCategory.execute(
            umeaCompetitionId,
            CategorySpec(category.id, category.name, category.type)
        ).id
    }

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName).id
    }
}