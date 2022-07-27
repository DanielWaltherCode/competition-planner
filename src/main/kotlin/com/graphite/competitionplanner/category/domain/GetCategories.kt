package com.graphite.competitionplanner.category.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import org.springframework.stereotype.Component

@Component
class GetCategories(
    val repository: ICategoryRepository
) {

    /**
     * Return a list of available categories
     */
    fun execute(competitionId: Int): List<CategoryDTO> {
        return repository.getAvailableCategories(competitionId)
    }
}