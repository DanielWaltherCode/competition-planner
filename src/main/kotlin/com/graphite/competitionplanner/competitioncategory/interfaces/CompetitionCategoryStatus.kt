package com.graphite.competitionplanner.competitioncategory.interfaces

/**
 * Represents the states that a competition category can be in
 */
enum class CompetitionCategoryStatus {
    /**
     * The competition category is open for registrations.
     */
    OPEN_FOR_REGISTRATION,

    /**
     * The competition category is closed for registrations.
     */
    CLOSED_FOR_REGISTRATION,

    /**
     * The category has been drawn
     */
    DRAWN,

    /**
     * A competition that has been canceled a head of schedule
     */
    CANCELLED
}