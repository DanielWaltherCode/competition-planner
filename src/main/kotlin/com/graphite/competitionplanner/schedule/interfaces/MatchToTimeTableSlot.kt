package com.graphite.competitionplanner.schedule.interfaces

import java.time.LocalDateTime

/**
 * Includes information about match and what TimeTableSlot it is mapped to
 */
data class MatchToTimeTableSlot(
    /**
     * ID of match
     */
    val matchId: Int,
    /**
     * ID of the competition category the match belongs to
     */
    val competitionCategoryId: Int,
    /**
     * ID of the TimeTableSlot that the match is mapped to
     */
    val timeTableSlotId: Int,
    /**
     * The start time of the TimeTableSlot
     */
    val startTime: LocalDateTime,
    /**
     * The table number of the TimeTableSlot
     */
    val tableNumber: Int,
    /**
     * The location of the TimeTableSlot
     */
    val location: String,
)