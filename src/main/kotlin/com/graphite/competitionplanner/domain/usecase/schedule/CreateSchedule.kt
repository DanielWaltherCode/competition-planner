package com.graphite.competitionplanner.domain.usecase.schedule

import com.graphite.competitionplanner.domain.dto.MatchDTO
import com.graphite.competitionplanner.domain.dto.ScheduleDTO
import com.graphite.competitionplanner.domain.dto.ScheduleSettingsDTO
import com.graphite.competitionplanner.domain.entity.*
import com.graphite.competitionplanner.domain.entity.Match
import com.graphite.competitionplanner.domain.entity.Schedule
import com.graphite.competitionplanner.domain.entity.ScheduleSettings
import com.graphite.competitionplanner.domain.entity.Timeslot
import com.graphite.competitionplanner.util.plusDuration
import org.springframework.stereotype.Component
import java.time.temporal.ChronoUnit
import kotlin.time.DurationUnit

@Component
class CreateSchedule {
    /**
     * Given a set of matches, and a maximum number of tables that can be used at any given time. This function
     * returns a schedule for the matches. The schedule promise that:
     * - No player will be scheduled more than once per time unit
     * - Nor will more matches than there are available tables be scheduled per time unit
     * - Matches from different categories will scheduled in a round robin fashion so that each category gets an
     * equal chance to be scheduled in any given time unit.
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
        val schedule = createSchedule(matches, Schedule(0, emptyList(), settings))
        return assignTimeInformationOnMatches(schedule)
    }

    private fun assignTimeInformationOnMatches(schedule: Schedule): Schedule {
        // TODO: Pause between matches shall be incorporated
        val settings = schedule.settings

        val timeslots = schedule.timeslots.map { it.assignTimeToMatches(schedule.settings) }

        return Schedule(schedule.id, timeslots, settings)
    }

    private fun Timeslot.assignTimeToMatches(settings: ScheduleSettings): Timeslot {
        val dayInMinutes = settings.startTime.until(settings.endTime, ChronoUnit.MINUTES)
        val timeslotsPerDay = dayInMinutes / settings.averageMatchTime.toLong(DurationUnit.MINUTES)
        val timeslotOrderForTheDay = this.orderNumber % timeslotsPerDay
        val dayOfTimeslot = this.orderNumber / timeslotsPerDay
        val matchStart = settings.startTime.plusDays(dayOfTimeslot)
            .plusDuration(settings.averageMatchTime * timeslotOrderForTheDay.toInt())
        val matchEnd = settings.startTime.plusDays(dayOfTimeslot)
            .plusDuration(settings.averageMatchTime * (timeslotOrderForTheDay + 1).toInt())

        return Timeslot(
            this.orderNumber,
            this.matches.map { match -> Match(match, matchStart, matchEnd) }
        )
    }

    private fun createSchedule(tempMatches: List<Match>, scheduleTest: Schedule): Schedule {

        var remainingMatches = tempMatches

        var schedule = scheduleTest.copy()

        while (remainingMatches.isNotEmpty()) {
            for (category in remainingMatches.groupByCategory()) { // Round robin per category
                // Since we consider players from all remaining matches (i.e all categories),
                // players that belongs to two categories will essentially have a higher priority
                val playerPriorities = calculatePlayerPriorityBasedOn(remainingMatches)
                val matchPriorities = calculateMatchPriorityBasedOn(category.matches, playerPriorities)
                val highestPriority = matchPriorities.maxBy { match -> match.priority }
                schedule = schedule.add(highestPriority!!.match)
                remainingMatches = remainingMatches.filterNot { it.id == highestPriority.match.id }
            }
        }

        return schedule
    }

    /**
     * Helper data structure that group all matches belong to same category
     */
    private data class MatchesGroupedOnCategory(
        val category: CompetitionCategory,
        var matches: List<Match>
    )

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
        val playerIds = matches.flatMap { it.playerIds() }
        val distinctPlayerIds = playerIds.distinct()
        return distinctPlayerIds.map { distinctId ->
            PlayerPriority(
                distinctId,
                playerIds.count { id -> distinctId == id })
        }
    }

    /**
     * Return a list of matches with their assigned priorities
     */
    private fun calculateMatchPriorityBasedOn(
        matches: List<Match>,
        playerPriorities: List<PlayerPriority>
    ): List<MatchPriority> {
        with(playerPriorities) {
            return matches.map { match -> MatchPriority(match, calculatePriorityFor(match)) }
        }
    }

    private fun List<PlayerPriority>.calculatePriorityFor(match: Match): Int {
        return this.filter { match.playerIds().contains(it.playerId) }.sumBy { it.priority }
    }

    /**
     * Returns a new schedule that contains the given match
     */
    private fun Schedule.add(match: Match): Schedule {
        val timeslots =
            placeMatchInFirstAvailableTimeslot(this.timeslots, match, this.settings.numberOfTables, this.timeslots.size)
        return Schedule(0, timeslots, this.settings)
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
            if (numberOfTables == timeslots.first().matches.size || timeslots.first().contains(match.playerIds())
            ) {
                listOf(timeslots.first()) + placeMatchInFirstAvailableTimeslot(
                    timeslots.drop(1),
                    match,
                    numberOfTables,
                    nextTimeSlotId
                )
            } else {
                timeslots.first().playerIds += match.playerIds()
                timeslots.first().matches += match
                timeslots
            }
        }
    }

    /**
     * Return a list of player ids that belongs to this match
     */
    private fun Match.playerIds(): List<Int> {
        return this.firstPlayer.map { it.id } + this.secondPlayer.map { it.id }
    }

    /**
     * Returns true if and only if the timeslot contains any of the new player ids
     */
    private fun Timeslot.contains(newPlayerIds: List<Int>): Boolean {
        return !this.playerIds.none { i: Int -> newPlayerIds.contains(i) }
    }

    /**
     * Returns a list of matches grouped by category
     */
    private fun List<Match>.groupByCategory(): List<MatchesGroupedOnCategory> {
        val categories = this.map { it.competitionCategory }.distinct()
        return categories.map { MatchesGroupedOnCategory(it, this.filter { match -> match.competitionCategory == it }) }
    }
}
