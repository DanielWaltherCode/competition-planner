package com.graphite.competitionplanner.schedule.domain.interfaces

import com.graphite.competitionplanner.competitioncategory.entity.ScheduleSettings
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
