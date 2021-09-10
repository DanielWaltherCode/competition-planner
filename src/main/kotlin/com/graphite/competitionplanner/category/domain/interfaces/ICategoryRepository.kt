package com.graphite.competitionplanner.category.domain.interfaces

import com.graphite.competitionplanner.domain.dto.CategoryDTO

interface ICategoryRepository {
    /**
     * Return a list of available categories
     *
     * @return A list of categories that can be added to a competition
     */
    fun getAvailableCategories(): List<CategoryDTO>
}