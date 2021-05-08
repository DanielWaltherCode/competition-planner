package com.graphite.competitionplanner.domain.dto

import com.graphite.competitionplanner.domain.entity.Timeslot

data class TimeslotDTO(
    val orderNumber: Int,
    var matches: List<MatchDTO>
) {
    internal constructor(timeslot: Timeslot) : this(timeslot.orderNumber, timeslot.matches.map { MatchDTO(it) })
}