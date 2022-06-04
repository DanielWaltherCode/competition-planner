package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.schedule.interfaces.ScheduleDTO
import com.graphite.competitionplanner.schedule.interfaces.TimeslotDTO
import org.springframework.stereotype.Component

@Component
class ConcatSchedule {

    /**
     * Given two schedules, this function will return a new schedule where all the matches in the first schedule is
     * played before any of the matches in the second schedule. The second schedule will in effect have the IDs of
     * its timeslots relabeled starting with the ID that is one above the last timeslot in the first schedule. Also,
     * the new schedule will have ID set to 0, and number of tables will be equal to the second schedule's number of
     * tables.
     */
    fun execute(first: ScheduleDTO, second: ScheduleDTO): ScheduleDTO {
        val numberOfTimeslotsInFirst = first.timeslots.size
        val relabeledTimeslots = second.timeslots.map {
            TimeslotDTO(it.orderNumber + numberOfTimeslotsInFirst, it.matches)
        }
        return ScheduleDTO(0, first.timeslots + (relabeledTimeslots), first.settings)
    }
}