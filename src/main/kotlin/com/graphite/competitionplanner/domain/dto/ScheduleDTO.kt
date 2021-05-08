package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Schedule
import java.lang.reflect.Constructor

data class ScheduleDTO(
    val id: Int,
    val numberOfTAbles: Int,
    val timeslots: List<TimeslotDTO>
) {
    internal constructor(schedule: Schedule) : this(
        schedule.id,
        schedule.numberOfTables,
        schedule.timeslots.map { TimeslotDTO(it) })
}