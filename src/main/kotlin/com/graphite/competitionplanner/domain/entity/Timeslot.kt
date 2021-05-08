package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.dto.TimeslotDTO

internal class Timeslot(
    val orderNumber: Int,
    var matches: List<Match>
) {

    init {
        require(matches.isNotEmpty()) { "A timeslot cannot have an empty list of matches" }
    }

    var playerIds = matches.flatMap { it.firstPlayer.map { p -> p.id } + it.secondPlayer.map { p -> p.id } }

    constructor(dto: TimeslotDTO) : this(
        dto.orderNumber,
        dto.matches.map { Match(it) })
}