package com.graphite.competitionplanner.schedule.interfaces

import java.time.LocalDateTime

/**
 * Describes a new TimeTableSlot. Used when creating a new schedule
 */
data class TimeTableSlotSpec(
    /**
     * Start time of the time slot
     */
    val startTime: LocalDateTime,
    /**
     * Table number
     */
    val tableNumber: Int,
    /**
     * Location e.g. Arena A or Arena B. It is user defined
     */
    val location: String
)