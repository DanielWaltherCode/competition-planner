package com.graphite.competitionplanner.schedule.domain

import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.schedule.api.MatchSchedulerSpec
import com.graphite.competitionplanner.schedule.api.ScheduleCategoryContainerDTO
import com.graphite.competitionplanner.schedule.api.ScheduleCategoryDTO
import com.graphite.competitionplanner.schedule.interfaces.ExcelScheduleDTO
import com.graphite.competitionplanner.schedule.interfaces.ExcelScheduleDTOContainer
import com.graphite.competitionplanner.schedule.interfaces.ExcelScheduleItemDTO
import com.graphite.competitionplanner.schedule.interfaces.ExcelScheduleMatchDTO
import com.graphite.competitionplanner.schedule.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.interfaces.*
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.time.Duration

/**
 * Class that provides methods to schedule matches within the same competition.
 */
@Component
class CompetitionScheduler(
        val repository: IScheduleRepository,
        val createSchedule: CreateSchedule,
        val findCompetitions: FindCompetitions,
        val availableTablesService: AvailableTablesService,
        val drawService: DrawService,
        val getCompetitionCategories: GetCompetitionCategories
) {

    /**
     * Maps a match to a specific TimeTableSlot. This is more of a utility function were an administrator
     * can easily move one match to a new TimeTableSlot.
     */
    fun mapMatchToTimeTableSlot(matchToTimeTableSlot: MapMatchToTimeTableSlotSpec): TimeTableSlotDTO {
        val matchesInSameSlot = repository.addMatchToTimeTableSlot(matchToTimeTableSlot)

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
    fun getSchedule(competitionId: Int): List<TimeTableSlotDTO> {
        val matchesToSlots = repository.getTimeTable(competitionId)
        val schedule = mergeTimeTableSlots(matchesToSlots)
        return schedule.sortedWith(
                compareBy(TimeTableSlotDTO::location, TimeTableSlotDTO::startTime, TimeTableSlotDTO::tableNumber))
    }

    fun getScheduleForFrontend(competitionId: Int): ExcelScheduleDTOContainer {
        val timeSlots = repository.getTimeSlotsForCompetition(competitionId)
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
     * @param matchType The type of matches in the given category to be scheduled
     * @param tables Table numbers that the matches will be scheduled at
     * @param startTime The first matches will be scheduled here
     * @param location The location the matches will be scheduled at
     */
    fun scheduleCompetitionCategory(
            competitionId: Int,
            competitionCategoryId: Int,
            matchSchedulerSpec: MatchSchedulerSpec
    ) {
        val competition = findCompetitions.byId(competitionId)
        val matches = repository.getScheduleMatches(competitionCategoryId, matchSchedulerSpec.matchType)
        val settings = ScheduleSettingsDTO(
                Duration.minutes(15), // Not used
                matchSchedulerSpec.tableNumbers.size,
                LocalDateTime.now(), // Not used
                LocalDateTime.now().plusMinutes(60) // Not used
        )
        val schedule = createSchedule.execute(matches, settings)
        val startTime = LocalDateTime.of(matchSchedulerSpec.day, matchSchedulerSpec.startTime)
        val timeTableSlots = repository.getTimeTableSlotRecords(competitionId, startTime, matchSchedulerSpec.tableNumbers,
                competition.location.name)

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
            updateTimeSlotCategory(updateSpec, competitionCategoryId, matchSchedulerSpec.matchType)

        } catch (ex: IndexOutOfBoundsException) {
            throw IndexOutOfBoundsException(
                    "Not all matches fit the schedule. Please consider adding more tables or start earlier.")
        }
    }

    fun getCategorySchedulerSettings(competitionId: Int): ScheduleCategoryContainerDTO {
        val competitionCategories = getCompetitionCategories.execute(competitionId)
        val drawnCategories = competitionCategories.filter { drawService.isDrawMade(it.id) }
        val availableTableDays = availableTablesService.getTablesAvailableForMainTable(competitionId)

        // Check if categories already are in timetable (i.e. scheduling is under way)
        val scheduleCategoryList = mutableListOf<ScheduleCategoryDTO>()
        for (category in drawnCategories) {
            val timeSlots = repository.getTimeSlotsForCategory(category.id)
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
        updateTimeSlotCategory(updateSpecs, competitionCategoryId, matchType)

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

    private fun createUpdateSpecs(settings: ScheduleSettingsDTO, blocks: List<ScheduleBlock>,
                                  matches: List<ScheduleMatchDto>): List<MapMatchToTimeTableSlotSpec> {
        return if (blocks.isEmpty()) {
            if (matches.isNotEmpty()) {
                throw IndexOutOfBoundsException(
                        "Not all matches fit the schedule. Please consider adding more tables or start earlier.")
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

    private fun getPossibleMatchTypes(categoryDTO: CompetitionCategoryDTO): List<MatchType> {
        return when (categoryDTO.settings.drawType) {
            DrawType.CUP_ONLY -> listOf(MatchType.PLAYOFF)
            DrawType.POOL_ONLY -> listOf(MatchType.GROUP)
            DrawType.POOL_AND_CUP -> listOf(MatchType.GROUP, MatchType.PLAYOFF)
        }
    }

    private fun updateTimeSlotCategory(matchSlotSpecList: List<MapMatchToTimeTableSlotSpec>, categoryId: Int,
                                       matchType: MatchType) {
        repository.removeCategoryFromTimeslots(categoryId, matchType)
        repository.setCategoryForTimeSlots(matchSlotSpecList.map { it.timeTableSlotId }, categoryId, matchType)
    }

    private fun getScheduleBlocks(tableSlots: List<TimeTableSlotDTO>): List<ScheduleBlock> {
        val groupedByTime = tableSlots.groupBy { it.startTime }
                .map { (startTime, slots) -> Pair(startTime, slots.filter { slot -> slot.matchInfo.isEmpty() }) }
        return mergeRowsToBlocks(groupedByTime)
    }

    private fun mergeRowsToBlocks(groupedByTime: List<Pair<LocalDateTime, List<TimeTableSlotDTO>>>): List<ScheduleBlock> {
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
            val timeTableSlots: List<TimeTableSlotDTO>
    )

    fun clearSchedule(competitionId: Int) {
        repository.clearSchedule(competitionId)
    }
}
