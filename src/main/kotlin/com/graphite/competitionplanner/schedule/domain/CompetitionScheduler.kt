package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.BadRequestType
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.match.domain.MatchType
import com.graphite.competitionplanner.schedule.api.ScheduleCategoryContainerDTO
import com.graphite.competitionplanner.schedule.api.ScheduleCategoryDTO
import com.graphite.competitionplanner.schedule.interfaces.*
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Class that provides methods to schedule matches within the same competition.
 */
@Component
class CompetitionScheduler(
        val scheduleRepository: IScheduleRepository,
        val createSchedule: CreateSchedule,
        val findCompetitions: FindCompetitions,
        val availableTablesService: AvailableTablesService,
        val getCompetitionCategories: GetCompetitionCategories
) {

    /**
     * Maps a match to a specific TimeTableSlot. This is more of a utility function where an administrator
     * can easily move one match to a new TimeTableSlot.
     */
    fun mapMatchToTimeTableSlot(matchToTimeTableSlot: MapMatchToTimeTableSlotSpec): TimeTableSlotDTO {
        val matchesInSameSlot = scheduleRepository.addMatchToTimeTableSlot(matchToTimeTableSlot)

        return TimeTableSlotDTO(
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
     * This will publish the schedule.
     *
     * DEVELOPER NOTE: Internally it means that we copy time-information from the TimeTableSlots db-table to the
     * match db-table. This will allow a user to continue editing a schedule without changing the already published
     * schedule. Almost like you save a draft.
     */
    fun publishSchedule(competitionId: Int) {
        scheduleRepository.publishSchedule(competitionId)
    }

    /**
     * Return the entire schedule for the competition
     *
     * @param competitionId ID of the competition
     * @return List of TimeTableSlots sorted by location, time and table in ascending order
     */
    fun getSchedule(competitionId: Int): List<TimeTableSlotDTO> {
        val matchesToSlots = scheduleRepository.getTimeTable(competitionId)
        val schedule = mergeTimeTableSlots(matchesToSlots)
        return schedule.sortedWith(
                compareBy(TimeTableSlotDTO::location, TimeTableSlotDTO::startTime, TimeTableSlotDTO::tableNumber))
    }

    fun getScheduleForFrontend(competitionId: Int): ExcelScheduleDTOContainer {
        val timeSlots = scheduleRepository.getTimeSlotsForCompetition(competitionId)
        val competitionCategories = getCompetitionCategories.execute(competitionId)
        val playingDays = timeSlots.map { it.startTime.toLocalDate() }.distinct()


        val excelScheduleList = mutableListOf<ExcelScheduleDTO>()

        for (day in playingDays) {
            val excelScheduleItemList = mutableListOf<ExcelScheduleItemDTO>()
            val filteredTimeSlots = timeSlots.filter { it.startTime.toLocalDate().equals(day) }
            val distinctTables = filteredTimeSlots.map { it.tableNumber }.distinct().sorted()
            val validStartTimes = filteredTimeSlots.map { it.startTime }.distinct().sorted().toSet()
            if (filteredTimeSlots.map { it.competitionCategoryId }.isEmpty()) {
                excelScheduleList.add(
                        ExcelScheduleDTO(
                                excelScheduleItemList,
                                validStartTimes,
                                emptySet(),
                                day
                        )
                )
                continue
            }
            val distinctCategories = filteredTimeSlots
                    .map { it.competitionCategoryId }
                    .distinct()
                    .filterNotNull()
                    .map { getCategoryDTO(competitionCategories, it) }
                    .toSet()

            for (table in distinctTables) {
                val matchesOnTable = filteredTimeSlots.filter { it.tableNumber == table && it.competitionCategoryId != null }
                val excelMatchDTOs = matchesOnTable.map {
                    ExcelScheduleMatchDTO(
                            it.startTime,
                            getCategoryDTO(competitionCategories, it.competitionCategoryId),
                            it.matchType)
                }
                excelScheduleItemList.add(ExcelScheduleItemDTO(table, excelMatchDTOs))
            }
            excelScheduleList.add(
                    ExcelScheduleDTO(
                            excelScheduleItemList,
                            validStartTimes,
                            distinctCategories,
                            day
                    )
            )
        }

        return ExcelScheduleDTOContainer(excelScheduleList)
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
     * @param matchSchedulerSpec Specification of how the scheduling should be done
     */
    fun scheduleCompetitionCategory(
            competitionId: Int,
            competitionCategoryId: Int,
            matchSchedulerSpec: MatchSchedulerSpec
    ) {
        val competition = findCompetitions.byId(competitionId)
        removeTimeSlotCategory(competitionCategoryId, matchSchedulerSpec.matchType)

        val matches = scheduleRepository.getScheduleMatches(competitionCategoryId, matchSchedulerSpec.matchType)

        val dateTimeFilter = LocalDateTime.of(matchSchedulerSpec.day, matchSchedulerSpec.startTime)
        val timetable = getSchedule(competition.id)
            .filter { it.startTime >= dateTimeFilter }
            .filter { matchSchedulerSpec.tableNumbers.contains(it.tableNumber) }
            .filter { it.matchInfo.isEmpty() }
            .groupBy { it.startTime }

        val updateSpec = if (matchSchedulerSpec.matchType == MatchType.PLAYOFF) {
            schedulePlayOffMatches(timetable, matches)
        } else {
            scheduleMatches(timetable, matches)
        }

        scheduleRepository.updateMatchesTimeTablesSlots(updateSpec)
        scheduleRepository.setCategoryForTimeSlots(
            updateSpec.map { it.timeTableSlotId },
            competitionCategoryId,
            matchSchedulerSpec.matchType)
    }

    private fun schedulePlayOffMatches(
        timetable: Map<LocalDateTime, List<TimeTableSlotDTO>>,
        matches: List<ScheduleMatchDto>
    ): List<MapMatchToTimeTableSlotSpec> {
        val sortedRounds = matches.map { it.groupOrRound }.distinct().map { Round.valueOf(it) }.sorted().reversed()
        val updateSpec = mutableListOf<MapMatchToTimeTableSlotSpec>()
        var restTable = timetable
        for (round in sortedRounds) {
            val matchesInRound = matches.filter { it.groupOrRound == round.name }
            val spec = scheduleMatches(restTable, matchesInRound)
            val usedTimeSlots = spec.map { it.timeTableSlotId }
            restTable = restTable.filterNot { (_, tables) -> tables.any { usedTimeSlots.contains(it.id) } }
            updateSpec.addAll(spec)
        }
        return updateSpec
    }

    /**
     * Schedule the given matches in the timetable.
     *
     * @throws BadRequestException If there are not enough slots available to fit all the matches
     */
    private fun scheduleMatches(
        timetable: Map<LocalDateTime, List<TimeTableSlotDTO>>,
        matches: List<ScheduleMatchDto>
    ): List<MapMatchToTimeTableSlotSpec> {
        return if (matches.isEmpty()) {
            emptyList()
        } else if (timetable.isEmpty()) {
            throw BadRequestException(BadRequestType.SCHEDULE_TOO_FEW_TIMESLOTS,
                    "There are too few timetable slots to fit all the matches")
        } else {
            val (first, rest) = getFirstTimeBlock(timetable)
            val (updateSpec, remainingMatchesToSchedule) = scheduleBlockOfTimeslots(first, matches)
            updateSpec + scheduleMatches(rest, remainingMatchesToSchedule)
        }
    }

    /**
     * Tries to schedule a list of matches optimally on a set of timeslots.
     *
     * @param block Equal sized timeslots i.e. each timeslot should have the same number of available tables
     * @param matches Matches to schedule
     * @return A pair where the first item contains information about successfully scheduled matches, while
     * the second part is a list of remaining matches that did not fit into the set of timeslots.
     */
    private fun scheduleBlockOfTimeslots(
        block: Map<LocalDateTime, List<TimeTableSlotDTO>>,
        matches: List<ScheduleMatchDto>
    ) : Pair<List<MapMatchToTimeTableSlotSpec>, List<ScheduleMatchDto>> {

        assert(block.entries.isNotEmpty()) { "This function may not be called with a empty block" }

        // The block of timeslots are all of equal size!
        val numberOfTablesAvailableInBlock = block.entries.elementAt(0).value.size

        assert(block.entries.all { it.value.size == numberOfTablesAvailableInBlock }) {
            "This function must be called with timeslots with same number of available tables."
        }

        val settings = ScheduleSettingsDTO(numberOfTablesAvailableInBlock)

        val schedule = createSchedule.execute(matches, settings)

        val updateSpec2 = mutableListOf<MapMatchToTimeTableSlotSpec>()
        for ((timeslot, key) in schedule.timeslots.zip(block.keys)) {
            for ((match, timeTableSlot) in timeslot.matches.zip(block[key]!!)) {
                val spec = MapMatchToTimeTableSlotSpec(match.id, timeTableSlot.id)
                updateSpec2.add(spec)
            }
        }

        val scheduledMatchIds = updateSpec2.map { it.matchId }
        val remainingMatches = matches.filterNot { scheduledMatchIds.contains(it.id) }
        return Pair(updateSpec2, remainingMatches)
    }

    private fun getFirstTimeBlock(timeslots: Map<LocalDateTime, List<TimeTableSlotDTO>>):
            Pair<Map<LocalDateTime, List<TimeTableSlotDTO>>, Map<LocalDateTime, List<TimeTableSlotDTO>>> {
        val time = timeslots.keys.first()
        val predicate = timeslots[time]!!.size
        return timeslots.takeWhile { size == predicate }
    }

    /**
     * Take timeslots while the given predicate is true
     */
    private fun Map<LocalDateTime, List<TimeTableSlotDTO>>.takeWhile(
        predicate: List<TimeTableSlotDTO>.() -> Boolean):
            Pair<Map<LocalDateTime, List<TimeTableSlotDTO>>, Map<LocalDateTime, List<TimeTableSlotDTO>>> {

        val predicateTrue = mutableMapOf<LocalDateTime, List<TimeTableSlotDTO>>()
        val predicateFalse = mutableMapOf<LocalDateTime, List<TimeTableSlotDTO>>()

        var keepTaking = true // Keep taking timeslots as long as this is true
        for ((k, v) in this) {
            if (v.predicate() && keepTaking) {
                predicateTrue[k] = v
            } else {
                keepTaking = false
                predicateFalse[k] = v
            }
        }

        return Pair(predicateTrue, predicateFalse)
    }

    fun getCategorySchedulerSettings(competitionId: Int): ScheduleCategoryContainerDTO {
        val competitionCategories = getCompetitionCategories.execute(competitionId)
        val drawnCategories = competitionCategories.filter { it.status == CompetitionCategoryStatus.DRAWN }
        val availableTableDays = availableTablesService.getTablesAvailableForMainTable(competitionId)

        // Check if categories already are in timetable (i.e. scheduling is under way)
        val scheduleCategoryList = mutableListOf<ScheduleCategoryDTO>()
        for (category in drawnCategories) {
            val timeSlots = scheduleRepository.getTimeSlotsForCategory(category.id)
            // Case 1 - No choices have been made for category
            if (timeSlots.isEmpty()) {
                val possibleMatchTypes = getPossibleMatchTypes(category)
                for (type in possibleMatchTypes) {
                    scheduleCategoryList.add(ScheduleCategoryDTO(
                            categoryDTO = category,
                            selectedMatchType = type,
                            selectedDay = availableTableDays[0].day,
                            selectedTables = emptyList(),
                            selectedStartTime = null
                    ))
                }
            } else {
                val distinctMatchTypes = timeSlots.map { it.matchType }.distinct()
                // Case 2 - One matchtype (group or playoff) has been scheduled
                if (distinctMatchTypes.size == 1) {
                    val matchType: MatchType = MatchType.valueOf(distinctMatchTypes[0])
                    val filteredTimeSlots = timeSlots.filter { it.matchType == matchType.name }

                    val selectedTables = filteredTimeSlots
                            .map { it.tableNumber }
                            .distinct()
                    val startTime = filteredTimeSlots
                            .map { it.startTime }
                            .minOf { it }

                    scheduleCategoryList.add(ScheduleCategoryDTO(
                            categoryDTO = category,
                            selectedMatchType = matchType,
                            selectedDay = filteredTimeSlots[0].startTime.toLocalDate(),
                            selectedTables = selectedTables,
                            selectedStartTime = startTime.toLocalTime()
                    ))
                    scheduleCategoryList.add(ScheduleCategoryDTO(
                            categoryDTO = category,
                            selectedMatchType = if (matchType == MatchType.PLAYOFF) MatchType.GROUP else MatchType.PLAYOFF,
                            selectedDay = filteredTimeSlots[0].startTime.toLocalDate(),
                            selectedTables = emptyList(),
                            selectedStartTime = startTime.toLocalTime()
                    ))
                }
                // Case 3 - both matchtypes have already been scheduled
                if (distinctMatchTypes.size == 2) {
                    for (matchTypeString in distinctMatchTypes) {
                        val matchType: MatchType = MatchType.valueOf(matchTypeString)
                        val filteredTimeSlots = timeSlots.filter { it.matchType == matchType.name }
                        val selectedTables = filteredTimeSlots
                                .map { it.tableNumber }
                                .distinct()
                        val startTime = filteredTimeSlots
                                .map { it.startTime }
                                .minOf { it }

                        scheduleCategoryList.add(ScheduleCategoryDTO(
                                categoryDTO = category,
                                selectedMatchType = matchType,
                                selectedDay = filteredTimeSlots[0].startTime.toLocalDate(),
                                selectedTables = selectedTables,
                                selectedStartTime = startTime.toLocalTime()
                        ))
                    }
                }
            }
        }
        return ScheduleCategoryContainerDTO(
                availableTablesService.getTablesAvailableForMainTable(competitionId),
                scheduleCategoryList
        )
    }

    private fun getCategoryDTO(competitionCategories: List<CompetitionCategoryDTO>, categoryId: Int): CategoryDTO? {
        val selectedCategory = competitionCategories.find { it.id == categoryId } ?: return null

        return CategoryDTO(selectedCategory.id, selectedCategory.category.name, selectedCategory.category.type)
    }

    private fun mergeTimeTableSlots(list: List<TimeTableSlotToMatch>): List<TimeTableSlotDTO> {
        if (list.isEmpty()) {
            return emptyList()
        } else {
            val id = list.first().id
            val (sameTimeSlots, otherTimeSlots) = list.partition { it.id == id }

            val matchInfos = sameTimeSlots.mapNotNull { it.matchInfo }
            val timeTableSlot = TimeTableSlotDTO(
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

    private fun getPossibleMatchTypes(categoryDTO: CompetitionCategoryDTO): List<MatchType> {
        return when (categoryDTO.settings.drawType) {
            DrawType.CUP_ONLY -> listOf(MatchType.PLAYOFF)
            DrawType.POOL_ONLY -> listOf(MatchType.GROUP)
            DrawType.POOL_AND_CUP -> listOf(MatchType.GROUP, MatchType.PLAYOFF)
            DrawType.POOL_AND_CUP_WITH_B_PLAY_OFF -> listOf(MatchType.GROUP, MatchType.PLAYOFF, MatchType.B_PLAYOFF)
        }
    }

    fun removeTimeSlotCategory(categoryId: Int, matchType: MatchType) {
        scheduleRepository.asTransaction {
            scheduleRepository.removeCategoryAndMatchTypeFromTimeslots(categoryId, matchType)
            scheduleRepository.removeCategoryTimeSlotFromMatchTable(categoryId, matchType)
        }
    }

    fun clearSchedule(competitionId: Int) {
        scheduleRepository.clearSchedule(competitionId)
        scheduleRepository.resetTimeSlotsForCompetition(competitionId)
    }
}

data class MatchSchedulerSpec(
    /**
     * Match type to schedule in the given category
     */
    val matchType: MatchType,

    /**
     * Number of tables to use
     */
    val tableNumbers: List<Int>,

    /**
     * Date to schedule the matches
     */
    val day: LocalDate,

    /**
     * Time of day to start schedule matches
     */
    val startTime: LocalTime
)
