package com.graphite.competitionplanner.schedule.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.tables.records.ScheduleAvailableTablesRecord
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class AvailableTablesService(
    val scheduleRepository: ScheduleRepository,
    val findCompetitions: FindCompetitions,
    val getDaysOfCompetition: GetDaysOfCompetition
) {
    fun registerTablesAvailable(
        competitionId: Int,
        availableTablesSpec: AvailableTablesSpec
    ): AvailableTablesDTO {
        val tablesRecord = scheduleRepository.registerTablesAvailable(competitionId, availableTablesSpec)
        return availableTablesRecordToDTO(tablesRecord)
    }

    fun updateTablesAvailable(
        competitionId: Int,
        availableTablesSpec: AvailableTablesSpec
    ): AvailableTablesDTO {
          val updatedTable =  scheduleRepository.updateTablesAvailable(competitionId, availableTablesSpec)
        return availableTablesRecordToDTO(updatedTable)
    }

    fun getTablesAvailable(competitionId: Int): List<AvailableTablesDTO> {
        val tablesRecord = scheduleRepository.getTablesAvailable(competitionId)
        return tablesRecord.map { availableTablesRecordToDTO(it) }
    }

    fun getTablesAvailableByDay(competitionId: Int, day: LocalDate): AvailableTablesDTO {
        val tablesRecord = scheduleRepository.getTablesAvailableByDay(competitionId, day)
        return availableTablesRecordToDTO(tablesRecord)
    }

    /**
     * Used in selection table on website.
     */
    fun getTablesAvailableForMainTable(competitionId: Int): List<AvailableTablesDayDTO> {
        val competition = findCompetitions.byId(competitionId)
        val competitionDays = getDaysOfCompetition.execute(competition)
        val availableTablesDayList = mutableListOf<AvailableTablesDayDTO>()
        for (day in competitionDays) {
            val availableTablesDTO: AvailableTablesDTO = getTablesAvailableByDay(competitionId, day)
            availableTablesDayList.add(AvailableTablesDayDTO(availableTablesDTO.nrTables, availableTablesDTO.day))
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
            registerTablesAvailable(competitionId, AvailableTablesSpec(availableTablesWholeCompetitionSpec.nrTables, day))
        }
    }

    private fun availableTablesRecordToDTO(availableTablesRecord: ScheduleAvailableTablesRecord): AvailableTablesDTO {
        return AvailableTablesDTO(
            availableTablesRecord.id,
            availableTablesRecord.nrTables,
            availableTablesRecord.day,
        )
    }
}

data class AvailableTablesDTO(
    val id: Int,
    val nrTables: Int,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate,
)

data class AvailableTablesDayDTO(
    val nrTables: Int,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val day: LocalDate
)
