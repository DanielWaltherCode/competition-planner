package com.graphite.competitionplanner.schedule.domain.interfaces

import com.graphite.competitionplanner.schedule.domain.entity.Schedule

data class ScheduleDTO(
    val id: Int,
    val timeslots: List<TimeslotDTO>,
    val settings: ScheduleSettingsDTO
) {
    internal constructor(schedule: Schedule) : this(
        schedule.id,
        schedule.timeslots.map { TimeslotDTO(it) },
        ScheduleSettingsDTO(schedule.settings)
    )
}