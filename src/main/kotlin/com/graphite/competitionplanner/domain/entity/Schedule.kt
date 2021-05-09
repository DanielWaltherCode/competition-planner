package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.ScheduleDTO

internal data class Schedule(
    val id: Int,
    val numberOfTables: Int,
    val timeslots: List<Timeslot>,
    val metadata: ScheduleMetaData
) {
    init {
        require(numberOfTables > 0) { "A schedule must have 1 or more tables" }
    }

    constructor(dto: ScheduleDTO) : this(
        dto.id,
        dto.numberOfTAbles,
        dto.timeslots.map { Timeslot(it) },
        ScheduleMetaData(dto.metadata)
    )
}