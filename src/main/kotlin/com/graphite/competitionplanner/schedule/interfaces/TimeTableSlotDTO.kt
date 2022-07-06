package com.graphite.competitionplanner.schedule.interfaces

import java.time.LocalDateTime

/**
 * A TimeTableSlot represents a place and time were a match can take place. The place is defined by the table number
 * and location. The time is defined as the start time of the timeslot.
 */
data class TimeTableSlotDTO(
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
         * Indicates if the timeslot has been double booked i.e. two matches are scheduled at the same table at the same time
         */
        val isDoubleBooked: Boolean,

        /**
         * Basic information about the match scheduled for this time slot. If the list is empty, then no match has been
         * assigned to this TableTimeSlot.
         */
        var matchInfo: List<TimeTableSlotMatchInfo>
)

data class TimeTableSlotMatchInfo(
        /**
         * ID of the match
         */
        val id: Int,
        /**
         * ID of the competition category the match belongs to
         */
        val competitionCategoryId: Int,

        // TODO: More information?
)