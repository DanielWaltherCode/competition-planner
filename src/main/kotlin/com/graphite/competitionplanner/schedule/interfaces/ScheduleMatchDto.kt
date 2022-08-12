package com.graphite.competitionplanner.schedule.interfaces

/**
 * The match dto from a scheduling perspective. Contains necessary information used by the scheduling algorithm.
 */
data class ScheduleMatchDto(
    /**
     * ID of the match
     */
    val id: Int,

    /**
     * ID of the competition category that the match belongs to
     */
    val competitionCategoryId: Int,

    /**
     * IDs of players in first team
     */
    val firstTeamPlayerIds: List<Int>,

    /**
     * IDs of players in second team
     */
    val secondTeamPlayerIds: List<Int>,

    /**
     * Group or Round of the match.
     */
    val groupOrRound: String
)