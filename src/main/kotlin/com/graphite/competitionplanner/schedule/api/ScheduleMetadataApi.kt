package com.graphite.competitionplanner.schedule.api

import com.graphite.competitionplanner.schedule.service.*
import org.springframework.web.bind.annotation.*

@RestController()
@RequestMapping("/schedule/{competitionId}/metadata")
class ScheduleMetadataApi(val scheduleMetadataService: ScheduleMetadataService) {

    @PostMapping
    fun addScheduleMetadata(
        @PathVariable competitionId: Int,
        @RequestBody metadataSpec: ScheduleMetadataSpec
    ): ScheduleMetadataDTO {
        return scheduleMetadataService.addScheduleMetadata(competitionId, metadataSpec)
    }

    @PutMapping("/minutes")
    fun updateMinutesPerMatch(
        @PathVariable competitionId: Int,
        @RequestBody minutesPerMatchSpec: MinutesPerMatchSpec
    ) {
        scheduleMetadataService.updateMinutesPerMatch(competitionId, minutesPerMatchSpec)
    }


    @GetMapping
    fun getScheduleMetadata(
        @PathVariable competitionId: Int
    ): ScheduleMetadataDTO {
        return scheduleMetadataService.getScheduleMetadata(competitionId)
    }

    @PutMapping("/{scheduleMetadataId}")
    fun updateScheduleMetadata(
        @PathVariable competitionId: Int,
        @PathVariable scheduleMetadataId: Int,
        @RequestBody scheduleMetadataSpec: ScheduleMetadataSpec
    ): ScheduleMetadataDTO {
        return scheduleMetadataService.updateScheduleMetadata(scheduleMetadataId, competitionId, scheduleMetadataSpec)
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
