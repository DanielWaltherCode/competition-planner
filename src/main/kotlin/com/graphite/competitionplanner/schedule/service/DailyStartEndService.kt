package com.graphite.competitionplanner.schedule.service

import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.schedule.api.DailyStartAndEndSpec
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.tables.records.ScheduleDailyTimesRecord
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit

@Service
class DailyStartEndService(
    val scheduleRepository: ScheduleRepository,
val findCompetitions: FindCompetitions,
val getDaysOfCompetition: GetDaysOfCompetition) {

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
        competitionId: Int,
        dailyStartAndEndSpec: DailyStartAndEndSpec
    ) {
        scheduleRepository.updateDailyStartAndEnd(competitionId, dailyStartAndEndSpec)
    }

    fun getDailyStartAndEnd(competitionId: Int, day: LocalDate): DailyStartAndEndDTO {
        val record = scheduleRepository.getDailyStartAndEnd(competitionId, day)
        return dailyStartEndRecordToDTO(record)
    }

    fun getDailyStartAndEndForWholeCompetition(competitionId: Int): DailyStartAndEndWithOptionsDTO {
        val records = scheduleRepository.getDailyStartAndEndForCompetition(competitionId)
        val startEndDTOList = records.map { dailyStartEndRecordToDTO(it) }
        val competition = findCompetitions.byId(competitionId)
        return DailyStartAndEndWithOptionsDTO(startEndDTOList.sortedBy { it.day }, getDaysOfCompetition.execute(competition))
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