package com.graphite.competitionplanner.schedule.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.match.domain.MatchType
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.schedule.domain.CompetitionScheduler
import com.graphite.competitionplanner.schedule.domain.MatchSchedulerSpec
import com.graphite.competitionplanner.schedule.interfaces.*
import com.graphite.competitionplanner.schedule.service.AvailableTablesDayDTO
import com.graphite.competitionplanner.schedule.service.ScheduleMetadataService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@RestController
@RequestMapping("api/schedule/{competitionId}/")
class ScheduleApi(
        val competitionScheduler: CompetitionScheduler,
        val scheduleMetadataService: ScheduleMetadataService,
        val matchRepository: MatchRepository
) {

    @PutMapping("publish")
    fun publishSchedule(@PathVariable competitionId: Int) {
        competitionScheduler.publishSchedule(competitionId)
    }

    @GetMapping("/timetable-info")
    fun getTimeTableInfo(
            @PathVariable competitionId: Int,
    ): ExcelScheduleDTOContainer {
        return competitionScheduler.getScheduleForFrontend(competitionId)
    }

    @GetMapping("/category-settings")
    fun getCategorySchedulerSettings(@PathVariable competitionId: Int): ScheduleCategoryContainerDTO {
        return competitionScheduler.getCategorySchedulerSettings(competitionId)
    }

    //Updates daily start end, minutes per match and available tables at once
    // This is done to make it easier to update match time slots correctly
    @PutMapping
    fun saveScheduleSettings(@PathVariable competitionId: Int,
                             @RequestBody scheduleSaveMainChangesSpec: ScheduleSaveMainChangesSpec) {
        scheduleMetadataService.saveGeneralScheduleChanges(competitionId, scheduleSaveMainChangesSpec)
    }

    @PutMapping("category/{competitionCategoryId}")
    fun scheduleCategory(
            @PathVariable competitionId: Int,
            @PathVariable competitionCategoryId: Int,
            @RequestBody spec: ScheduleCategorySpec
    ) {
        competitionScheduler.scheduleCompetitionCategory(
                competitionId,
                competitionCategoryId,
                MatchSchedulerSpec(
                        spec.matchType,
                        spec.tableNumbers,
                        spec.startTime.toLocalDate(),
                        spec.startTime.toLocalTime()
                )
        )
    }

    // Where "stage" is group or playoff
    @DeleteMapping("category/{competitionCategoryId}/{stage}")
    fun clearSchedulingForCategory(@PathVariable competitionCategoryId: Int,
                                   @PathVariable stage: String) {
        val matchType = if (stage.lowercase() == "group") MatchType.GROUP else MatchType.PLAYOFF
        competitionScheduler.removeTimeSlotCategory(competitionCategoryId, matchType)
        matchRepository.removeMatchTimeForCategory(competitionCategoryId, matchType)
    }

    /**
     * Removes scheduling data in match and timeslot table for a given competition
     */
    @DeleteMapping
    fun clearSchedule(@PathVariable competitionId: Int) {
        competitionScheduler.clearSchedule(competitionId)
    }

    @PutMapping("match/{matchId}")
    fun setMatchTime(@PathVariable matchId: Int, @RequestBody matchTimeSpec: MatchTimeSpec) {
        matchRepository.updateMatchTime(matchId, matchTimeSpec.matchTime)
    }

    data class MatchTimeSpec(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
            val matchTime: LocalDateTime
    )

    data class ScheduleCategorySpec(
            /**
             * Scheduling mode
             */
            val mode: ScheduleMode,
            /**
             * Type of matches to schedule
             */
            val matchType: MatchType,
            /**
             * Tables to schedule the matches on
             */
            val tableNumbers: List<Int>,
            /**
             * Location where the matches are scheduled
             */
            val location: String,
            /**
             * When the first matches will be scheduled. Only applicable if mode is ABSOLUTE
             */
            val startTime: LocalDateTime
    )

    enum class ScheduleMode {
        /**
         * Start scheduling matches on first available time slot on the given tables
         */
        APPEND,

        /**
         * Start scheduling matches at the given time on the given tables
         */
        ABSOLUTE
    }
}

data class ScheduleCategoryContainerDTO(
        val availableTablesDayList: List<AvailableTablesDayDTO>,
        val scheduleCategoryList: List<ScheduleCategoryDTO>
)

data class ScheduleSaveMainChangesSpec(
        val availableTables: AvailableTablesAllDaysSpec,
        val dailyStartEnd: AllDailyStartEndsSpec,
        val minutesPerMatchSpec: MinutesPerMatchSpec
)

data class ScheduleCategoryDTO(

        val categoryDTO: CompetitionCategoryDTO,

        /**
         * Type of matches to schedule
         */
        val selectedMatchType: MatchType,

        @JsonFormat(pattern = "yyyy-MM-dd")
        val selectedDay: LocalDate,
        /**
         * Tables to schedule the matches on
         */
        val selectedTables: List<Int>,

        /**
         * When the first matches will be scheduled. Null if no start time has been set yet
         */
        @JsonFormat(pattern = "HH:mm")
        val selectedStartTime: LocalTime?
)