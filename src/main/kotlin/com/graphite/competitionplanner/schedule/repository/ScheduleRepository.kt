package com.graphite.competitionplanner.schedule.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.common.repository.BaseRepository
import com.graphite.competitionplanner.match.domain.MatchType
import com.graphite.competitionplanner.schedule.api.*
import com.graphite.competitionplanner.schedule.interfaces.*
import com.graphite.competitionplanner.tables.records.*
import org.jooq.DSLContext
import org.jooq.Record6
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class ScheduleRepository(
    dslContext: DSLContext
) : BaseRepository(dslContext),
    IScheduleRepository
{

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
                .fetchOneInto(SCHEDULE_AVAILABLE_TABLES)
                ?: throw NotFoundException("No available tables registration found for $competitionId on day $day")
    }

    fun deleteTablesAvailable(availableTablesId: Int) {
        dslContext.deleteFrom(SCHEDULE_AVAILABLE_TABLES)
                .where(SCHEDULE_AVAILABLE_TABLES.ID.eq(availableTablesId))
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
            competitionId: Int,
            dailyStartAndEndSpec: DailyStartAndEndSpec
    ) {
        dslContext.update(SCHEDULE_DAILY_TIMES)
                .set(SCHEDULE_DAILY_TIMES.START_TIME, dailyStartAndEndSpec.startTime)
                .set(SCHEDULE_DAILY_TIMES.END_TIME, dailyStartAndEndSpec.endTime)
                .where(SCHEDULE_DAILY_TIMES.COMPETITION_ID.eq(competitionId).and(SCHEDULE_DAILY_TIMES.DAY.eq(dailyStartAndEndSpec.day)))
                .execute()
    }

    fun getDailyStartAndEnd(competitionId: Int, day: LocalDate): ScheduleDailyTimesRecord {
        return dslContext.selectFrom(SCHEDULE_DAILY_TIMES)
                .where(SCHEDULE_DAILY_TIMES.COMPETITION_ID.eq(competitionId).and(SCHEDULE_DAILY_TIMES.DAY.eq(day)))
                .fetchOneInto(SCHEDULE_DAILY_TIMES)
                ?: throw NotFoundException("No daily start and end found for $competitionId on day $day")
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

    override fun storeTimeTable(competitionId: Int, timeTable: List<TimeTableSlotSpec>) {
        val records = timeTable.map { it.toRecord(competitionId) }
        try {
            dslContext.batchStore(records).execute()
        } catch (exception: DuplicateKeyException) {
            logger.warn("Exception message: ${exception.message}")
            throw RuntimeException("Failed to store time table for competition id $competitionId")

        }
    }

    override fun setCategoryForTimeSlots(timeSlotIds: List<Int>, categoryId: Int, matchType: MatchType) {
        dslContext
                .update(MATCH_TIME_SLOT)
                .set(MATCH_TIME_SLOT.COMPETITION_CATEGORY_ID, categoryId)
                .set(MATCH_TIME_SLOT.MATCH_TYPE, matchType.name)
                .where(MATCH_TIME_SLOT.ID.`in`(timeSlotIds))
                .execute()
    }

    override fun removeCategoryAndMatchTypeFromTimeslots(categoryId: Int, matchType: MatchType) {
        dslContext
                .update(MATCH_TIME_SLOT)
                .setNull(MATCH_TIME_SLOT.COMPETITION_CATEGORY_ID)
                .setNull(MATCH_TIME_SLOT.MATCH_TYPE)
                .where(MATCH_TIME_SLOT.COMPETITION_CATEGORY_ID.eq(categoryId).and(MATCH_TIME_SLOT.MATCH_TYPE.eq(matchType.name)))
                .execute()
    }

    override fun resetTimeSlotsForCompetition(competitionId: Int) {
        dslContext
                .update(MATCH_TIME_SLOT)
                .setNull(MATCH_TIME_SLOT.COMPETITION_CATEGORY_ID)
                .setNull(MATCH_TIME_SLOT.MATCH_TYPE)
                .where(MATCH_TIME_SLOT.COMPETITION_ID.eq(competitionId))
                .execute()
    }

    override fun getTimeSlotsForCategory(categoryId: Int): List<MatchTimeSlotRecord> {
        return dslContext
                .selectFrom(MATCH_TIME_SLOT)
                .where(MATCH_TIME_SLOT.COMPETITION_CATEGORY_ID.eq(categoryId))
                .fetchInto(MATCH_TIME_SLOT)
    }

    override fun getTimeSlotsForCompetition(competitionId: Int): List<MatchTimeSlotRecord> {
        return dslContext
                .selectFrom(MATCH_TIME_SLOT)
                .where(MATCH_TIME_SLOT.COMPETITION_ID.eq(competitionId))
                .fetchInto(MATCH_TIME_SLOT)
    }

    override fun clearTimeSlotTable() {
        dslContext.deleteFrom(MATCH_TIME_SLOT).execute()
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

    override fun removeCategoryTimeSlotFromMatchTable(categoryId: Int, matchType: MatchType) {
        dslContext
                .update(MATCH)
                .setNull(MATCH.MATCH_TIME_SLOT_ID)
                .where(MATCH.COMPETITION_CATEGORY_ID.eq(categoryId).and(MATCH.MATCH_TYPE.eq(matchType.name)))
                .execute()
    }

    override fun getScheduleMatches(competitionCategoryId: Int, matchType: MatchType): List<ScheduleMatchDto> {
        val matches = dslContext.select(
                MATCH.ID,
                MATCH.COMPETITION_CATEGORY_ID,
                MATCH.FIRST_REGISTRATION_ID,
                MATCH.SECOND_REGISTRATION_ID,
                MATCH.GROUP_OR_ROUND
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
                    getPlayerIdsForRegistrationId(it.get(MATCH.SECOND_REGISTRATION_ID)),
                    it.get(MATCH.GROUP_OR_ROUND)
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

    override fun publishSchedule(competitionId: Int) {
        dslContext.update(MATCH)
                .set(
                        MATCH.START_TIME,
                        dslContext.select(MATCH_TIME_SLOT.START_TIME)
                                .from(MATCH_TIME_SLOT)
                                .where(MATCH.MATCH_TIME_SLOT_ID.eq(MATCH_TIME_SLOT.ID))
                                .and(MATCH_TIME_SLOT.COMPETITION_ID.eq(competitionId)))
                .execute()
    }

    override fun clearSchedule(competitionId: Int) {
        dslContext.update(MATCH)
                .setNull(MATCH.MATCH_TIME_SLOT_ID)
                .setNull(MATCH.START_TIME)
                .from(MATCH_TIME_SLOT)
                .where(MATCH.MATCH_TIME_SLOT_ID.eq(MATCH_TIME_SLOT.ID)
                        .and(MATCH_TIME_SLOT.COMPETITION_ID.eq(competitionId)))
                .execute()
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