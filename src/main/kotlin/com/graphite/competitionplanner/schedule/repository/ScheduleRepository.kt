package com.graphite.competitionplanner.schedule.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.schedule.api.*
import com.graphite.competitionplanner.schedule.domain.ScheduleMatchDto
import com.graphite.competitionplanner.schedule.domain.TimeInterval
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.tables.records.ScheduleAvailableTablesRecord
import com.graphite.competitionplanner.tables.records.ScheduleCategoryRecord
import com.graphite.competitionplanner.tables.records.ScheduleDailyTimesRecord
import com.graphite.competitionplanner.tables.records.ScheduleMetadataRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class ScheduleRepository(private val dslContext: DSLContext): IScheduleRepository {

    // Schedule metadata methods
    fun addScheduleMetadata(
        competitionId: Int,
        scheduleMetadataSpec: ScheduleMetadataSpec
    ): ScheduleMetadataRecord {
        val record = dslContext.newRecord(SCHEDULE_METADATA)
        record.minutesPerMatch = scheduleMetadataSpec.minutesPerMatch
        record.pauseAfterGroupStage = scheduleMetadataSpec.pauseAfterGroupStage
        record.pauseBetweenGroupMatches = scheduleMetadataSpec.pauseBetweenGroupMatches
        record.pauseBetweenPlayoffMatches = scheduleMetadataSpec.pauseBetweenPlayoffMatches
        record.competitionId = competitionId
        record.store()
        return record
    }

    fun getScheduleMetadata(competitionId: Int): ScheduleMetadataRecord {
        return dslContext.selectFrom(SCHEDULE_METADATA)
            .where(SCHEDULE_METADATA.COMPETITION_ID.eq(competitionId))
            .fetchOneInto(SCHEDULE_METADATA) ?: throw NotFoundException("No schedule metadata found for $competitionId")
    }

    fun updateMinutesPerMatch(competitionId: Int, minutesPerMatchSpec: MinutesPerMatchSpec) {
        dslContext.update(SCHEDULE_METADATA)
            .set(SCHEDULE_METADATA.MINUTES_PER_MATCH, minutesPerMatchSpec.minutesPerMatch)
            .where(SCHEDULE_METADATA.COMPETITION_ID.eq(competitionId))
            .execute()
    }

    fun deleteScheduleMetadata(competitionId: Int) {
        dslContext.deleteFrom(SCHEDULE_METADATA)
            .where(SCHEDULE_METADATA.COMPETITION_ID.eq(competitionId))
            .execute()
    }

    fun updateScheduleMetadata(
        scheduleMetadataId: Int,
        competitionId: Int,
        scheduleMetadataSpec: ScheduleMetadataSpec
    ): ScheduleMetadataRecord {
        val record = dslContext.newRecord(SCHEDULE_METADATA)
        record.id = scheduleMetadataId
        record.minutesPerMatch = scheduleMetadataSpec.minutesPerMatch
        record.pauseAfterGroupStage = scheduleMetadataSpec.pauseAfterGroupStage
        record.pauseBetweenGroupMatches = scheduleMetadataSpec.pauseBetweenGroupMatches
        record.pauseBetweenPlayoffMatches = scheduleMetadataSpec.pauseBetweenPlayoffMatches
        record.competitionId = competitionId
        record.update()
        return record
    }

    // Table availability methods
    fun registerTablesAvailable(
        competitionId: Int,
        availableTablesSpec: AvailableTablesSpec
    ): ScheduleAvailableTablesRecord {
        val record = dslContext.newRecord(SCHEDULE_AVAILABLE_TABLES)
        record.nrTables = availableTablesSpec.nrTables
        record.day = availableTablesSpec.day
        record.hour = availableTablesSpec.hour
        record.competitionId = competitionId
        record.store()
        return record
    }

    fun updateTablesAvailable(
        availableTablesId: Int,
        competitionId: Int,
        availableTablesSpec: AvailableTablesSpec
    ): ScheduleAvailableTablesRecord {
        val record = dslContext.newRecord(SCHEDULE_AVAILABLE_TABLES)
        record.id = availableTablesId
        record.nrTables = availableTablesSpec.nrTables
        record.hour = availableTablesSpec.hour
        record.day = availableTablesSpec.day
        record.competitionId = competitionId
        record.update()
        return record
    }

    fun updateTablesAvailableForWholeDay(
        competitionId: Int,
        availableTablesFullDaySpec: AvailableTablesFullDaySpec
    ) {
        dslContext.update(SCHEDULE_AVAILABLE_TABLES)
            .set(SCHEDULE_AVAILABLE_TABLES.NR_TABLES, availableTablesFullDaySpec.nrTables)
            .where(SCHEDULE_AVAILABLE_TABLES.DAY.eq(availableTablesFullDaySpec.day)
                .and(SCHEDULE_AVAILABLE_TABLES.COMPETITION_ID.eq(competitionId)))
            .execute()
    }

    fun getTablesAvailable(competitionId: Int): List<ScheduleAvailableTablesRecord> {
        return dslContext.selectFrom(SCHEDULE_AVAILABLE_TABLES)
            .where(SCHEDULE_AVAILABLE_TABLES.COMPETITION_ID.eq(competitionId))
            .orderBy(SCHEDULE_AVAILABLE_TABLES.DAY, SCHEDULE_AVAILABLE_TABLES.HOUR)
            .fetchInto(SCHEDULE_AVAILABLE_TABLES)
    }

    fun getTablesAvailableByDay(competitionId: Int, day: LocalDate): List<ScheduleAvailableTablesRecord> {
        return dslContext.selectFrom(SCHEDULE_AVAILABLE_TABLES)
            .where(SCHEDULE_AVAILABLE_TABLES.COMPETITION_ID.eq(competitionId).and(SCHEDULE_AVAILABLE_TABLES.DAY.eq(day)))
            .orderBy(SCHEDULE_AVAILABLE_TABLES.HOUR)
            .fetchInto(SCHEDULE_AVAILABLE_TABLES)
    }

    fun deleteTablesAvailable(availableTablesId: Int) {
        dslContext.deleteFrom(SCHEDULE_AVAILABLE_TABLES)
            .where(SCHEDULE_AVAILABLE_TABLES.ID.eq(availableTablesId))
            .execute()
    }

    // Category start time methods -- sets day and time for when category is held
    fun addCategoryStartTime(competitionCategoryId: Int, categoryStartTimeSpec: CategoryStartTimeSpec): ScheduleCategoryRecord {
        val record = dslContext.newRecord(SCHEDULE_CATEGORY)
        val startInterval = if (categoryStartTimeSpec.startInterval == null) {
            null
        }
        else {
            categoryStartTimeSpec.startInterval.name
        }
        record.playingDay = categoryStartTimeSpec.playingDay
        record.startInterval = startInterval // Enum to string
        record.exactStartTime = categoryStartTimeSpec.exactStartTime
        record.competitonCategoryId = competitionCategoryId
        record.store()
        return record
    }

    fun getCategoryStartTimeForCategory(competitionCategoryId: Int): ScheduleCategoryRecord {
        return dslContext.selectFrom(SCHEDULE_CATEGORY)
            .where(SCHEDULE_CATEGORY.COMPETITON_CATEGORY_ID.eq(competitionCategoryId))
            .fetchSingleInto(SCHEDULE_CATEGORY)
    }

    // Currently sorted on day but should be sorted on parts of day and time as well
    fun getAllCategoryStartTimesInCompetition(competitionId: Int): List<ScheduleCategoryRecord> {
        return dslContext
            .select(SCHEDULE_CATEGORY.ID, SCHEDULE_CATEGORY.PLAYING_DAY, SCHEDULE_CATEGORY.START_INTERVAL,
        SCHEDULE_CATEGORY.EXACT_START_TIME, SCHEDULE_CATEGORY.COMPETITON_CATEGORY_ID)
            .from(COMPETITION)
            .join(COMPETITION_CATEGORY).on(COMPETITION_CATEGORY.COMPETITION_ID.eq(COMPETITION.ID))
            .join(SCHEDULE_CATEGORY).on(SCHEDULE_CATEGORY.COMPETITON_CATEGORY_ID.eq(COMPETITION_CATEGORY.ID))
            .where(COMPETITION.ID.eq(competitionId))
            .orderBy(SCHEDULE_CATEGORY.PLAYING_DAY)
            .fetchInto(SCHEDULE_CATEGORY)
    }

    fun updateCategoryStartTime(
        scheduleStartTimeId: Int,
        competitionCategoryId: Int,
        categoryStartTimeSpec: CategoryStartTimeSpec
    ): ScheduleCategoryRecord {
        val record = dslContext.newRecord(SCHEDULE_CATEGORY)

        val startInterval = if (categoryStartTimeSpec.startInterval == null) {
            null
        }
        else {
            categoryStartTimeSpec.startInterval.name
        }

        record.id = scheduleStartTimeId
        record.playingDay = categoryStartTimeSpec.playingDay
        record.startInterval = startInterval // Enum to string
        record.exactStartTime = categoryStartTimeSpec.exactStartTime
        record.competitonCategoryId = competitionCategoryId
        record.update()
        return record
    }

    fun deleteCategoryStartTime(scheduleStartTimeId: Int) {
        dslContext
            .deleteFrom(SCHEDULE_CATEGORY)
            .where(SCHEDULE_CATEGORY.ID.eq(scheduleStartTimeId))
            .execute()
    }

    // Daily start and end times for whole competition
    fun addDailyStartAndEnd(
        competitionId: Int,
        dailyStartAndEndSpec: DailyStartAndEndSpec
    ): ScheduleDailyTimesRecord {
        val record = dslContext.newRecord(SCHEDULE_DAILY_TIMES)
        record.day = dailyStartAndEndSpec.day
        record.startTime = dailyStartAndEndSpec.startTime
        record.endTime = dailyStartAndEndSpec.endTime
        record.competitionId = competitionId
        record.store()
        return record
    }

    fun updateDailyStartAndEnd(
        dailyStartAndEndId: Int,
        competitionId: Int,
        dailyStartAndEndSpec: DailyStartAndEndSpec
    ): ScheduleDailyTimesRecord  {
        val record = dslContext.newRecord(SCHEDULE_DAILY_TIMES)
        record.id = dailyStartAndEndId
        record.day = dailyStartAndEndSpec.day
        record.startTime = dailyStartAndEndSpec.startTime
        record.endTime = dailyStartAndEndSpec.endTime
        record.update()
        return record
    }

    fun getDailyStartAndEnd(competitionId: Int, day: LocalDate): ScheduleDailyTimesRecord? {
        return dslContext.selectFrom(SCHEDULE_DAILY_TIMES)
            .where(SCHEDULE_DAILY_TIMES.COMPETITION_ID.eq(competitionId).and(SCHEDULE_DAILY_TIMES.DAY.eq(day)))
            .fetchOneInto(SCHEDULE_DAILY_TIMES)
    }

    fun deleteDailyStartAndEnd(dailyStartAndEndId: Int) {
        dslContext.deleteFrom(SCHEDULE_DAILY_TIMES)
            .where(SCHEDULE_DAILY_TIMES.ID.eq(dailyStartAndEndId))
            .execute()
    }

    fun getDailyStartAndEndForCompetition(competitionId: Int): List<ScheduleDailyTimesRecord> {
        return dslContext.selectFrom(SCHEDULE_DAILY_TIMES)
            .where(SCHEDULE_DAILY_TIMES.COMPETITION_ID.eq(competitionId))
            .fetchInto(SCHEDULE_DAILY_TIMES)
    }

    override fun getMatchesIn(competitionId: Int, date: LocalDate, timeInterval: TimeInterval): List<ScheduleMatchDto> {
//        dslContext.select(MATCH.ID)
//        .from(MATCH)
//        .join(PRE_SCHEDULE).on(MATCH.COMPETITION_CATEGORY_ID.eq(PRE_SCHEDULE.COMPETITION_CATEGORY))
//        .where(PRE_SCHEDULE.COMPETITION_ID.eq(competitionId)
//        .and(PRE_SCHEDULE.DATE.eq(date)
//        .and(PRE_SCHEDULE.TIME_INTERVAL.eq(timeInterval)))
        TODO("Not yet implemented")
    }
}