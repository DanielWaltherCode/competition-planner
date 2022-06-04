package com.graphite.competitionplanner.schedule.interfaces

data class ScheduleDTO(
    val id: Int,
    val timeslots: List<TimeslotDTO>,
    val settings: ScheduleSettingsDTO
)