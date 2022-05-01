package com.graphite.competitionplanner.schedule.interfaces

import com.graphite.competitionplanner.schedule.domain.*
import com.graphite.competitionplanner.schedule.service.StartInterval
import java.time.LocalDate
import java.time.LocalDateTime

interface IScheduleRepository {

    /**
     * Return all matches that have been pre-scheduled for the given date and time interval in the competition
     *
     * @param competitionId ID of competition
     * @param date Date when the competition category has been pre-scheduled
     * @param timeInterval Time interval when the competition category has been pre-scheduled
     * @return Matches for all the competition categories that has been scheduled for the given date and time interval
     */
    fun getPreScheduledMatches(competitionId: Int, date: LocalDate, timeInterval: StartInterval): List<ScheduleMatchDto>

    /**
     * Store a pre-schedule for a competition. If the given pre-schedule has already been stored nothing will change.
     * This function is idempotent i.e. calling it multiple time will have the same effect.
     *
     * @param competitionId ID of competition
     * @param competitionCategoryId ID of the competition category
     * @param spec The pre-schedule to store
     */
    fun storePreSchedule(competitionId: Int, competitionCategoryId: Int,  spec: PreScheduleSpec)

    /**
     * Get the currently stored pre-schedule for the given competition
     *
     * @param competitionId ID of the competition
     */
    fun getPreSchedule(competitionId: Int): List<CompetitionCategoryPreSchedule>

    /**
     * Update the estimated end time and success status of the competition categories
     *
     * @param competitionCategoryIds IDs of competition categories to update pre-schedule status on
     * @param estimatedEndTime Time to set on the competition categories
     * @param success Pre-schedule success status to set on the competition categories
     */
    fun update(competitionCategoryIds: List<Int>, estimatedEndTime: LocalDateTime, success: Boolean)
}