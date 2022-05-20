package com.graphite.competitionplanner.schedule.service

import com.graphite.competitionplanner.schedule.api.MinutesPerMatchSpec
import com.graphite.competitionplanner.schedule.api.ScheduleMetadataSpec
import com.graphite.competitionplanner.schedule.domain.TimeTableSlotHandler
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.tables.records.ScheduleMetadataRecord
import org.springframework.stereotype.Service

@Service
class ScheduleMetadataService(
    val scheduleRepository: ScheduleRepository,
    @org.springframework.context.annotation.Lazy val timeTableSlotHandler: TimeTableSlotHandler
) {

    // Schedule metadata methods
    fun addScheduleMetadata(competitionId: Int, metadataSpec: ScheduleMetadataSpec): ScheduleMetadataDTO {
        val metadataRecord = scheduleRepository.addScheduleMetadata(competitionId, metadataSpec)
        return metadataRecordToDTO(metadataRecord)
    }

    fun updateScheduleMetadata(
        scheduleMetadataId: Int,
        competitionId: Int,
        metadataSpec: ScheduleMetadataSpec
    ): ScheduleMetadataDTO {
        val metadataRecord = scheduleRepository.updateScheduleMetadata(scheduleMetadataId, competitionId, metadataSpec)
        timeTableSlotHandler.init(competitionId)
        return metadataRecordToDTO(metadataRecord)
    }

    fun updateMinutesPerMatch(competitionId: Int, minutesPerMatchSpec: MinutesPerMatchSpec) {
        scheduleRepository.updateMinutesPerMatch(competitionId, minutesPerMatchSpec)
        timeTableSlotHandler.init(competitionId)
    }

    fun addDefaultScheduleMetadata(competitionId: Int) {
        val metadataSpec = ScheduleMetadataSpec(
            minutesPerMatch = 25,
            pauseAfterGroupStage = 0,
            pauseBetweenGroupMatches = 0,
            pauseBetweenPlayoffMatches = 25
        )
        scheduleRepository.addScheduleMetadata(competitionId, metadataSpec)
    }

    fun getScheduleMetadata(competitionId: Int): ScheduleMetadataDTO {
        val record = scheduleRepository.getScheduleMetadata(competitionId)
        return metadataRecordToDTO(record)
    }

    // Converters
    private fun metadataRecordToDTO(metadataRecord: ScheduleMetadataRecord): ScheduleMetadataDTO {
        return ScheduleMetadataDTO(
            metadataRecord.id,
            metadataRecord.minutesPerMatch,
            metadataRecord.pauseAfterGroupStage,
            metadataRecord.pauseBetweenGroupMatches,
            metadataRecord.pauseBetweenPlayoffMatches
        )
    }
}

data class ScheduleMetadataDTO(
    val id: Int,
    val minutesPerMatch: Int,
    val pauseAfterGroupStage: Int,
    val pauseBetweenGroupMatches: Int,
    val pauseBetweenPlayoffMatches: Int
)