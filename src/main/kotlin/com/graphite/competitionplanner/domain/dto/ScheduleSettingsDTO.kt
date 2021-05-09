package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.ScheduleSettings

data class ScheduleSettingsDTO(
    val averageMatchTime: Int,
    val numberOfTables: Int
) {
    internal constructor(scheduleSettings: ScheduleSettings) : this(
        scheduleSettings.averageMatchTime,
        scheduleSettings.numberOfTables
    )
}
