package com.graphite.competitionplanner.competitioncategory.interfaces

/**
 * Represents the states that a competition category can be in
 */
enum class CompetitionCategoryStatus {
    /**
     * A competition that has just been created is considered active
     */
    ACTIVE, // Ta bort, utbytt av OPEN_FOR_REGISTRATIONS

    /**
     * The competition category is open for registrations.
     */
    OPEN_FOR_REGISTRATION,

    /**
     * The competition category is closed for registrations.
     */
    CLOSED_FOR_REGISTRATION, // TA

    /**
     * The category has been drawn
     */
    DRAWN,

    /**
     * A competition that has been canceled a head of schedule
     */
    CANCELLED
}