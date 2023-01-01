package com.graphite.competitionplanner.schedule.api

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.schedule.service.DailyStartAndEndDTO
import com.graphite.competitionplanner.schedule.service.DailyStartAndEndWithOptionsDTO
import com.graphite.competitionplanner.schedule.service.DailyStartEndService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalTime

@RestController
@RequestMapping("api/schedule/{competitionId}/daily-start-end")
class DailyStartEndApi(val dailyStartEndService: DailyStartEndService) {

    @PostMapping
    fun addDailyStartEnd(
        @PathVariable competitionId: Int,
        @RequestBody dailyStartAndEndSpec: DailyStartAndEndSpec
    ): DailyStartAndEndDTO? {
        return dailyStartEndService.addDailyStartAndEnd(competitionId, dailyStartAndEndSpec)
    }

    @PutMapping()
    fun updateDailyStartEnd(
        @PathVariable competitionId: Int,
        @RequestBody dailyStartAndEndSpec: DailyStartAndEndSpec
    ) {
        dailyStartEndService.updateDailyStartAndEnd(competitionId, dailyStartAndEndSpec)
    }

    @GetMapping("/{day}")
    fun getDailyStartEnd(
        @PathVariable competitionId: Int,
        @PathVariable day: LocalDate
    ): DailyStartAndEndDTO? {
        return dailyStartEndService.getDailyStartAndEnd(competitionId, day)
    }

    @GetMapping()
    fun getDailyStartEndForCompetition(
        @PathVariable competitionId: Int
    ): DailyStartAndEndWithOptionsDTO {
        return dailyStartEndService.getDailyStartAndEndForWholeCompetition(competitionId)
    }
}

data class AllDailyStartEndsSpec(
        val dailyStartEndList: List<DailyStartAndEndSpec>
)

data class DailyStartAndEndSpec(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate,
    @JsonFormat(pattern = "HH:mm")
    val startTime: LocalTime,
    @JsonFormat(pattern = "HH:mm")
    val endTime: LocalTime
)