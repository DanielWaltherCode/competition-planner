package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import java.time.LocalDateTime

internal data class ScheduleSettings(
    val averageMatchTime: Long,
    val numberOfTables: Int,
    val startTime: LocalDateTime
) {
    init {
        require(averageMatchTime > 0)
        require(numberOfTables > 0)
    }

    constructor(dto: ScheduleSettingsDTO) : this(dto.averageMatchTime, dto.numberOfTables, dto.startTime)
}
