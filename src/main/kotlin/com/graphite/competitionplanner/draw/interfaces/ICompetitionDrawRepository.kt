package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.draw.domain.CompetitionCategoryDrawDTO

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
}