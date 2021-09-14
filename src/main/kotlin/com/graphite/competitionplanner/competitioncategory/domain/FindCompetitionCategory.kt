package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FindCompetitionCategory(
    @Autowired val repository: ICompetitionCategoryRepository
) {

    /**
     * Return the competition category with the given id
     *
     * @throws NotFoundException When the competition category with the given id cannot be found
     */
    @Throws(NotFoundException::class)
    fun byId(competitionCategoryId: Int): CompetitionCategoryDTO {
        return repository.get(competitionCategoryId)
    }
}