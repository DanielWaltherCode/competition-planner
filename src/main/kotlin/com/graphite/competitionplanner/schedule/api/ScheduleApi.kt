package com.graphite.competitionplanner.schedule.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.schedule.service.*
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime

@RestController()
@RequestMapping("/schedule/{competitionId}/metadata")
class ScheduleMetadataApi(val scheduleService: ScheduleService) {

    @PostMapping
    fun addScheduleMetadata(
        @PathVariable competitionId: Int,
        @RequestBody metadataSpec: ScheduleMetadataSpec
    ): ScheduleMetadataDTO {
        return scheduleService.addScheduleMetadata(competitionId, metadataSpec)
    }

    @PutMapping("/minutes")
    fun updateMinutesPerMatch(
        @PathVariable competitionId: Int,
        @RequestBody minutesPerMatchSpec: MinutesPerMatchSpec
    ) {
        scheduleService.updateMinutesPerMatch(competitionId, minutesPerMatchSpec)
    }


    @GetMapping
    fun getScheduleMetadata(
        @PathVariable competitionId: Int
    ): ScheduleMetadataDTO {
        return scheduleService.getScheduleMetadata(competitionId)
    }

    @PutMapping("/{scheduleMetadataId}")
    fun updateScheduleMetadata(
        @PathVariable competitionId: Int,
        @PathVariable scheduleMetadataId: Int,
        @RequestBody scheduleMetadataSpec: ScheduleMetadataSpec
    ): ScheduleMetadataDTO {
        return scheduleService.updateScheduleMetadata(scheduleMetadataId, competitionId, scheduleMetadataSpec)
    }
}

@RestController
@RequestMapping("/schedule/{competitionId}/available-tables")
class AvailableTablesApi(
    val scheduleService: ScheduleService,
    val scheduleRepository: ScheduleRepository
) {

    @GetMapping("/{day}")
    fun getTablesAvailableByDay(
        @PathVariable competitionId: Int,
        @PathVariable day: LocalDate
    ): List<AvailableTablesDTO> {
        return scheduleService.getTablesAvailableByDay(competitionId, day)
    }

    /**
     * Used in selection table on website. Should return either one number of available tables
     * per day, if it's the same for all hourly time slots, or return -1 if the nr of tables differs.
     * Then the number can no longer be changed in the simple table.
     */
    @GetMapping("/main-table")
    fun getTablesAvailableForMainTable(
        @PathVariable competitionId: Int
    ): List<AvailableTablesDayDTO> {
        return scheduleService.getTablesAvailableForMainTable(competitionId)
    }

    @PostMapping
    fun registerTablesAvailable(
        @PathVariable competitionId: Int,
        @RequestBody availableTablesSpec: AvailableTablesSpec
    ): AvailableTablesDTO {
        return scheduleService.registerTablesAvailable(competitionId, availableTablesSpec)
    }

    @PutMapping("/{availableTablesId}")
    fun updateTablesAvailable(
        @PathVariable competitionId: Int,
        @PathVariable availableTablesId: Int,
        @RequestBody availableTablesSpec: AvailableTablesSpec
    ): AvailableTablesDTO {
        return scheduleService.updateTablesAvailable(availableTablesId, competitionId, availableTablesSpec)
    }

    @DeleteMapping("/{availableTablesId}")
    fun deleteTablesAvailable(
        @PathVariable availableTablesId: Int
    ) {
        scheduleRepository.deleteTablesAvailable(availableTablesId)
    }

    @PostMapping("/full-day")
    fun registerTablesAvailableFullDay(
        @PathVariable competitionId: Int,
        @RequestBody availableTablesFullDaySpec: AvailableTablesFullDaySpec
    ): List<AvailableTablesDTO> {
        return scheduleService.registerTablesAvailableFullDay(competitionId, availableTablesFullDaySpec)
    }

    @PutMapping("/full-day")
    fun updateTablesAvailableFullDay(
        @PathVariable competitionId: Int,
        @RequestBody availableTablesFullDaySpec: AvailableTablesFullDaySpec
    ): List<AvailableTablesDTO> {
        return scheduleService.updateTablesAvailableFullDay(competitionId, availableTablesFullDaySpec)
    }
}

