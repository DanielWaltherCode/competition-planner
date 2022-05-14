package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.schedule.domain.interfaces.MatchDTO
import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotMatchInfo
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotDto
import com.graphite.competitionplanner.schedule.interfaces.UpdateMatchToTimeTableSlotSpec
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.Duration

/**
 * Class that can map matches to TimeTableSlots
 */
@Component
class MatchScheduler( // TODO: Come up with a better name. CompetitionScheduler?
    val repository: IScheduleRepository,
    val createSchedule: CreateSchedule
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Maps a match to a specific TimeTableSlot. This is more of a utility function were an administrator
     * can easily move one match to a new TimeTableSlot.
     */
    fun addMatchToTimeTableSlot(tableSlotId: Int, matchId: Int): TimeTableSlotDto {
        val matchesInSameSlot = repository.addMatchToTimeTableSlot(tableSlotId, matchId)

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
    fun addMultipleMatchToTimeTableSlot(matchToTimeTableSlots: List<UpdateMatchToTimeTableSlotSpec>) {
        repository.updateMatchesTimeTablesSlots(matchToTimeTableSlots)
    }

    /**
     * This will publish the schedule.
     *
     * DEVELOPER NOTE: Internally it means that we copy time-information from the TimeTableSlots db-table to the
     * match db-table. This will allow a user to continue editing a schedule without changing the already published
     * schedule. Almost like you save a draft.
     */
    fun publishSchedule() {
        // TODO: Implement
        // repository.publishSchedule()
    }

    /**
     * Return the entire schedule for the competition
     *
     * @param competitionId ID of the competition
     * @return List of TimeTableSlots sorted by location, time and table in ascending order
     */
    fun get(competitionId: Int): List<TimeTableSlotDto> {
        // TODO: Sort and post-process
        val matchesToSlots = repository.getTimeTable(competitionId)
        return emptyList()
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
        val matchDtos = matches.map { it.toMatchDTO() } // Map for compatability
        val settings = ScheduleSettingsDTO(
            Duration.minutes(15), // Not used
            tables.size,
            LocalDateTime.now(), // Not used
            LocalDateTime.now() // Not used
        )
        val schedule = createSchedule.execute(matchDtos, settings)
        val timeTableSlots = repository.getTimeTableSlotRecords(competitionId, startTime, tables, location)

        // TODO: Check that we get enough timeTableSlots back to contain all matches. Otherwise potential index error
        // TODO: For now. Catch potential IndexOutOfBounds error and log it
        try {
            val updateSpec = mutableListOf<UpdateMatchToTimeTableSlotSpec>()
            var index = 0
            for (timeslot in schedule.timeslots) {
                for (match in timeslot.matches) {
                    updateSpec.add(UpdateMatchToTimeTableSlotSpec(match.id, timeTableSlots[index].id))
                    index++
                }
            }
            repository.updateMatchesTimeTablesSlots(updateSpec)
        } catch (ex: IndexOutOfBoundsException) {
            logger.error("Encountered error when scheduling for competition $competitionId and competition category $competitionCategoryId")
            logger.error("Input parameters: MatchType = $matchType, TableNumbers = ${tables.joinToString { "," }}, StartTime = $startTime, Location = $location")
        }
    }

    fun ScheduleMatchDto.toMatchDTO(): MatchDTO {
        return MatchDTO(
            this.id,
            null,
            null,
            this.competitionCategoryId,
            "POOL", // Not used
            this.firstTeamPlayerIds,
            this.secondTeamPlayerIds,
            0, // Not used
            "A" // Not used
        )
    }
}

