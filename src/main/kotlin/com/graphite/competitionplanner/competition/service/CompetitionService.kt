package com.graphite.competitionplanner.competition.service

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.UpdateCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.CompetitionUpdateSpec
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CompetitionService(
    val scheduleService: ScheduleService,
    val findCompetitions: FindCompetitions,
    val createCompetition: CreateCompetition,
    val updateCompetition: UpdateCompetition
) {

    fun addCompetition(competitionSpec: CompetitionSpec): CompetitionDTO {
        val competition = createCompetition.execute(competitionSpec)

        // Add default competition schedule metadata
        scheduleService.addDefaultScheduleMetadata(competition.id)
        scheduleService.addDailyStartAndEndForWholeCompetition(competition.id)
        scheduleService.registerTablesAvailableForWholeCompetition(
            competition.id,
            AvailableTablesWholeCompetitionSpec(0)
        )

        return competition
    }

    fun getByDate(weekStartDate: LocalDate?, weekEndDate: LocalDate?): List<CompetitionWithClubDTO> {
        var start = LocalDate.now()
        if (weekStartDate != null) {
            start = weekStartDate
        }
        var end = LocalDate.now().plusMonths(1)
        if (weekEndDate != null) {
            end = weekEndDate
        }
        return findCompetitions.thatStartOrEndWithin(start, end)
    }

    fun getById(competitionId: Int): CompetitionDTO {
        return findCompetitions.byId(competitionId)
    }

    fun getByClubId(clubId: Int): List<CompetitionDTO> {
        return findCompetitions.thatBelongsTo(clubId)
    }

    fun getDaysOfCompetition(competitionId: Int): List<LocalDate> {
        val competition = findCompetitions.byId(competitionId)
        val dates = mutableListOf<LocalDate>()

        var currentDate = competition.startDate

        while (currentDate <= competition.endDate) {
            dates.add(LocalDate.from(currentDate))
            currentDate = currentDate.plusDays(1)
        }
        return dates
    }
}
