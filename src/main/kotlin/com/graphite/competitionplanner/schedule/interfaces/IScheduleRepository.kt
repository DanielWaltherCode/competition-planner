package com.graphite.competitionplanner.schedule.interfaces

import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.schedule.domain.*
import com.graphite.competitionplanner.schedule.service.StartInterval
import com.graphite.competitionplanner.tables.records.MatchScheduleRecord
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
    fun storePreSchedule(competitionId: Int, competitionCategoryId: Int, spec: PreScheduleSpec)

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

    /**
     * Store the timetable for the given competition
     *
     * @param competitionId ID of the competition
     * @param timeTable Timetable to be stored
     */
    fun storeTimeTable(competitionId: Int, timeTable: List<TimeTableSlotSpec>)

    /**
     * Return a list of mapping from matches to TimeTableSlots
     *
     * @param competitionId ID of the competition
     * @return A list of mapping from matches to TimeTableSlots ordered by the ID of the TimeTableSlots in ascending order
     */
    fun getTimeTable(competitionId: Int): List<TimeTableSlotToMatch>

    /**
     * Add a match to the TimeTableSlot and return all matches that are now booked in that TimeTableSlot
     *
     * @param id ID of the TimeTableSlot
     * @param matchId ID of the match
     * @return List of matches that are now booked to the same timeslots
     */
    fun addMatchToTimeTableSlot(matchToTimeTableSlotSpec: MapMatchToTimeTableSlotSpec): List<MatchToTimeTableSlot>

    /**
     * Batch update the TimeTableSlots for the given matches
     *
     * @param matchTimeTableSlots A list of matches and what TimeTableSlots they should be mapped to.
     */
    fun updateMatchesTimeTablesSlots(matchTimeTableSlots: List<MapMatchToTimeTableSlotSpec>)

    /**
     * Get matches that belong to the given competition category and match type
     *
     * @param competitionCategoryId ID of the competition category
     * @param matchType Type of match
     * @return List of matches
     */
    fun getScheduleMatches(competitionCategoryId: Int, matchType: MatchType): List<ScheduleMatchDto>

    /**
     * Get the TimeTableSlot records for the given competition that matches the filter
     *
     * @param competitionId ID of the competition. Only TimeTableSlots of the competition are returned
     * @param startTime Only TimeTableSlots that has a start time greater or equal are returned
     * @param tableNumbers Only TimeTableSlots that has any of the table numbers are returned
     * @param location Only TimeTableSlots that matches the location are returned
     *
     * @return A list of TimeTableSlotsRecords that matches the filter are returned and ordered by start time and table number
     */
    fun getTimeTableSlotRecords(
        competitionId: Int,
        startTime: LocalDateTime,
        tableNumbers: List<Int>,
        location: String
    ): List<MatchScheduleRecord>
}