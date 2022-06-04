package com.graphite.competitionplanner.schedule.interfaces

data class TimeslotDTO(
    val orderNumber: Int,
    var matches: List<ScheduleMatchDto>
) {
    init {
        require(matches.isNotEmpty()) { "A timeslot cannot have an empty list of matches" }
    }

    var playerIds = matches.flatMap { it.firstTeamPlayerIds + it.secondTeamPlayerIds }
}