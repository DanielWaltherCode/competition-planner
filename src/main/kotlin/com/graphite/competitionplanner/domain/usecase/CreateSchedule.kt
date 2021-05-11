package com.graphite.competitionplanner.domain.usecase

import com.graphite.competitionplanner.domain.dto.MatchDTO
import com.graphite.competitionplanner.domain.dto.ScheduleDTO
import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import com.graphite.competitionplanner.domain.entity.Match
import com.graphite.competitionplanner.domain.entity.Schedule
import com.graphite.competitionplanner.domain.entity.ScheduleSettings
import com.graphite.competitionplanner.domain.entity.Timeslot
import org.springframework.stereotype.Component

@Component
class CreateSchedule {
    /**
     * Given a set of matches, and a maximum number of tables that can be used at any given time. This function
     * returns a schedule for the matches. The schedule promise that:
     * - No player will be scheduled more than once per time unit
     * - Nor will more matches than there are available tables be scheduled per time unit
     *
     * This scheduler assumes that the length of each match is exactly the same, and that all tables are available during
     * all time. This leads to the schedule being divided into equally sized timeslots where the maximum number of
     * matches that can be played simultaneously is equal to the number of tables.
     */
    fun execute(matches: List<MatchDTO>, scheduleSettings: ScheduleSettingsDTO): ScheduleDTO {
        val matchesToBeScheduled = matches.map { dto -> Match(dto) }
        return ScheduleDTO(execute(matchesToBeScheduled, ScheduleSettings(scheduleSettings)))
    }

    internal fun execute(matches: List<Match>, settings: ScheduleSettings): Schedule {
        // TODO: Consider match type POOL or PLAYOFF when scheduling
        // TODO: Probably schedule all POOL games then PLAYOFF
        // TODO: Favor matches with same category ID together, probably need to sort matches before scheduling
        val schedule = createSchedule(matches, Schedule(0, listOf(), settings))
        return assignTimeInformationOnMatches(schedule)
    }

    private fun assignTimeInformationOnMatches(schedule: Schedule): Schedule {
        // TODO: Pause between matches shall be incorporated
        // TODO: End of day must be considered
        val settings = schedule.settings

        val timeslots = schedule.timeslots.map {
            Timeslot(
                it.orderNumber,
                it.matches.map { match ->
                    Match(
                        match.id,
                        match.competitionCategory,
                        settings.startTime.plusMinutes(settings.averageMatchTime * it.orderNumber),
                        settings.startTime.plusMinutes(settings.averageMatchTime * (it.orderNumber + 1)),
                        match.type,
                        match.firstPlayer,
                        match.secondPlayer,
                        match.orderNumber,
                        match.groupOrRound
                    )
                })
        }

        return Schedule(schedule.id, timeslots, settings)
    }

    private fun createSchedule(tempMatches: List<Match>, scheduleTest: Schedule): Schedule {

        var remainingMatches = tempMatches

        var schedule = scheduleTest.copy()

        while (remainingMatches.isNotEmpty()) {
            val playerPriorities = calculatePlayerPriorityBasedOn(remainingMatches)
            val matchPriorities = calculateMatchPriorityBasedOn(remainingMatches, playerPriorities)
            val highestPriority = matchPriorities.maxBy { match -> match.priority }
            schedule = scheduleMatch(schedule, highestPriority!!.match, schedule.settings.numberOfTables)
            remainingMatches = remainingMatches.filterNot { it == highestPriority.match }
        }

        return schedule
    }

    /**
     * Helper data class to keep track of a player's priority
     */
    private data class PlayerPriority(
        val playerId: Int,
        val priority: Int
    )

    /**
     * Helper data class to keep track of a match's priority
     */
    private data class MatchPriority(
        val match: Match,
        val priority: Int
    )

    /**
     * A player with more matches left to play will get a higher priority
     */
    private fun calculatePlayerPriorityBasedOn(matches: List<Match>): List<PlayerPriority> {
        val playerPriorities = matches.flatMap { match ->
            match.firstPlayer.map { PlayerPriority(it.id, 0) } +
                    match.secondPlayer.map { PlayerPriority(it.id, 0) }
        }

        var distinctPlayerPriorities = playerPriorities.distinctBy { it.playerId }

        for (p in playerPriorities) {
            distinctPlayerPriorities = distinctPlayerPriorities.map {
                if (it.playerId == p.playerId) PlayerPriority(it.playerId, it.priority + 1) else it
            }
        }
        return distinctPlayerPriorities
    }

    /**
     * Return a list of matches with their assigned priorities
     */
    private fun calculateMatchPriorityBasedOn(
        matches: List<Match>,
        playerPriorities: List<PlayerPriority>
    ): List<MatchPriority> {
        return matches.map { match -> MatchPriority(match, assignPriorityToMatch(match, playerPriorities)) }
    }

    /**
     * The priority of the match is given by the sum of the players' priorities
     */
    private fun assignPriorityToMatch(match: Match, playerPriorities: List<PlayerPriority>): Int {
        return playerPriorities.filter { playerIdsIn(match).contains(it.playerId) }.sumBy { it.priority }
    }

    private fun scheduleMatch(schedule: Schedule, match: Match, numberOfTables: Int): Schedule {
        val timeslots =
            placeMatchInFirstAvailableTimeslot(schedule.timeslots, match, numberOfTables, schedule.timeslots.size)
        return Schedule(0, timeslots, schedule.settings)
    }

    /**
     * Take a list of current timeslots, a match, and maximum number of tables, and place the match in the first
     * available timeslot where it fit. It fit in a timeslot if:
     * - there is a table available
     * - and any of the players in the match is not scheduled in that timeslot already.
     */
    private fun placeMatchInFirstAvailableTimeslot(
        timeslots: List<Timeslot>,
        match: Match,
        numberOfTables: Int,
        nextTimeSlotId: Int
    ): List<Timeslot> {
        return if (timeslots.isEmpty())
            listOf(Timeslot(nextTimeSlotId, listOf(match)))
        else {
            if (numberOfTables == timeslots.first().matches.size || registrationsInTimeslot(
                    timeslots.first(),
                    playerIdsIn(match)
                )
            ) {
                listOf(timeslots.first()) + placeMatchInFirstAvailableTimeslot(
                    timeslots.drop(1),
                    match,
                    numberOfTables,
                    nextTimeSlotId
                )
            } else {
                timeslots.first().playerIds += playerIdsIn(match)
                timeslots.first().matches += match
                timeslots
            }
        }
    }

    private fun playerIdsIn(match: Match): List<Int> {
        return match.firstPlayer.map { it.id } + match.secondPlayer.map { it.id }
    }

    /**
     * Returns True if and only if none of the newPlayerIds are already scheduled in the given timeslot
     */
    private fun registrationsInTimeslot(timeslot: Timeslot, newPlayerIds: List<Int>): Boolean {
        return !timeslot.playerIds.none { i: Int -> newPlayerIds.contains(i) }
    }
}