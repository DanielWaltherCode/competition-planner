package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.common.toList
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.CompetitionUpdateSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.schedule.api.AvailableTablesSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.api.DailyStartAndEndSpec
import com.graphite.competitionplanner.schedule.domain.TimeTableSlotHandler
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.schedule.service.DailyStartEndService
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalTime

@Component
class UpdateCompetition(
    val repository: ICompetitionRepository,
    val timeTableSlotHandler: TimeTableSlotHandler,
    val dailyStartEndService: DailyStartEndService,
    val availableTablesService: AvailableTablesService,
    val scheduleRepository: ScheduleRepository
) {
    fun execute(id: Int, spec: CompetitionUpdateSpec): CompetitionDTO {
        val competitionDTO = repository.update(id, spec)
        updateThingsDependentOnCompetitionDays(id, spec)
        return competitionDTO
    }

    fun updateThingsDependentOnCompetitionDays(competitionId: Int, competitionSpec: CompetitionUpdateSpec) {
        val currentDays = dailyStartEndService.getDailyStartAndEndForWholeCompetition(competitionId)
        val availableTables = availableTablesService.getTablesAvailable(competitionId)
        val allNewDaysOfCompetition = (competitionSpec.startDate .. competitionSpec.endDate).toList()

        // Days of competition have not been updated
        if (currentDays.availableDays == allNewDaysOfCompetition) {
            return
        }

        // If an old day is not among new days, delete values for that one
        for(day in currentDays.availableDays) {
            if (!allNewDaysOfCompetition.contains(day)) {
                scheduleRepository.deleteDailyStartAndEnd(currentDays.dailyStartEndList.first{it.day == day }.id)
                scheduleRepository.deleteTablesAvailable(availableTables.first { it.day == day }.id)
            }
        }

        for (day in allNewDaysOfCompetition) {
            // If there is a current dailyStartEnd, don't overwrite with default
            val currentDailyStartEnd = currentDays.dailyStartEndList.firstOrNull { it.day == day }
            if (currentDailyStartEnd == null) {
                dailyStartEndService.addDailyStartAndEnd(competitionId,
                        DailyStartAndEndSpec(day, LocalTime.of(9, 0), LocalTime.of(18, 0)))
            }

            val currentTablesAvailable = availableTables.firstOrNull{it.day == day}
            if (currentTablesAvailable == null) {
                availableTablesService.registerTablesAvailable(competitionId, AvailableTablesSpec(0, day))
            }
        }
        // Update time slots
        timeTableSlotHandler.init(competitionId)
    }
}