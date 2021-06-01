package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import com.graphite.competitionplanner.util.plusDuration
import java.time.LocalDateTime
import kotlin.time.Duration

internal data class ScheduleSettings(
    val averageMatchTime: Duration,
    val numberOfTables: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) {
    init {
        require(averageMatchTime.inMinutes > 0) { "Average match time must be greater than one minute" }
        require(numberOfTables > 0) { "Number of tables must be greater than zero" }
        require(startTime.plusDuration(averageMatchTime) <= endTime)
        { "Time difference between start time and end time is to little considering the average play time per match" }
    }

    constructor(dto: ScheduleSettingsDTO) : this(dto.averageMatchTime, dto.numberOfTables, dto.startTime, dto.endTime)
}

