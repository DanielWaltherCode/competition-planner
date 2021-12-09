package com.graphite.competitionplanner.competition.service

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import org.springframework.stereotype.Service

@Service
class CompetitionService(
    val scheduleService: ScheduleService,
    val findCompetitions: FindCompetitions,
    val createCompetition: CreateCompetition
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
}
