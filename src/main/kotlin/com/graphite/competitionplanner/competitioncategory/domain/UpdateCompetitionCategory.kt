package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.IllegalActionException
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryUpdateSpec
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import org.springframework.stereotype.Component

@Component
class UpdateCompetitionCategory(
    val repository: ICompetitionCategoryRepository
) {

    fun execute(competitionCategoryId: Int, spec: CompetitionCategoryUpdateSpec) {
        val competitionCategory = repository.get(competitionCategoryId)
        if (competitionCategory.status == CompetitionCategoryStatus.DRAWN.toString()) {
            throw IllegalActionException("Cannot update the game settings when the category has already been drawn.")
        }
        repository.update(competitionCategoryId, spec)
    }

}