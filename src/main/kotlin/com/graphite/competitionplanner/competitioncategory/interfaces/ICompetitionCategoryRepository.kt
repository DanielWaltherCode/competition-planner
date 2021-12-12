package com.graphite.competitionplanner.competitioncategory.interfaces

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.common.exception.NotFoundException

interface ICompetitionCategoryRepository {

    /**
     * Return the competition category with the given ID
     *
     * @throws NotFoundException If a competition category with the given id cannot be found
     */
    @Throws(NotFoundException::class)
    fun get(competitionCategoryID: Int): CompetitionCategoryDTO

    /**
     * Return already registered categories for a given competition
     *
     * @param competitionId Id for the competition
     * @return A list of competition categories that belong to the given competition
     */
    fun getAll(competitionId: Int): List<CompetitionCategoryDTO>

    /**
     * Add a new competition category to the given competition
     *
     * @param competitionId Id for the competition
     * @param spec Competition category to store
     * @return A CompetitionCategoryDTO identical to the provided one, except that its id is updated
     */
    fun store(competitionId: Int, spec: CompetitionCategorySpec): CompetitionCategoryDTO

    /**
     * Delete the given competition category, as well as all associated general- and game settings
     *
     * @param competitionCategoryId ID of the competition category to delete
     */
    fun delete(competitionCategoryId: Int)

    /**
     * Update the given competition category
     *
     * @param spec The competition category that will be updated
     * @throws NotFoundException If a competition category that matches the dto's id cannot be found
     */
    @Throws(NotFoundException::class)
    fun update(id: Int, spec: CompetitionCategoryUpdateSpec)

    /**
     * Add a competition category to the list of available categories. If one tries to add a category with a name that
     * is identical to one that already exist, then this function does nothing.
     *
     * @param dto A new category to be added as part of available categories that can be used in a competition
     */
    fun addAvailableCategory(dto: CategoryDTO)

    /**
     * Set the state of the competition category
     */
    fun setStatus(competitionCategoryId: Int, status: CompetitionCategoryStatus)

    fun getCostForCategories(categoryIds: List<Int>): MutableMap<Int, Float>
}