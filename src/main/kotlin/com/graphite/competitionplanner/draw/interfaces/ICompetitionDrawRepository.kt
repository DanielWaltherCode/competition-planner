package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.draw.domain.CompetitionCategoryDrawSpec

interface ICompetitionDrawRepository {

    /**
     * Store the draw of a specific competition category
     */
    fun store(draw: CompetitionCategoryDrawSpec)

    /**
     * Returns the matches from a resulting draw of a specific competition category
     *
     * @param competitionCategoryId Id of the competition category
     * @return The matches for a competition category
     */
    fun get(competitionCategoryId: Int): CompetitionCategoryDrawDTO

    /**
     *  Delete the draw for the given competition category. This will delete any matches and groups
     *
     *  @param competitionCategoryId Id of the competition category
     */
    fun delete(competitionCategoryId: Int)
}