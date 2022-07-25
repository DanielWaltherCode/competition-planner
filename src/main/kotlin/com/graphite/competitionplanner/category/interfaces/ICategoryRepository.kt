package com.graphite.competitionplanner.category.interfaces

import com.graphite.competitionplanner.category.api.CustomCategorySpec

interface ICategoryRepository {
    /**
     * Return a list of available categories
     *
     * @return A list of categories that can be added to a competition
     */
    fun getAvailableCategories(competitionId: Int): List<CategoryDTO>
    fun addCategory(category: String, type: CategoryType)
    fun addCustomCategory(competitionId: Int, customCategorySpec: CustomCategorySpec): CategoryDTO
    fun deleteCategory(categoryId: Int)
    fun deleteCategoriesInCompetition(competitionId: Int)
    fun updateCategory(categorySpec: CategorySpec)
}