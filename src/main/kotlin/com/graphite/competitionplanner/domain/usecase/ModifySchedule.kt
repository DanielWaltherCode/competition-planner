package com.graphite.competitionplanner.domain.usecase

import com.graphite.competitionplanner.domain.dto.ScheduleDTO
import org.springframework.stereotype.Component

@Component
class ModifySchedule(
    val createSchedule: CreateSchedule
) {
    /**
     * Given a schedule and a new number of tables, this function will return a new schedule containing the original
     * schedule's matches, but scheduled over the new number of tables.
     */
    fun execute(schedule: ScheduleDTO, numberOfTables: Int): ScheduleDTO {
        val matches = schedule.timeslots.flatMap { it.matches }
        return createSchedule.execute(matches, numberOfTables)
    }
}