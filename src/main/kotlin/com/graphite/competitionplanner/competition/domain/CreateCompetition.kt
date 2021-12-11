package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import org.springframework.stereotype.Component

@Component
class CreateCompetition(
    val repository: ICompetitionRepository,
    val findClub: FindClub,
    val scheduleService: ScheduleService
) {
    fun execute(spec: CompetitionSpec): CompetitionDTO {
        val competition = repository.store(spec)

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