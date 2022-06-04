package com.graphite.competitionplanner.schedule.interfaces

import com.graphite.competitionplanner.util.plusDuration
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class ScheduleSettingsDTO(
    val averageMatchTime: Duration,
    val numberOfTables: Int,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
) {
    init {
        require(averageMatchTime.toDouble(DurationUnit.MINUTES) > 0) { "Average match time must be greater than one minute" }
        require(numberOfTables > 0) { "Number of tables must be greater than zero" }
        require(startTime.plusDuration(averageMatchTime) <= endTime)
        { "Time difference between start time and end time is to little considering the average play time per match" }
    }
}
