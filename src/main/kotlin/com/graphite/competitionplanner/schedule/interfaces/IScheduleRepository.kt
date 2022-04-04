package com.graphite.competitionplanner.schedule.interfaces

import com.graphite.competitionplanner.schedule.domain.ScheduleMatchDto
import com.graphite.competitionplanner.schedule.domain.TimeInterval
import java.time.LocalDate

interface IScheduleRepository {

    /**
     * Return all matches that have been pre-scheduled for the given date and time interval in the competition
     *
     * @param competitionId ID of competition
     * @param date Date when the competition category has been pre-scheduled
     * @param timeInterval Time interval when the competition category has been pre-scheduled
     * @return Matches for all the competition categories that has been scheduled for the given date and time interval
     */
    fun getMatchesIn(competitionId: Int, date: LocalDate, timeInterval: TimeInterval): List<ScheduleMatchDto>
}