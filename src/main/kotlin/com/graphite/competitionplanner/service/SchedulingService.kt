package com.graphite.competitionplanner.service

import org.springframework.stereotype.Service

@Service
class SchedulingService {

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
    fun createSchedule(tempMatches: List<TempMatch>, numberOfTables: Int) : Schedule {

        if (numberOfTables < 1){
            throw IllegalArgumentException("numberOfTables has to be greater or equal to 1")
        }

        val existInvalidMatchSetup = tempMatches
                .map { it.playerIds.distinct().size == it.playerIds.size }
                .filterNot { it }
                .isNotEmpty()

        if (existInvalidMatchSetup){
            throw IllegalArgumentException("There exists at least one match where a player is on both teams")
        }

        var schedule = Schedule(listOf())
        var remainingMatches = tempMatches

        while (remainingMatches.isNotEmpty()) {
            val playerPriorities = createPlayerPriorityList(remainingMatches)
            val matchPriorities = createMatchPriorityList(remainingMatches, playerPriorities)
            val highestPriority = matchPriorities.maxBy { match -> match.priority }
            schedule = scheduleMatch(schedule, highestPriority!!.match, numberOfTables)
            remainingMatches = remainingMatches.filterNot { it == highestPriority.match }
        }

        return schedule
    }

    data class PlayerPriority(
            val playerId: Int,
            val priority: Int
    )

    data class MatchPriority(
            val match: TempMatch,
            val priority: Int
    )

    /**
     * A player
     */
    private fun createPlayerPriorityList(matches: List<TempMatch>) : List<PlayerPriority> {

        val playerPriorities = matches.flatMap { match -> match.playerIds.map { PlayerPriority(it, 0) } }
        var distinctPlayerPriorities = playerPriorities.distinctBy { it.playerId }

        for (p in playerPriorities) {
            distinctPlayerPriorities = distinctPlayerPriorities.map {
                if (it.playerId == p.playerId) PlayerPriority(it.playerId, it.priority + 1) else it  }
        }
        return distinctPlayerPriorities
    }

    /**
     * Return a list of matches with their assigned priorites
     */
    private fun createMatchPriorityList(matches: List<TempMatch>, playerPriorities: List<PlayerPriority>) : List<MatchPriority> {
        return matches.map { match -> MatchPriority(match, assignPriorityToMatch(match, playerPriorities))}
    }

    /**
     * The priority of the match is given by the sum of the players' priorities
     */
    private fun assignPriorityToMatch(match: TempMatch, playerPriorities: List<PlayerPriority>) : Int {
       return playerPriorities.filter { match.playerIds.contains(it.playerId) }.sumBy { it.priority }
    }

    private fun scheduleMatch(schedule: Schedule, match: TempMatch, numberOfTables: Int): Schedule {
        val timeslots = placeMatchInFirstAvailableTimeslot(schedule.timeslots, match, numberOfTables)
        return Schedule(timeslots)
    }

    /**
     * Take a list of current timeslots, a match, and maximum number of tables, and place the match in the first
     * available timeslot where it fit. It fit in a timeslot if:
     * - there is a table available
     * - and any of the players in the match is not scheduled in that timeslot already.
     */
    private fun placeMatchInFirstAvailableTimeslot(timeslots: List<Timeslot>, match: TempMatch, numberOfTables: Int) : List<Timeslot> {
        if(timeslots.isEmpty())
            return listOf(Timeslot(0, match.playerIds, listOf(match)))
        else {
            if (numberOfTables ==  timeslots.last().matches.size){
                return listOf(timeslots.last()) + placeMatchInFirstAvailableTimeslot(timeslots.dropLast(1), match, numberOfTables)
            }
            if (registrationNotInTimeslot(timeslots.last(), match.playerIds)) {
                timeslots.last().playerIds += match.playerIds
                timeslots.last().matches += match
                return timeslots
            } else {
                return listOf(timeslots.last()) + placeMatchInFirstAvailableTimeslot(timeslots.dropLast(1), match, numberOfTables)
            }
        }
    }

    /**
     * Returns True if and only if none of the newPlayerIds are already scheduled in the timeslot
     */
    private fun registrationNotInTimeslot (timeslot: Timeslot, newPlayerIds: List<Int>): Boolean{
        return timeslot.playerIds.none { i: Int -> newPlayerIds.contains(i) }
    }

    data class TempMatch (
            val playerIds: List<Int>
    )

    data class Schedule (
            val timeslots: List<Timeslot>
    )

    data class Timeslot (
            val timeslot: Int,
            var playerIds: List<Int>,
            var matches: List<TempMatch>
    )
}