package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.ScheduleSettings
import java.time.LocalDateTime

data class ScheduleSettingsDTO(
    val averageMatchTime: Long,
    val numberOfTables: Int,
    val startTime: LocalDateTime
) {
    internal constructor(scheduleSettings: ScheduleSettings) : this(
        scheduleSettings.averageMatchTime,
        scheduleSettings.numberOfTables,
        scheduleSettings.startTime
    )
}
