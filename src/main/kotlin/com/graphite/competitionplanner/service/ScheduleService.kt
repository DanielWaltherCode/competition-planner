package com.graphite.competitionplanner.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.api.*
import com.graphite.competitionplanner.api.competition.CompetitionSpec
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
import java.time.LocalTime
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

    fun updateMinutesPerMatch(competitionId: Int, minutesPerMatchSpec: MinutesPerMatchSpec) {
        scheduleRepository.updateMinutesPerMatch(competitionId, minutesPerMatchSpec)
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

    /*
     * Schedule available tables
     */
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

    /**
     * Used in selection table on website. Should return either one number of available tables
     * per day, if it's the same for all hourly time slots, or return -1 if the nr of tables differs.
     * Then the number can no longer be changed in the simple table.
     */
    fun getTablesAvailableForMainTable(competitionId: Int): List<AvailableTablesDayDTO> {
        val competitionDays = competitionService.getDaysOfCompetition(competitionId)
        val availableTablesDayList = mutableListOf<AvailableTablesDayDTO>()
        for (day in competitionDays) {
            val availableTables = getTablesAvailableByDay(competitionId, day)
            if (availableTables.size == availableTables.toSet().size) {
                // All elements are the same
                availableTablesDayList.add(AvailableTablesDayDTO(availableTables[0].nrTables, day))
            }
            else {
                availableTablesDayList.add(AvailableTablesDayDTO(-1, day))
            }
        }
        return availableTablesDayList
    }

    // Used as helper function when competition is set up
    fun registerTablesAvailableForWholeCompetition(
        competitionId: Int,
        availableTablesWholeCompetitionSpec: AvailableTablesWholeCompetitionSpec
    ) {
        val competitionDays = competitionService.getDaysOfCompetition(competitionId)
        for (day in competitionDays) {
            registerTablesAvailableFullDay(
                competitionId, AvailableTablesFullDaySpec(
                    availableTablesWholeCompetitionSpec.nrTables, day
                )
            )
        }
    }

    fun updateTablesAvailableForWholeCompetition(
        competitionId: Int,
        availableTablesWholeCompetitionSpec: AvailableTablesWholeCompetitionSpec
    ) {
        val competitionDays = competitionService.getDaysOfCompetition(competitionId)
        for (day in competitionDays) {
            updateTablesAvailableFullDay(
                competitionId, AvailableTablesFullDaySpec(
                    availableTablesWholeCompetitionSpec.nrTables, day
                )
            )
        }
    }

    fun registerTablesAvailableFullDay(
        competitionId: Int,
        availableTablesFullDaySpec: AvailableTablesFullDaySpec
    ): List<AvailableTablesDTO> {
        // Takes selected day, finds all competition hours of that day, and sets nr tables available to same
        // number for all hours
        val dailyStartAndEndDTO = getDailyStartAndEnd(competitionId, availableTablesFullDaySpec.day)
        var currentTime = dailyStartAndEndDTO.startTime

        if (ChronoUnit.HOURS.between(currentTime, dailyStartAndEndDTO.endTime) < 24) {
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
        }

        return getTablesAvailable(competitionId)
    }

    fun updateTablesAvailableFullDay(
        competitionId: Int,
        availableTablesFullDaySpec: AvailableTablesFullDaySpec
    ): List<AvailableTablesDTO> {
        scheduleRepository.updateTablesAvailableForWholeDay(competitionId, availableTablesFullDaySpec)
        return getTablesAvailable(competitionId)
    }

    /*
     *Handle category start times
     */
    fun addCategoryStartTime(
        competitionCategoryId: Int,
        categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        val scheduleCategoryRecord =
            scheduleRepository.addCategoryStartTime(competitionCategoryId, categoryStartTimeSpec)
        return scheduleCategoryRecordToDTO(scheduleCategoryRecord)
    }

    fun updateCategoryStartTime(
        categoryStartTimeId: Int,
        competitionCategoryId: Int,
        categoryStartTimeSpec: CategoryStartTimeSpec
    ): CategoryStartTimeDTO {
        val scheduleCategoryRecord =
            scheduleRepository.updateCategoryStartTime(
                categoryStartTimeId,
                competitionCategoryId,
                categoryStartTimeSpec
            )
        return scheduleCategoryRecordToDTO(scheduleCategoryRecord)
    }

    fun getCategoryStartTimesForCompetition(competitionId: Int): CategoryStartTimesWithOptionsDTO {
        val records = scheduleRepository.getAllCategoryStartTimesInCompetition(competitionId)
        val startTimeDTOList = records.map { scheduleCategoryRecordToDTO(it) }
        val options = getStartTimeFormOptions(competitionId)
        return CategoryStartTimesWithOptionsDTO(startTimeDTOList, options)
    }

    fun getCategoryStartTimesByDay(competitionId: Int, day: LocalDate): List<CategoryStartTimeDTO> {
        val categoriesInCompetition = competitionService.getCategoriesInCompetition(competitionId)
        val startTimeRecords = mutableListOf<ScheduleCategoryRecord>()
        for (category in categoriesInCompetition.categories) {
            try {
                val startTimeRecord = scheduleRepository.getCategoryStartTimeForCategory(category.competitionCategoryId)
                if (startTimeRecord.playingDay == null) {
                    continue
                }
                if (startTimeRecord.playingDay.equals(day)) {
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

                scheduleRepository.addDailyStartAndEnd(
                    competitionId, DailyStartAndEndSpec(
                        currentDate,
                        LocalTime.of(9, 0),
                        LocalTime.of(18, 0)
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

    fun getDailyStartAndEndForWholeCompetition(competitionId: Int): DailyStartAndEndWithOptionsDTO {
        val records = scheduleRepository.getDailyStartAndEndForCompetition(competitionId)
        val startEndDTOList = records.map { dailyStartEndRecordToDTO(it) }
        return DailyStartAndEndWithOptionsDTO(startEndDTOList, competitionService.getDaysOfCompetition(competitionId))
    }

    private fun getStartTimeFormOptions(competitionId: Int): StartTimeFormOptions {
        return StartTimeFormOptions(
            competitionService.getDaysOfCompetition(competitionId),
            StartInterval.values().asList()
        )
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
        val startInterval: StartInterval = if (scheduleCategoryRecord.startInterval == null) {
            StartInterval.NOT_SELECTED
        } else {
            StartInterval.valueOf(scheduleCategoryRecord.startInterval)
        }
        return CategoryStartTimeDTO(
            scheduleCategoryRecord.id,
            competitionCategoryService.getByCompetitionCategoryId(scheduleCategoryRecord.competitonCategoryId),
            scheduleCategoryRecord.playingDay,
            startInterval,
            scheduleCategoryRecord.exactStartTime
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
    val hour: LocalTime
)

data class AvailableTablesDayDTO(
    val nrTables: Int,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate
)

data class CategoryStartTimeDTO(
    val id: Int,
    val categoryDTO: CompetitionCategory,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val playingDay: LocalDate?,
    val startInterval: StartInterval,
    @JsonFormat(pattern = "HH:mm")
    val exactStartTime: LocalTime?
)

data class CategoryStartTimesWithOptionsDTO(
    val categoryStartTimeList: List<CategoryStartTimeDTO>,
    val startTimeFormOptions: StartTimeFormOptions
)

data class DailyStartAndEndDTO(
    val id: Int,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate,
    @JsonFormat(pattern = "HH:mm")
    val startTime: LocalTime,
    @JsonFormat(pattern = "HH:mm")
    val endTime: LocalTime
)

data class DailyStartAndEndWithOptionsDTO(
    val dailyStartEndList: List<DailyStartAndEndDTO>,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val availableDays: List<LocalDate>
)

data class StartTimeFormOptions(
    @JsonFormat(pattern = "yyyy-MM-dd")
    val availableDays: List<LocalDate>,
    val startIntervals: List<StartInterval>
)

enum class StartInterval {
    NOT_SELECTED, EARLY_MORNING, LATE_MORNING, EARLY_AFTERNOON, LATE_AFTERNOON, EVENING
}