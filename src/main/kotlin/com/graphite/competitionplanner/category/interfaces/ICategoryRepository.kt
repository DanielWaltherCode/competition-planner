package com.graphite.competitionplanner.category.interfaces

import com.graphite.competitionplanner.category.domain.CategoryType

interface ICategoryRepository {
    /**
     * Return a list of available categories for the given competition.
     *
     * If ID equals 0, the default value, the default classes (no custom classes) are returned.
     *
     * @return A list of categories that can be added to a competition
     */
    fun getAvailableCategories(competitionId: Int = 0): List<CategoryDTO>
    fun addCategory(category: String, type: CategoryType)
    fun addCustomCategory(competitionId: Int, customCategorySpec: CustomCategorySpec): CategoryDTO
    fun deleteCategory(categoryId: Int)
    fun deleteCategoriesInCompetition(competitionId: Int)
    fun updateCategory(categorySpec: CategorySpec)
}