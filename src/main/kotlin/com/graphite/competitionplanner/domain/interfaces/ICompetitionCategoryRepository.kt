package com.graphite.competitionplanner.domain.interfaces

import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO

interface ICompetitionCategoryRepository {

    /**
     * Return already registered categories for a given competition
     */
    fun getCompetitionCategoriesIn(competitionId: Int): List<CompetitionCategoryDTO>

    /**
     * Add a category to the given competition
     */
    fun addCompetitionCategoryTo(competitionId: Int, dto: CategoryDTO)

    /**
     * Delete the given competition category
     */
    fun deleteCompetitionCategoryFrom(competitionCategoryId: Int)

    /**
     * Return a list of available categories
     */
    fun getAvailableCategories(): List<CategoryDTO>

    /**
     * Add a competition category to the list of available categories. If one tries to add a category with a name that
     * is identical to one that already exist, then this function does nothing.
     */
    fun addAvailableCategory(dto: CategoryDTO)
}