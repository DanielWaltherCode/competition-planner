package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.ScheduleSettings
import java.time.LocalDateTime
import kotlin.time.Duration

data class ScheduleSettingsDTO(
    val averageMatchTime: Duration,
    val numberOfTables: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) {
    internal constructor(scheduleSettings: ScheduleSettings) : this(
        scheduleSettings.averageMatchTime,
        scheduleSettings.numberOfTables,
        scheduleSettings.startTime,
        scheduleSettings.endTime
    )
}
