package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Schedule

data class ScheduleDTO(
    val id: Int,
    val numberOfTAbles: Int,
    val timeslots: List<TimeslotDTO>,
    val metadata: ScheduleMetaDataDTO
) {
    internal constructor(schedule: Schedule) : this(
        schedule.id,
        schedule.numberOfTables,
        schedule.timeslots.map { TimeslotDTO(it) },
        ScheduleMetaDataDTO(schedule.metadata)
    )
}