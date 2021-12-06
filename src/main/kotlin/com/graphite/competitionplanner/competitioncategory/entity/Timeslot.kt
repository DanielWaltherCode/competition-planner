package com.graphite.competitionplanner.competitioncategory.entity

import com.graphite.competitionplanner.schedule.domain.interfaces.TimeslotDTO

internal class Timeslot(
    val orderNumber: Int,
    var matches: List<Match>
) {
    init {
        require(matches.isNotEmpty()) { "A timeslot cannot have an empty list of matches" }
    }

    var playerIds = matches.flatMap { it.firstTeamPlayerIds + it.secondTeamPlayerIds }

    constructor(dto: TimeslotDTO) : this(
        dto.orderNumber,
        dto.matches.map { Match(it) })
}