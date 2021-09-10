package com.graphite.competitionplanner.category.domain

import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.category.domain.interfaces.ICategoryRepository
import org.springframework.stereotype.Component

@Component
class GetCategories(
    val repository: ICategoryRepository
) {

    /**
     * Return a list of available categories
     */
    fun execute(): List<CategoryDTO> {
        return repository.getAvailableCategories()
    }
}