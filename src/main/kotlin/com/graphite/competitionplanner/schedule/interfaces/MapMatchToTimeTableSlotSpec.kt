package com.graphite.competitionplanner.schedule.interfaces

/**
 * Data class that describes a new mapping between a match and a TimeTableSlot
 */
data class MapMatchToTimeTableSlotSpec(
    /**
     * ID of the match
     */
    val matchId: Int,
    /**
     * ID of the TimeTableSlot
     */
    val timeTableSlotId: Int
)