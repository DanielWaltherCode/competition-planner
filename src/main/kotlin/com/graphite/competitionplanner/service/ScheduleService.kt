package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.*
import com.graphite.competitionplanner.repositories.ScheduleRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategory
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.tables.records.ScheduleAvailableTablesRecord
import com.graphite.competitionplanner.tables.records.ScheduleCategoryRecord
import com.graphite.competitionplanner.tables.records.ScheduleDailyTimesRecord
import com.graphite.competitionplanner.tables.records.ScheduleMetadataRecord
import org.jooq.exception.NoDataFoundException
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class ScheduleService(
    val scheduleRepository: ScheduleRepository,
    val competitionCategoryService: CompetitionCategoryService,
    // Lazy needed because otherwise there is a circular dependency
    @Lazy val competitionService: CompetitionService
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
        return metadataRecordToDTO(metadataRecord)
    }

    fun addDefaultScheduleMetadata(competitionId: Int) {
        val metadataSpec = ScheduleMetadataSpec(
            minutesPerMatch = 25,
            pauseHoursAfterGroupStage = 0,
            pauseBetweenGroupMatches = 0,
            pauseBetweenPlayoffMatches = 30
        )
        scheduleRepository.addScheduleMetadata(competitionId, metadataSpec)
    }

    fun getScheduleMetadata(competitionId: Int): ScheduleMetadataDTO {
        val record = scheduleRepository.getScheduleMetadata(competitionId)
        return metadataRecordToDTO(record)
    }

    // Table methods
    fun registerTablesAvailable(
        competitionId: Int,
        availableTablesSpec: AvailableTablesSpec
    ): AvailableTablesDTO {
        val tablesRecord = scheduleRepository.registerTablesAvailable(competitionId, availableTablesSpec)
        return availableTablesRecordToDTO(tablesRecord)
    }

    fun updateTablesAvailable(
        availableTablesId: Int,
        competitionId: Int,
        availableTablesSpec: AvailableTablesSpec
    ): AvailableTablesDTO {
        val tablesRecord =
            scheduleRepository.updateTablesAvailable(availableTablesId, competitionId, availableTablesSpec)
        return availableTablesRecordToDTO(tablesRecord)
    }

    fun getTablesAvailable(competitionId: Int): List<AvailableTablesDTO> {
        val tablesRecord = scheduleRepository.getTablesAvailable(competitionId)
        return tablesRecord.map { availableTablesRecordToDTO(it) }
    }

    fun getTablesAvailableByDay(competitionId: Int, day: LocalDate): List<AvailableTablesDTO> {
        val tablesRecord = scheduleRepository.getTablesAvailableByDay(competitionId, day)
        return tablesRecord.map { availableTablesRecordToDTO(it) }
    }

    fun registerTablesAvailableFullDay(
        competitionId: Int,
        availableTablesFullDaySpec: AvailableTablesFullDaySpec
    ): List<AvailableTablesDTO> {
        val dailyStartAndEndDTO = getDailyStartAndEnd(competitionId, availableTablesFullDaySpec.day)
        var currentTime = dailyStartAndEndDTO.startTime
        while (currentTime <= dailyStartAndEndDTO.endTime) {
            scheduleRepository.registerTablesAvailable(
                competitionId, AvailableTablesSpec(
                    availableTablesFullDaySpec.nrTables,
                    availableTablesFullDaySpec.day,
                    currentTime
                )
            )
            currentTime = currentTime.plusHours(1)
        }
        return getTablesAvailable(competitionId)
    }

    // Handle category start times
    fun addCategoryStartTime(
        competitionCategoryId: Int,
        categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        val scheduleCategoryRecord =
            scheduleRepository.addCategoryStartTime(competitionCategoryId, categoryStartTimeSpec.startTime)
        return scheduleCategoryRecordToDTO(scheduleCategoryRecord)
    }

    fun updateCategoryStartTime(
        categoryStartTimeId: Int,
        competitionCategoryId: Int,
        categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        val scheduleCategoryRecord =
            scheduleRepository.updateCategoryStartTime(categoryStartTimeId, competitionCategoryId, categoryStartTimeSpec.startTime)
        return scheduleCategoryRecordToDTO(scheduleCategoryRecord)
    }

    fun getCategoryStartTimesByDay(competitionId: Int, day: LocalDate): List<CategoryStartTimeDTO> {
        val categoriesInCompetition = competitionService.getCategoriesInCompetition(competitionId)
        val startTimeRecords = mutableListOf<ScheduleCategoryRecord>()
        for (category in categoriesInCompetition.categories) {
            try {
                val startTimeRecord = scheduleRepository.getCategoryStartTime(category.competitionCategoryId)
                if (startTimeRecord.startTime.toLocalDate().equals(day)) {
                    startTimeRecords.add(startTimeRecord)
                }
            }
            // If the category has not been scheduled, continue
            catch (noDataException: NoDataFoundException) {
                continue
            }
        }
        return startTimeRecords.map { scheduleCategoryRecordToDTO(it) }
    }

    // Handle start and end times for whole day
    fun addDailyStartAndEnd(
        competitionId: Int,
        dailyStartAndEndSpec: DailyStartAndEndSpec
    ): DailyStartAndEndDTO {
        val record = scheduleRepository.addDailyStartAndEnd(competitionId, dailyStartAndEndSpec)
        return dailyStartEndRecordToDTO(record)
    }

    // This is used as helper method to set up default start/end times when registering a new competition
    fun addDailyStartAndEndForWholeCompetition(
        competitionId: Int
    ) {
        val competition = competitionService.getById(competitionId)

        if (competition.startDate == null || competition.endDate == null) {
            return
        }

        if (ChronoUnit.DAYS.between(competition.startDate, competition.endDate) < 30) {
            var currentDate = competition.startDate
            while (currentDate!! <= competition.endDate) {
                val startTime = currentDate.atTime(9, 0)
                val endTime = currentDate.atTime(18, 0)

                scheduleRepository.addDailyStartAndEnd(
                    competitionId, DailyStartAndEndSpec(
                        currentDate,
                        startTime,
                        endTime
                    )
                )
                currentDate = currentDate.plusDays(1)
            }
        }
    }

    fun updateDailyStartAndEnd(
        dailyStartAndEndId: Int,
        competitionId: Int,
        dailyStartAndEndSpec: DailyStartAndEndSpec
    ): DailyStartAndEndDTO {
        val record = scheduleRepository.updateDailyStartAndEnd(dailyStartAndEndId, competitionId, dailyStartAndEndSpec)
        return dailyStartEndRecordToDTO(record)
    }

    fun getDailyStartAndEnd(competitionId: Int, day: LocalDate): DailyStartAndEndDTO {
        val record = scheduleRepository.getDailyStartAndEnd(competitionId, day)
        return dailyStartEndRecordToDTO(record)
    }

    fun getDailyStartAndEndForWholeCompetition(competitionId: Int): List<DailyStartAndEndDTO> {
        val records = scheduleRepository.getDailyStartAndEndForCompetition(competitionId)
        return records.map { dailyStartEndRecordToDTO(it) }
    }

    // Converters
    private fun metadataRecordToDTO(metadataRecord: ScheduleMetadataRecord): ScheduleMetadataDTO {
        return ScheduleMetadataDTO(
            metadataRecord.id,
            metadataRecord.minutesPerMatch,
            metadataRecord.pauseHoursAfterGroupStage,
            metadataRecord.pauseBetweenGroupMatches,
            metadataRecord.pauseBetweenPlayoffMatches
        )
    }

    private fun availableTablesRecordToDTO(availableTablesRecord: ScheduleAvailableTablesRecord): AvailableTablesDTO {
        return AvailableTablesDTO(
            availableTablesRecord.id,
            availableTablesRecord.nrTables,
            availableTablesRecord.day,
            availableTablesRecord.hour
        )
    }

    private fun scheduleCategoryRecordToDTO(scheduleCategoryRecord: ScheduleCategoryRecord): CategoryStartTimeDTO {
        return CategoryStartTimeDTO(
            scheduleCategoryRecord.id,
            competitionCategoryService.getByCompetitionCategoryId(scheduleCategoryRecord.competitonCategoryId),
            scheduleCategoryRecord.startTime
        )
    }

    private fun dailyStartEndRecordToDTO(dailyTimesRecord: ScheduleDailyTimesRecord): DailyStartAndEndDTO {
        return DailyStartAndEndDTO(
            dailyTimesRecord.id,
            dailyTimesRecord.day,
            dailyTimesRecord.startTime,
            dailyTimesRecord.endTime
        )
    }
}


data class ScheduleMetadataDTO(
    val id: Int,
    val minutesPerMatch: Int,
    val pauseHoursAfterGroupStage: Int,
    val pauseBetweenGroupMatches: Int,
    val pauseBetweenPlayoffMatches: Int
)

data class AvailableTablesDTO(
    val id: Int,
    val nrTables: Int,
    val day: LocalDate,
    val hour: LocalDateTime
)

data class CategoryStartTimeDTO(
    val id: Int,
    val categoryDTO: CompetitionCategory,
    val startTime: LocalDateTime
)

data class DailyStartAndEndDTO(
    val id: Int,
    val day: LocalDate,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime
)