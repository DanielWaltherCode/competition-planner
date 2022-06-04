package com.graphite.competitionplanner.schedule.domain.interfaces

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import java.time.LocalDateTime

data class ExcelScheduleDTO(
    val scheduleItemList: List<ExcelScheduleItemDTO>,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val validStartTimes: Set<LocalDateTime>,
    val distinctCategories: Set<CategoryDTO>
)

data class ExcelScheduleItemDTO(
    val tableNumber: Number,
    val matchesAtTable: List<ExcelScheduleMatchDTO>
)

data class ExcelScheduleMatchDTO(
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    val startTime: LocalDateTime,
    val category: CategoryDTO,
    val groupOrRound: String
)