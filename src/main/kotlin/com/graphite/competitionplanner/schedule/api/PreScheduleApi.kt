package com.graphite.competitionplanner.schedule.api

import com.graphite.competitionplanner.schedule.domain.*
import com.graphite.competitionplanner.schedule.domain.interfaces.ScheduleSettingsDTO
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime
import kotlin.time.Duration

@RestController
@RequestMapping("/schedule/{competitionId}/")
class PreScheduleApi(
    val getPreSchedule: GetPreSchedule,
    val trySchedule: TrySchedule,
    val scheduleRepository: ScheduleRepository
) {

    @GetMapping("pre-schedule")
    fun getPreSchedule(
        @PathVariable competitionId: Int
    ): List<CompetitionCategoryPreSchedule> {
        return getPreSchedule.execute(competitionId)
    }

    @PostMapping("pre-schedule")
    fun tryPreSchedule(
        @PathVariable competitionId: Int,
        @RequestBody spec: PreScheduleSpec
    ): PreScheduleDto {
        // TODO: Implement fetching scheduling settings
//        val scheduleSettings = scheduleRepository.getScheduleMetadata(competitionId)
        return trySchedule.execute(competitionId, spec,
            ScheduleSettingsDTO( // TODO: This data has to be read from database.
                Duration.minutes(15),
                1000,
                LocalDateTime.now(),

                LocalDateTime.now()
            )
        )
    }
}