package com.graphite.competitionplanner.schedule.domain.interfaces

import com.graphite.competitionplanner.competitioncategory.entity.Timeslot

data class TimeslotDTO(
    val orderNumber: Int,
    val matches: List<MatchDTO>
) {
    internal constructor(timeslot: Timeslot) : this(timeslot.orderNumber, timeslot.matches.map { MatchDTO(it) })
}