@RestController
@RequestMapping("/schedule/{competitionId}/category-start-time/{categoryId}")
class CategoryStartTimeApi(val scheduleService: ScheduleService, val scheduleRepository: ScheduleRepository) {

    @PostMapping
    fun addCategoryStartTime(
        @PathVariable competitionId: Int,
        @PathVariable categoryId: Int,
        @RequestBody categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        return scheduleService.addCategoryStartTime(categoryId, categoryStartTimeSpec)
    }

    @PutMapping("/{categoryStartTimeId}")
    fun updateCategoryStartTime(
        @PathVariable competitionId: Int,
        @PathVariable categoryId: Int,
        @PathVariable categoryStartTimeId: Int,
        @RequestBody categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        return scheduleService.updateCategoryStartTime(categoryStartTimeId, categoryId, categoryStartTimeSpec)
    }

    @GetMapping("/{day}")
    fun getCategoryStartTimesByDay(
        @PathVariable competitionId: Int,
        @PathVariable day: LocalDate
    ): List<CategoryStartTimeDTO> {
        return scheduleService.getCategoryStartTimesByDay(competitionId, day)
    }

    @GetMapping
    fun getCategoryStartTimesInCompetition(@PathVariable competitionId: Int): CategoryStartTimesWithOptionsDTO {
        return scheduleService.getCategoryStartTimesForCompetition(competitionId)
    }

    @DeleteMapping("/{categoryStartTimeId}")
    fun deleteCategoryStartTime(
        @PathVariable categoryStartTimeId: Int
    ) {
        scheduleRepository.deleteCategoryStartTime(categoryStartTimeId)
    }
}

@RestController
@RequestMapping("/schedule/{competitionId}/daily-start-end")
class DailyStartEndApi(val scheduleService: ScheduleService, val scheduleRepository: ScheduleRepository) {

    @PostMapping
    fun addDailyStartEnd(
        @PathVariable competitionId: Int,
        @RequestBody dailyStartAndEndSpec: DailyStartAndEndSpec
    ): DailyStartAndEndDTO? {
        return scheduleService.addDailyStartAndEnd(competitionId, dailyStartAndEndSpec)
    }

    @PutMapping("/{dailyStartEndId}")
    fun addDailyStartEnd(
        @PathVariable competitionId: Int,
        @PathVariable dailyStartEndId: Int,
        @RequestBody dailyStartAndEndSpec: DailyStartAndEndSpec
    ): DailyStartAndEndDTO? {
        return scheduleService.updateDailyStartAndEnd(dailyStartEndId, competitionId, dailyStartAndEndSpec)
    }

    @GetMapping("/{day}")
    fun getDailyStartEnd(
        @PathVariable competitionId: Int,
        @PathVariable day: LocalDate
    ): DailyStartAndEndDTO? {
        return scheduleService.getDailyStartAndEnd(competitionId, day)
    }

    @GetMapping()
    fun getDailyStartEndForCompetition(
        @PathVariable competitionId: Int
    ): DailyStartAndEndWithOptionsDTO {
        return scheduleService.getDailyStartAndEndForWholeCompetition(competitionId)
    }
}

data class MinutesPerMatchSpec(
    val minutesPerMatch: Int
)

data class ScheduleMetadataSpec(
    val minutesPerMatch: Int,
    val pauseAfterGroupStage: Int,
    val pauseBetweenGroupMatches: Int,
    val pauseBetweenPlayoffMatches: Int
)

// Set all time slots for time to same number of tables
data class AvailableTablesWholeCompetitionSpec(
    val nrTables: Int
)

data class AvailableTablesFullDaySpec(
    val nrTables: Int,
    val day: LocalDate
)

// Here number of tables can be scheduled per hour
data class AvailableTablesSpec(
    val nrTables: Int,
    val day: LocalDate,
    val hour: LocalTime
)

data class CategoryStartTimeSpec(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playingDay: LocalDate?,
    val startInterval: StartInterval?,
    @JsonFormat(pattern = "HH:mm")
    val exactStartTime: LocalTime?
)

data class DailyStartAndEndSpec(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate,
    @JsonFormat(pattern = "HH:mm")
    val startTime: LocalTime,
    @JsonFormat(pattern = "HH:mm")
    val endTime: LocalTime
)