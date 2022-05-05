package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class GetTimeTable(
    val repository: IScheduleRepository
) {

    fun execute(competitionId: Int): List<TimeTableSlotDto> {
        return repository.getTimeTable(competitionId)
    }
}

/**
 * A TimeTableSlot represents a place and time were a match can take place. The place is defined by the table number
 * and location, and the time is defined by the start time of the timeslot.
 */
data class TimeTableSlotDto(
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
    val isDoubleBocked: Boolean,

    /**
     * Basic information about the match scheduled for this time slot. If the list is empty, then no match has been
     * assigned to this TableTimeSlot.
     */
    var matchInfo: List<MatchInfo>
)

data class MatchInfo(
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