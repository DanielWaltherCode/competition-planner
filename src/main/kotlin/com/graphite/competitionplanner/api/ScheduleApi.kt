package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.ScheduleRepository
import com.graphite.competitionplanner.service.*
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

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


    @PostMapping("/start-time/{competitionCategoryId}")
    fun registerCategoryStarttime(
        @PathVariable competitionCategoryId: Int,
        @RequestBody categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        return scheduleService.addCategoryStartTime(competitionCategoryId, categoryStartTimeSpec)
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
        @PathVariable day: LocalDate) {
        return getCategoryStartTimesByDay(competitionId, day)
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
    ): DailyStartAndEndDTO {
        return scheduleService.addDailyStartAndEnd(competitionId, dailyStartAndEndSpec)
    }

    @PutMapping("/{dailyStartEndId}")
    fun addDailyStartEnd(
        @PathVariable competitionId: Int,
        @PathVariable dailyStartEndId: Int,
        @RequestBody dailyStartAndEndSpec: DailyStartAndEndSpec
    ): DailyStartAndEndDTO {
        return scheduleService.updateDailyStartAndEnd(dailyStartEndId, competitionId, dailyStartAndEndSpec)
    }

    @GetMapping("/{day}")
    fun getDailyStartEnd(
        @PathVariable competitionId: Int,
        @PathVariable day: LocalDate
    ): DailyStartAndEndDTO {
        return scheduleService.getDailyStartAndEnd(competitionId, day)
    }

    @GetMapping()
    fun getDailyStartEndForCompetition(
        @PathVariable competitionId: Int
    ): List<DailyStartAndEndDTO> {
        return scheduleService.getDailyStartAndEndForWholeCompetition(competitionId)
    }
}

data class ScheduleMetadataSpec(
    val minutesPerMatch: Int,
    val pauseHoursAfterGroupStage: Int,
    val pauseBetweenGroupMatches: Int,
    val pauseBetweenPlayoffMatches: Int
)

// Set all time slots for time to same number of tables
data class AvailableTablesFullDaySpec(
    val nrTables: Int,
    val day: LocalDate
)

// Here number of tables can be scheduled per hour
data class AvailableTablesSpec(
    val nrTables: Int,
    val day: LocalDate,
    val hour: LocalDateTime
)

data class CategoryStartTimeSpec(
    val startTime: LocalDateTime
)

data class DailyStartAndEndSpec(
    val day: LocalDate,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)