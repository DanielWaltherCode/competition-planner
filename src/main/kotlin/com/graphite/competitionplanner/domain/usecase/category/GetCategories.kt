package com.graphite.competitionplanner.domain.usecase.category

import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.interfaces.ICategoryRepository
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