package com.graphite.competitionplanner.schedule.api

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.schedule.domain.*
import com.graphite.competitionplanner.schedule.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotDTO
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.schedule.service.ScheduleMetadataService
import com.graphite.competitionplanner.schedule.service.StartInterval
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.time.Duration

@RestController
@RequestMapping("/schedule/{competitionId}/pre-schedule")
class PreScheduleApi(
        val getPreSchedule: GetPreSchedule,
        val trySchedule: TrySchedule,
        val scheduleRepository: ScheduleRepository,
        val scheduleMetadataService: ScheduleMetadataService,
        val availableTablesService: AvailableTablesService,
        val competitionScheduler: CompetitionScheduler,
        val findCompetitionCategory: FindCompetitionCategory
) {


    @GetMapping
    fun getPreSchedule(
            @PathVariable competitionId: Int,
    ): List<CompetitionCategoryPreScheduleDTO> {
        return getPreSchedule.execute(competitionId)
    }

    @PostMapping("/{competitionCategoryId}")
    fun tryPreSchedule(
            @PathVariable competitionId: Int,
            @PathVariable competitionCategoryId: Int,
            @RequestBody spec: PreScheduleSpec
    ): PreScheduleDto {
        val scheduleSettings = scheduleMetadataService.getScheduleMetadata(competitionId)
        val availableTables = availableTablesService.getTablesAvailableByDay(competitionId, spec.playDate)
        return trySchedule.execute(competitionId, competitionCategoryId, spec,
                ScheduleSettingsDTO(
                        Duration.minutes(scheduleSettings.minutesPerMatch),
                        availableTables.nrTables,
                        LocalDateTime.now(),
                        LocalDateTime.now()
                )
        )
    }
}

data class MatchSchedulerSpec(
        val matchType: MatchType,
        val tableNumbers: List<Int>,
        val day: LocalDate,
        val startTime: LocalTime
)

data class TimeTableContainerDTO(
        val timeTableSlots: List<TimeTableSlotDTO>,
        val validStartTimes: Set<LocalDateTime>,
        val tables: Set<Int>,
        val distinctCategories: Set<CompetitionCategoryDTO>
)