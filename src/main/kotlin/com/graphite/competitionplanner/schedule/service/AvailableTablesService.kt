package com.graphite.competitionplanner.schedule.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.schedule.api.AvailableTablesFullDaySpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.tables.records.ScheduleAvailableTablesRecord
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class AvailableTablesService(
    val scheduleRepository: ScheduleRepository,
    val findCompetitions: FindCompetitions,
    val getDaysOfCompetition: GetDaysOfCompetition,
    val dailyStartEndService: DailyStartEndService

) {
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
        val dailyStartAndEndDTO = dailyStartEndService.getDailyStartAndEnd(competitionId, availableTablesFullDaySpec.day)
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

    private fun availableTablesRecordToDTO(availableTablesRecord: ScheduleAvailableTablesRecord): AvailableTablesDTO {
        return AvailableTablesDTO(
            availableTablesRecord.id,
            availableTablesRecord.nrTables,
            availableTablesRecord.day,
            availableTablesRecord.hour
        )
    }
}

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
