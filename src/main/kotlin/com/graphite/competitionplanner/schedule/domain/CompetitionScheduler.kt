package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.schedule.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.interfaces.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.Duration

/**
 * Class that provides methods to schedule matches within the same competition.
 */
@Component
class CompetitionScheduler(
    val repository: IScheduleRepository,
    val createSchedule: CreateSchedule
) {

    /**
     * Maps a match to a specific TimeTableSlot. This is more of a utility function were an administrator
     * can easily move one match to a new TimeTableSlot.
     */
    fun mapMatchToTimeTableSlot(matchToTimeTableSlot: MapMatchToTimeTableSlotSpec): TimeTableSlotDto {
        val matchesInSameSlot = repository.addMatchToTimeTableSlot(matchToTimeTableSlot)

        return TimeTableSlotDto(
            matchesInSameSlot.first().timeTableSlotId,
            matchesInSameSlot.first().startTime,
            matchesInSameSlot.first().tableNumber,
            matchesInSameSlot.first().location,
            matchesInSameSlot.size > 1,
            matchesInSameSlot.map {
                TimeTableSlotMatchInfo(
                    it.matchId,
                    it.competitionCategoryId
                )
            }
        )
    }

    /**
     * Updates the TimeTableSlots for multiple matches.
     *
     * @param matchToTimeTableSlots Mapping from matches to TimeTableSlots
     */
    fun addMultipleMatchToTimeTableSlot(matchToTimeTableSlots: List<MapMatchToTimeTableSlotSpec>) {
        repository.updateMatchesTimeTablesSlots(matchToTimeTableSlots)
    }

    /**
     * This will publish the schedule.
     *
     * DEVELOPER NOTE: Internally it means that we copy time-information from the TimeTableSlots db-table to the
     * match db-table. This will allow a user to continue editing a schedule without changing the already published
     * schedule. Almost like you save a draft.
     */
    fun publishSchedule(competitionId: Int) {
        repository.publishSchedule(competitionId)
    }

    /**
     * Return the entire schedule for the competition
     *
     * @param competitionId ID of the competition
     * @return List of TimeTableSlots sorted by location, time and table in ascending order
     */
    fun getSchedule(competitionId: Int): List<TimeTableSlotDto> {
        val matchesToSlots = repository.getTimeTable(competitionId)
        val schedule = mergeTimeTableSlots(matchesToSlots)
        return schedule.sortedWith(compareBy(TimeTableSlotDto::location, TimeTableSlotDto::startTime, TimeTableSlotDto::tableNumber))
    }

    private fun mergeTimeTableSlots(list: List<TimeTableSlotToMatch>): List<TimeTableSlotDto> {
        if (list.isEmpty()) {
            return emptyList()
        } else {
            val id = list.first().id
            val (sameTimeSlots, otherTimeSlots) = list.partition { it.id == id }

            val matchInfos = sameTimeSlots.mapNotNull { it.matchInfo }
            val timeTableSlot = TimeTableSlotDto(
                sameTimeSlots.first().id,
                sameTimeSlots.first().startTime,
                sameTimeSlots.first().tableNumber,
                sameTimeSlots.first().location,
                sameTimeSlots.size > 1,
                matchInfos
            )
            return listOf(timeTableSlot) + mergeTimeTableSlots(otherTimeSlots)
        }
    }

    /**
     * Schedules all matches of a specific type belonging to a competition category from the given start time on
     * the selected tables and location
     *
     * NOTE: This function does not guarantee that a player won't be double booked in the same timeslot which can
     * happen if a player in the given competition category also participate in another competition category within
     * the same competition.
     *
     * @param competitionId ID of the competition
     * @param competitionCategoryId ID of the competition category whose matches will be scheduled
     * @param matchType The type of matches in the given category to be scheduled
     * @param tables Table numbers that the matches will be scheduled at
     * @param startTime The first matches will be scheduled here
     * @param location The location the matches will be scheduled at
     */
    fun scheduleCompetitionCategory(
        competitionId: Int,
        competitionCategoryId: Int,
        matchType: MatchType,
        tables: List<Int>,
        startTime: LocalDateTime,
        location: String
    ) {
        val matches = repository.getScheduleMatches(competitionCategoryId, matchType)
        val settings = ScheduleSettingsDTO(
            Duration.minutes(15), // Not used
            tables.size,
            LocalDateTime.now(), // Not used
            LocalDateTime.now().plusMinutes(60) // Not used
        )
        val schedule = createSchedule.execute(matches, settings)
        val timeTableSlots = repository.getTimeTableSlotRecords(competitionId, startTime, tables, location)

        try {
            val updateSpec = mutableListOf<MapMatchToTimeTableSlotSpec>()
            var index = 0
            for (timeslot in schedule.timeslots) {
                for (match in timeslot.matches) {
                    updateSpec.add(MapMatchToTimeTableSlotSpec(match.id, timeTableSlots[index].id))
                    index++
                }
            }
            repository.updateMatchesTimeTablesSlots(updateSpec)
        } catch (ex: IndexOutOfBoundsException) {
            throw IndexOutOfBoundsException("Not all matches fit the schedule. Please consider adding more tables or start earlier.")
        }
    }

    /**
     * Schedules all matches of a specific type belonging to a competition category in such a way that matches are
     * scheduled as early as possible on the given tables.
     *
     * This method will prevent two matches from being booked on the same TimeTableSlot but will throw exception if
     * not all matches can fit in the schedule
     *
     * NOTE: This function does not guarantee that a player won't be double booked in the same timeslot which can
     * happen if a player in the given competition category also participate in another competition category within
     * the same competition.
     *
     * @param competitionId ID of the competition
     * @param competitionCategoryId ID of the competition category whose matches will be scheduled
     * @param matchType The type of matches in the given category to be scheduled
     * @param tables Table numbers that the matches will be scheduled at
     * @param location The location the matches will be scheduled at
     * @throws IndexOutOfBoundsException When not all matches can be scheduled on the given tables
     */
    fun appendMatchesToTables(
        competitionId: Int,
        competitionCategoryId: Int,
        matchType: MatchType,
        tables: List<Int>,
        location: String
    ) {
        val matches = repository.getScheduleMatches(competitionCategoryId, matchType)
        val settings = ScheduleSettingsDTO(
            Duration.minutes(15), // Not used
            tables.size,
            LocalDateTime.now(), // Not used
            LocalDateTime.now().plusMinutes(60) // Not used
        )

        val currentSchedule = getSchedule(competitionId)
        val blocks = getScheduleBlocks(currentSchedule.filter { tables.contains(it.tableNumber) && it.location == location })
        val updateSpecs = createUpdateSpecs(settings, blocks, matches)

        repository.updateMatchesTimeTablesSlots(updateSpecs)
    }

    private fun createUpdateSpecs(settings: ScheduleSettingsDTO, blocks: List<ScheduleBlock>, matches: List<ScheduleMatchDto>): List<MapMatchToTimeTableSlotSpec> {
        return if (blocks.isEmpty()) {
            if (matches.isNotEmpty()) {
                throw IndexOutOfBoundsException("Not all matches fit the schedule. Please consider adding more tables or start earlier.")
            }
            emptyList()
        } else {
            val block = blocks.first()
            val (schedule, remaining) = createSchedule.execute(matches, settings, block.limit)
            val updateSpec = mutableListOf<MapMatchToTimeTableSlotSpec>()
            var index = 0
            for (timeslot in schedule.timeslots) {
                for (match in timeslot.matches) {
                    updateSpec.add(MapMatchToTimeTableSlotSpec(match.id, block.timeTableSlots[index].id))
                    index++
                }
                index = block.numberOfTables
            }

            updateSpec + createUpdateSpecs(settings, blocks.drop(1), remaining)
        }
    }

    private fun getScheduleBlocks(tableSlots: List<TimeTableSlotDto>): List<ScheduleBlock> {
        val groupedByTime = tableSlots.groupBy { it.startTime }
            .map { (startTime, slots) -> Pair(startTime, slots.filter { slot -> slot.matchInfo.isEmpty() }) }
        return mergeRowsToBlocks(groupedByTime)
    }

    private fun mergeRowsToBlocks(groupedByTime: List<Pair<LocalDateTime, List<TimeTableSlotDto>>>): List<ScheduleBlock> {
        return if (groupedByTime.isEmpty()) {
            emptyList()
        } else {
            val numberOfTables = groupedByTime.first().second.size
            val (sameSize, other) = groupedByTime.partition { it.second.size == numberOfTables }

            listOf(ScheduleBlock(sameSize.size, numberOfTables, sameSize.flatMap { it.second })) + mergeRowsToBlocks(other)
        }
    }

    private data class ScheduleBlock(
        val limit: Int,
        val numberOfTables: Int,
        val timeTableSlots: List<TimeTableSlotDto>
    )

    fun clearSchedule(competitionId: Int) {
        repository.clearSchedule(competitionId)
    }
}
