package com.graphite.competitionplanner.competitioncategory.interfaces

/**
 * Represents the states that a competition category can be in
 */
enum class CompetitionCategoryStatus {
    /**
     * A competition that has just been created is considered active
     */
    ACTIVE,

    /**
     * The category has been drawn
     */
    DRAWN,

    /**
     * A competition that has been canceled a head of schedule
     */
    CANCELLED
}