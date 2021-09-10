package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleDTO

internal data class Schedule(
    val id: Int,
    val timeslots: List<Timeslot>,
    val settings: ScheduleSettings
) {
    constructor(dto: ScheduleDTO) : this(
        dto.id,
        dto.timeslots.map { Timeslot(it) },
        ScheduleSettings(dto.settings)
    )
}