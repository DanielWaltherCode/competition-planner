package com.graphite.competitionplanner.schedule.interfaces

import java.time.LocalDateTime

data class TimeTableSlotToMatch (
    /**
     * ID of this time slot
     */
    val id: Int,

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
    val location: String,

    /**
     * Basic information about match. If null then it means no match has been mapped to this slot
     */
    val matchInfo: TimeTableSlotMatchInfo?
)