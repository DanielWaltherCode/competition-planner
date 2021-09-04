package com.graphite.competitionplanner.domain.interfaces

import com.graphite.competitionplanner.domain.dto.*

// TODO: Split this
interface ICompetitionCategoryRepository {

    /**
     * Return already registered categories for a given competition
     */
    fun getCompetitionCategoriesIn(competitionId: Int): List<CompetitionCategoryDTO>

    /**
     * Add a new competition category to the given competition
     */
    fun addCompetitionCategoryTo(competitionId: Int, dto: CompetitionCategoryDTO): CompetitionCategoryDTO

    /**
     * Delete the given competition category, as well as all associated general- and game settings
     */
    fun deleteCompetitionCategory(competitionCategoryId: Int)

    /**
     * Return a list of available categories
     */
    fun getAvailableCategories(): List<CategoryDTO>

    /**
     * Add a competition category to the list of available categories. If one tries to add a category with a name that
     * is identical to one that already exist, then this function does nothing.
     */
    fun addAvailableCategory(dto: CategoryDTO)

    /**
     * Returns the draw type with the given name
     */
    fun getDrawType(name: String): DrawTypeDTO

    /**
     * Returns the pool draw type with the given name
     */
    fun getPoolDrawStrategy(name: String): PoolDrawStrategyDTO
}