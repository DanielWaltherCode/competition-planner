package com.graphite.competitionplanner.schedule.interfaces

import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.tables.records.MatchTimeSlotRecord
import java.time.LocalDateTime

interface IScheduleRepository {

    /**
     * Store the timetable for the given competition
     *
     * @param competitionId ID of the competition
     * @param timeTable Timetable to be stored
     */
    fun storeTimeTable(competitionId: Int, timeTable: List<TimeTableSlotSpec>)

    /**
     * Deletes the saved match time slots for a given competition
     */
    fun deleteTimeTable(competitionId: Int)

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
     * @param spec Specification for mapping a match to a TimeTableSlot
     * @return List of matches that are now booked to the same timeslots
     */
    fun addMatchToTimeTableSlot(spec: MapMatchToTimeTableSlotSpec): List<MatchToTimeTableSlot>

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
    ): List<MatchTimeSlotRecord>

    /**
     * Publish the schedule of the competition
     *
     * @param competitionId ID of the competition to publish the schedule for.
     */
    fun publishSchedule(competitionId: Int)

    /**
     * Clears the mapping between matches and TimeTableSlots for a given competition
     *
     * @param competitionId ID of the competition to clear the schedule for.
     */
    fun clearSchedule(competitionId: Int)

    /**
     * Removes category and match type in time slot table for a competition
     */
    fun resetTimeSlotsForCompetition(competitionId: Int)

    /**
     * Ensure timeslots are no longer occupied by a given competition category and matchtype
     */
    fun removeCategoryAndMatchTypeFromTimeslots(categoryId: Int, matchType: MatchType)

    /**
     * Assign timeslots to a given category
     */
    fun setCategoryForTimeSlots(timeSlotIds: List<Int>, categoryId: Int, matchType: MatchType)

    fun getTimeSlotsForCategory(categoryId: Int): List<MatchTimeSlotRecord>

    fun getTimeSlotsForCompetition(competitionId: Int): List<MatchTimeSlotRecord>

    fun clearTimeSlotTable()

    fun removeCategoryTimeSlotFromMatchTable(categoryId: Int, matchType: MatchType)
}