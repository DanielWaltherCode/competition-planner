package com.graphite.competitionplanner.schedule.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategory
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.schedule.api.*
import com.graphite.competitionplanner.schedule.domain.*
import com.graphite.competitionplanner.schedule.interfaces.*
import com.graphite.competitionplanner.schedule.service.StartInterval
import com.graphite.competitionplanner.tables.records.*
import org.jooq.DSLContext
import org.jooq.Record6
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class ScheduleRepository(private val dslContext: DSLContext) : IScheduleRepository {

    private val logger = LoggerFactory.getLogger(javaClass)

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
        record.competitionId = competitionId
        record.store()
        return record
    }

    fun updateTablesAvailable(
            competitionId: Int,
            availableTablesSpec: AvailableTablesSpec
    ): ScheduleAvailableTablesRecord {
        dslContext.update(SCHEDULE_AVAILABLE_TABLES)
                .set(SCHEDULE_AVAILABLE_TABLES.NR_TABLES, availableTablesSpec.nrTables)
                .where(
                        SCHEDULE_AVAILABLE_TABLES.DAY.eq(availableTablesSpec.day)
                                .and(SCHEDULE_AVAILABLE_TABLES.COMPETITION_ID.eq(competitionId))
                )
                .execute()
        return getTablesAvailableByDay(competitionId, availableTablesSpec.day)

    }

    fun getTablesAvailable(competitionId: Int): List<ScheduleAvailableTablesRecord> {
        return dslContext.selectFrom(SCHEDULE_AVAILABLE_TABLES)
                .where(SCHEDULE_AVAILABLE_TABLES.COMPETITION_ID.eq(competitionId))
                .orderBy(SCHEDULE_AVAILABLE_TABLES.DAY)
                .fetchInto(SCHEDULE_AVAILABLE_TABLES)
    }

    fun getTablesAvailableByDay(competitionId: Int, day: LocalDate): ScheduleAvailableTablesRecord {
        return dslContext.selectFrom(SCHEDULE_AVAILABLE_TABLES)
                .where(
                        SCHEDULE_AVAILABLE_TABLES.COMPETITION_ID.eq(competitionId).and(SCHEDULE_AVAILABLE_TABLES.DAY.eq(day))
                )
                .fetchOneInto(SCHEDULE_AVAILABLE_TABLES) ?: throw NotFoundException("No available tables registration found for $competitionId on day $day")
    }

    fun deleteTablesAvailable(availableTablesId: Int) {
        dslContext.deleteFrom(SCHEDULE_AVAILABLE_TABLES)
                .where(SCHEDULE_AVAILABLE_TABLES.ID.eq(availableTablesId))
                .execute()
    }

    // Category start time methods -- sets day and time for when category is held
    fun addCategoryStartTime(
            competitionCategoryId: Int,
            categoryStartTimeSpec: CategoryStartTimeSpec
    ): ScheduleCategoryRecord {
        val record = dslContext.newRecord(SCHEDULE_CATEGORY)
        val startInterval = if (categoryStartTimeSpec.startInterval == null) {
            null
        } else {
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
                .select(
                        SCHEDULE_CATEGORY.ID, SCHEDULE_CATEGORY.PLAYING_DAY, SCHEDULE_CATEGORY.START_INTERVAL,
                        SCHEDULE_CATEGORY.EXACT_START_TIME, SCHEDULE_CATEGORY.COMPETITON_CATEGORY_ID
                )
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
        } else {
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
    ): ScheduleDailyTimesRecord {
        val record = dslContext.newRecord(SCHEDULE_DAILY_TIMES)
        record.id = dailyStartAndEndId
        record.day = dailyStartAndEndSpec.day
        record.startTime = dailyStartAndEndSpec.startTime
        record.endTime = dailyStartAndEndSpec.endTime
        record.update()
        return record
    }

    fun getDailyStartAndEnd(competitionId: Int, day: LocalDate): ScheduleDailyTimesRecord {
        return dslContext.selectFrom(SCHEDULE_DAILY_TIMES)
                .where(SCHEDULE_DAILY_TIMES.COMPETITION_ID.eq(competitionId).and(SCHEDULE_DAILY_TIMES.DAY.eq(day)))
                .fetchOneInto(SCHEDULE_DAILY_TIMES) ?: throw NotFoundException("No daily start and end found for $competitionId on day $day")
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

    override fun getPreScheduledMatches(
            competitionId: Int,
            date: LocalDate,
            timeInterval: StartInterval
    ): List<ScheduleMatchDto> {
        val matches = dslContext.select(
                MATCH.ID,
                MATCH.COMPETITION_CATEGORY_ID,
                MATCH.FIRST_REGISTRATION_ID,
                MATCH.SECOND_REGISTRATION_ID
        )
                .from(MATCH)
                .join(PRE_SCHEDULE).on(MATCH.COMPETITION_CATEGORY_ID.eq(PRE_SCHEDULE.COMPETITION_CATEGORY_ID))
                .where(
                        PRE_SCHEDULE.COMPETITION_ID.eq(competitionId)
                                .and(PRE_SCHEDULE.PLAY_DATE.eq(date))
                                .and(PRE_SCHEDULE.TIME_INTERVAL.eq(timeInterval.name))
                )

        return matches.map {
            ScheduleMatchDto(
                    it.get(MATCH.ID),
                    it.get(MATCH.COMPETITION_CATEGORY_ID),
                    getPlayerIdsForRegistrationId(it.get(MATCH.FIRST_REGISTRATION_ID)),
                    getPlayerIdsForRegistrationId(it.get(MATCH.SECOND_REGISTRATION_ID))
            )
        }

    }

    override fun storePreSchedule(competitionId: Int, competitionCategoryId: Int, spec: PreScheduleSpec) {
        try {
            dslContext.insertInto(
                    PRE_SCHEDULE,
                    PRE_SCHEDULE.COMPETITION_ID,
                    PRE_SCHEDULE.PLAY_DATE,
                    PRE_SCHEDULE.TIME_INTERVAL,
                    PRE_SCHEDULE.COMPETITION_CATEGORY_ID
            )
                    .values(competitionId, spec.playDate, spec.timeInterval.name, competitionCategoryId)
                    .execute()
        } catch (_: DuplicateKeyException) {
            // If user tries to store a new pre-schedule settings for an already pre-scheduled competition category we
            // instead update the settings.
            dslContext.update(PRE_SCHEDULE)
                    .set(PRE_SCHEDULE.PLAY_DATE, spec.playDate)
                    .set(PRE_SCHEDULE.TIME_INTERVAL, spec.timeInterval.name)
                    .where(PRE_SCHEDULE.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
                    .execute()
        }
    }

    override fun getPreSchedule(competitionId: Int): List<CompetitionCategoryPreSchedule> {
        val records = dslContext.select(
                PRE_SCHEDULE.PLAY_DATE,
                PRE_SCHEDULE.TIME_INTERVAL,
                PRE_SCHEDULE.COMPETITION_CATEGORY_ID,
                PRE_SCHEDULE.ESTIMATED_END_TIME,
                PRE_SCHEDULE.SUCCESS,
                COMPETITION_CATEGORY.STATUS,
                CATEGORY.CATEGORY_NAME
        )
                .from(PRE_SCHEDULE)
                .join(COMPETITION_CATEGORY).on(PRE_SCHEDULE.COMPETITION_CATEGORY_ID.eq(COMPETITION_CATEGORY.ID))
                .join(CATEGORY).on(COMPETITION_CATEGORY.CATEGORY.eq(CATEGORY.ID))
                .where(PRE_SCHEDULE.COMPETITION_ID.eq(competitionId))
                .fetch()

        return records.map {
            CompetitionCategoryPreSchedule(
                    it.get(PRE_SCHEDULE.SUCCESS),
                    it.get(PRE_SCHEDULE.ESTIMATED_END_TIME),
                    it.get(PRE_SCHEDULE.PLAY_DATE),
                    StartInterval.valueOf(it.get(PRE_SCHEDULE.TIME_INTERVAL)),
                    CompetitionCategory(
                            it.get(PRE_SCHEDULE.COMPETITION_CATEGORY_ID),
                            it.get(COMPETITION_CATEGORY.STATUS),
                            it.get(CATEGORY.CATEGORY_NAME)
                    )
            )
        }
    }

    override fun update(competitionCategoryIds: List<Int>, estimatedEndTime: LocalDateTime, success: Boolean) {
        dslContext.update(PRE_SCHEDULE)
                .set(PRE_SCHEDULE.ESTIMATED_END_TIME, estimatedEndTime)
                .set(PRE_SCHEDULE.SUCCESS, success)
                .where(PRE_SCHEDULE.COMPETITION_CATEGORY_ID.`in`(competitionCategoryIds))
                .execute()
    }

    override fun storeTimeTable(competitionId: Int, timeTable: List<TimeTableSlotSpec>) {
        val records = timeTable.map { it.toRecord(competitionId) }
        try {
            dslContext.batchInsert(records).execute()
        } catch (exception: DuplicateKeyException) {
            logger.error("Exception message: ${exception.message}")
            throw RuntimeException("Failed to store time table for competition id $competitionId")
        }
    }

    override fun deleteTimeTable(competitionId: Int) {
        dslContext.deleteFrom(MATCH_TIME_SLOT)
                .where(MATCH_TIME_SLOT.COMPETITION_ID.eq(competitionId))
                .execute()
    }

    override fun getTimeTable(competitionId: Int): List<TimeTableSlotToMatch> {
        val records = dslContext.select(
                MATCH_TIME_SLOT.ID,
                MATCH_TIME_SLOT.START_TIME,
                MATCH_TIME_SLOT.LOCATION,
                MATCH_TIME_SLOT.TABLE_NUMBER,
                MATCH.ID,
                MATCH.COMPETITION_CATEGORY_ID
        )
                .from(MATCH_TIME_SLOT)
                .leftJoin(MATCH).on(MATCH.MATCH_TIME_SLOT_ID.eq(MATCH_TIME_SLOT.ID))
                .where(MATCH_TIME_SLOT.COMPETITION_ID.eq(competitionId))
                .orderBy(MATCH_TIME_SLOT.ID.asc())

        return records.map { it.toTimeTableSlotToMatch() }
    }

    override fun addMatchToTimeTableSlot(spec: MapMatchToTimeTableSlotSpec): List<MatchToTimeTableSlot> {
        dslContext.update(MATCH)
                .set(MATCH.MATCH_TIME_SLOT_ID, spec.timeTableSlotId)
                .where(MATCH.ID.eq(spec.matchId))
                .execute()

        val records = dslContext.select(
                MATCH_TIME_SLOT.ID,
                MATCH_TIME_SLOT.START_TIME,
                MATCH_TIME_SLOT.LOCATION,
                MATCH_TIME_SLOT.TABLE_NUMBER,
                MATCH.ID,
                MATCH.COMPETITION_CATEGORY_ID
        )
                .from(MATCH_TIME_SLOT)
                .leftJoin(MATCH).on(MATCH.MATCH_TIME_SLOT_ID.eq(MATCH_TIME_SLOT.ID))
                .where(MATCH_TIME_SLOT.ID.eq(spec.timeTableSlotId))

        return records.map { it.toMatchToTimeTableSlot() }
    }

    override fun updateMatchesTimeTablesSlots(matchTimeTableSlots: List<MapMatchToTimeTableSlotSpec>) {
        val records = matchTimeTableSlots.map { it.toRecord() }
        dslContext.batchUpdate(records).execute()
    }

    override fun getScheduleMatches(competitionCategoryId: Int, matchType: MatchType): List<ScheduleMatchDto> {
        val matches = dslContext.select(
                MATCH.ID,
                MATCH.COMPETITION_CATEGORY_ID,
                MATCH.FIRST_REGISTRATION_ID,
                MATCH.SECOND_REGISTRATION_ID
        )
                .from(MATCH)
                .where(
                        MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                                .and(MATCH.MATCH_TYPE.eq(matchType.name))
                )

        return matches.map {
            ScheduleMatchDto(
                    it.get(MATCH.ID),
                    it.get(MATCH.COMPETITION_CATEGORY_ID),
                    getPlayerIdsForRegistrationId(it.get(MATCH.FIRST_REGISTRATION_ID)),
                    getPlayerIdsForRegistrationId(it.get(MATCH.SECOND_REGISTRATION_ID))
            )
        }
    }

    override fun getTimeTableSlotRecords(
            competitionId: Int,
            startTime: LocalDateTime,
            tableNumbers: List<Int>,
            location: String
    ): List<MatchTimeSlotRecord> {
        return dslContext.selectFrom(MATCH_TIME_SLOT)
                .where(
                        MATCH_TIME_SLOT.COMPETITION_ID.eq(competitionId)
                                .and(MATCH_TIME_SLOT.LOCATION.eq(location))
                                .and(MATCH_TIME_SLOT.TABLE_NUMBER.`in`(tableNumbers))
                                .and(MATCH_TIME_SLOT.START_TIME.greaterOrEqual(startTime))
                )
                .orderBy(MATCH_TIME_SLOT.START_TIME.asc(), MATCH_TIME_SLOT.TABLE_NUMBER.asc())
                .fetch()
    }

    private fun MapMatchToTimeTableSlotSpec.toRecord(): MatchRecord {
        val record = dslContext.newRecord(MATCH)
        record.id = this.matchId
        record.matchTimeSlotId = this.timeTableSlotId
        return record
    }

    private fun Record6<Int, LocalDateTime, String, Int, Int, Int>.toMatchToTimeTableSlot(): MatchToTimeTableSlot {
        return MatchToTimeTableSlot(
                this.get(MATCH.ID),
                this.get(MATCH.COMPETITION_CATEGORY_ID),
                this.get(MATCH_TIME_SLOT.ID),
                this.get(MATCH_TIME_SLOT.START_TIME),
                this.get(MATCH_TIME_SLOT.TABLE_NUMBER),
                this.get(MATCH_TIME_SLOT.LOCATION),
        )
    }

    private fun Record6<Int, LocalDateTime, String, Int, Int, Int>.toTimeTableSlotToMatch(): TimeTableSlotToMatch {

        val matchId = this.get(MATCH.ID)
        val competitionCategoryId = this.get(MATCH.COMPETITION_CATEGORY_ID)

        val matchInfo: TimeTableSlotMatchInfo? = if (matchId != null) {
            TimeTableSlotMatchInfo(
                    matchId,
                    competitionCategoryId
            )
        } else {
            null
        }

        return TimeTableSlotToMatch(
                this.get(MATCH_TIME_SLOT.ID),
                this.get(MATCH_TIME_SLOT.START_TIME),
                this.get(MATCH_TIME_SLOT.TABLE_NUMBER),
                this.get(MATCH_TIME_SLOT.LOCATION),
                matchInfo
        )
    }

    private fun TimeTableSlotSpec.toRecord(competitionId: Int): MatchTimeSlotRecord {
        val record = dslContext.newRecord(MATCH_TIME_SLOT)
        record.competitionId = competitionId
        record.startTime = this.startTime
        record.tableNumber = this.tableNumber
        record.location = this.location
        return record
    }

    private fun getPlayerIdsForRegistrationId(registrationId: Int): List<Int> {
        val records = dslContext.select(PLAYER_REGISTRATION.PLAYER_ID)
                .from(PLAYER_REGISTRATION)
                .where(PLAYER_REGISTRATION.REGISTRATION_ID.eq(registrationId))
        return records.map { it.get(PLAYER_REGISTRATION.PLAYER_ID) }
    }
}