package com.graphite.competitionplanner.schedule.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategory
import com.graphite.competitionplanner.schedule.api.*
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.tables.records.ScheduleAvailableTablesRecord
import com.graphite.competitionplanner.tables.records.ScheduleCategoryRecord
import com.graphite.competitionplanner.tables.records.ScheduleDailyTimesRecord
import com.graphite.competitionplanner.tables.records.ScheduleMetadataRecord
import org.jooq.exception.NoDataFoundException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class ScheduleService(
    val scheduleRepository: ScheduleRepository,
    val findCompetitions: FindCompetitions,
    val getDaysOfCompetition: GetDaysOfCompetition,
    val findCompetitionCategory: FindCompetitionCategory,
    val getCompetitionCategories: GetCompetitionCategories
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
        val competition = findCompetitions.byId(competitionId)
        val competitionDays = getDaysOfCompetition.execute(competition)
        val availableTablesDayList = mutableListOf<AvailableTablesDayDTO>()
        for (day in competitionDays) {
            val availableTablesDTOs = getTablesAvailableByDay(competitionId, day)
            val nrTablesList = availableTablesDTOs.map { it.nrTables }
            val nrTablesSet = nrTablesList.toSet()

            // All elements are the same
            if (nrTablesSet.size == 1) {
                availableTablesDayList.add(AvailableTablesDayDTO(nrTablesList[0], day))
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
        val competition = findCompetitions.byId(competitionId)
        val competitionDays = getDaysOfCompetition.execute(competition)
        for (day in competitionDays) {
            registerTablesAvailableFullDay(
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
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Daily start end needs to be set first")
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
        val categoriesInCompetition = getCompetitionCategories.execute(competitionId)
        val startTimeRecords = mutableListOf<ScheduleCategoryRecord>()
        for (category in categoriesInCompetition) {
            try {
                val startTimeRecord = scheduleRepository.getCategoryStartTimeForCategory(category.id)
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
    ): DailyStartAndEndDTO? {
        val record = scheduleRepository.addDailyStartAndEnd(competitionId, dailyStartAndEndSpec)
        return dailyStartEndRecordToDTO(record)
    }

    // This is used as helper method to set up default start/end times when registering a new competition
    fun addDailyStartAndEndForWholeCompetition(
        competitionId: Int
    ) {
        val competition = findCompetitions.byId(competitionId)

        if (ChronoUnit.DAYS.between(competition.startDate, competition.endDate) < 30) {
            var currentDate = competition.startDate
            while (currentDate <= competition.endDate) {

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
    ): DailyStartAndEndDTO? {
        val record = scheduleRepository.updateDailyStartAndEnd(dailyStartAndEndId, competitionId, dailyStartAndEndSpec)
        return dailyStartEndRecordToDTO(record)
    }

    fun getDailyStartAndEnd(competitionId: Int, day: LocalDate): DailyStartAndEndDTO? {
        val record = scheduleRepository.getDailyStartAndEnd(competitionId, day)
        return dailyStartEndRecordToDTO(record)
    }

    fun getDailyStartAndEndForWholeCompetition(competitionId: Int): DailyStartAndEndWithOptionsDTO {
        val records = scheduleRepository.getDailyStartAndEndForCompetition(competitionId)
        val startEndDTOList = records.map { dailyStartEndRecordToDTO(it) }
        val competition = findCompetitions.byId(competitionId)
        return DailyStartAndEndWithOptionsDTO(startEndDTOList, getDaysOfCompetition.execute(competition))
    }

    private fun getStartTimeFormOptions(competitionId: Int): StartTimeFormOptions {
        val competition = findCompetitions.byId(competitionId)
        return StartTimeFormOptions(
            getDaysOfCompetition.execute(competition),
            StartInterval.values().asList()
        )
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
        val competitionCategory =
            findCompetitionCategory.byId(scheduleCategoryRecord.competitonCategoryId)
        return CategoryStartTimeDTO(
            scheduleCategoryRecord.id,
            CompetitionCategory(competitionCategory.id, competitionCategory.category.name, competitionCategory.status),
            scheduleCategoryRecord.playingDay,
            startInterval,
            scheduleCategoryRecord.exactStartTime
        )
    }

    private fun dailyStartEndRecordToDTO(dailyTimesRecord: ScheduleDailyTimesRecord?): DailyStartAndEndDTO? {
        if (dailyTimesRecord == null) {
            return null
        }
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
    val pauseAfterGroupStage: Int,
    val pauseBetweenGroupMatches: Int,
    val pauseBetweenPlayoffMatches: Int
)

data class AvailableTablesDTO(
    val id: Int,
    val nrTables: Int,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate,
    @JsonFormat(pattern = "HH:mm")
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
    val dailyStartEndList: List<DailyStartAndEndDTO?>,
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