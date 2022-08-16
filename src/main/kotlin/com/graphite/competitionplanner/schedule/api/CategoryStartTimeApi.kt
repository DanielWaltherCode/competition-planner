package com.graphite.competitionplanner.schedule.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.schedule.service.StartInterval
import java.time.LocalDate
import java.time.LocalTime

data class CategoryStartTimeSpec(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playingDay: LocalDate?,
    val startInterval: StartInterval?,
    @JsonFormat(pattern = "HH:mm")
    val exactStartTime: LocalTime?
)