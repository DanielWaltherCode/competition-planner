package com.graphite.competitionplanner.domain.interfaces

import com.graphite.competitionplanner.domain.dto.*

// TODO: Split this
interface ICompetitionCategoryRepository {

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
     * @param dto Competition category to store
     * @return A CompetitionCategoryDTO identical to the provided one, except that its id is updated
     */
    fun store(competitionId: Int, dto: CompetitionCategoryDTO): CompetitionCategoryDTO

    /**
     * Delete the given competition category, as well as all associated general- and game settings
     *
     * @param competitionCategoryId Id of the competition category to delete
     * @throws NotFoundException - If a given competition with the given id cannot be found
     */
    @Throws(NotFoundException::class)
    fun delete(competitionCategoryId: Int)

    /**
     * Update the given competition category
     *
     * @param dto The competition category that will be updated
     * @throws NotFoundException If a competition category that matches the dto's id cannot be found
     */
    @Throws(NotFoundException::class)
    fun update(dto: CompetitionCategoryDTO)

    /**
     * Add a competition category to the list of available categories. If one tries to add a category with a name that
     * is identical to one that already exist, then this function does nothing.
     *
     * @param dto A new category to be added as part of available categories that can be used in a competition
     */
    fun addAvailableCategory(dto: CategoryDTO)
}