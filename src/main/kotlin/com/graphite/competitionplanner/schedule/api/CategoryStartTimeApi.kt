package com.graphite.competitionplanner.schedule.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.schedule.service.*
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime

@RestController
@RequestMapping("/schedule/{competitionId}/category-start-time/{categoryId}")
class CategoryStartTimeApi(val categoryStartTimeService: CategoryStartTimeService, val scheduleRepository: ScheduleRepository) {

    @PostMapping
    fun addCategoryStartTime(
        @PathVariable competitionId: Int,
        @PathVariable categoryId: Int,
        @RequestBody categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        return categoryStartTimeService.addCategoryStartTime(categoryId, categoryStartTimeSpec)
    }

    @PutMapping("/{categoryStartTimeId}")
    fun updateCategoryStartTime(
        @PathVariable competitionId: Int,
        @PathVariable categoryId: Int,
        @PathVariable categoryStartTimeId: Int,
        @RequestBody categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        return categoryStartTimeService.updateCategoryStartTime(categoryStartTimeId, categoryId, categoryStartTimeSpec)
    }

    @GetMapping("/{day}")
    fun getCategoryStartTimesByDay(
        @PathVariable competitionId: Int,
        @PathVariable day: LocalDate
    ): List<CategoryStartTimeDTO> {
        return categoryStartTimeService.getCategoryStartTimesByDay(competitionId, day)
    }

    @GetMapping
    fun getCategoryStartTimesInCompetition(@PathVariable competitionId: Int): CategoryStartTimesWithOptionsDTO {
        return categoryStartTimeService.getCategoryStartTimesForCompetition(competitionId)
    }

    @DeleteMapping("/{categoryStartTimeId}")
    fun deleteCategoryStartTime(
        @PathVariable categoryStartTimeId: Int
    ) {
        scheduleRepository.deleteCategoryStartTime(categoryStartTimeId)
    }
}

data class CategoryStartTimeSpec(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playingDay: LocalDate?,
    val startInterval: StartInterval?,
    @JsonFormat(pattern = "HH:mm")
    val exactStartTime: LocalTime?
)