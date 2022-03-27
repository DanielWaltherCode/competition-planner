package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.schedule.api.AvailableTablesWholeCompetitionSpec
import com.graphite.competitionplanner.schedule.service.AvailableTablesService
import com.graphite.competitionplanner.schedule.service.DailyStartEndService
import com.graphite.competitionplanner.schedule.service.ScheduleMetadataService
import org.springframework.stereotype.Component

@Component
class CreateCompetition(
    val repository: ICompetitionRepository,
    val findClub: FindClub,
    val scheduleMetadataService: ScheduleMetadataService,
    val dailyStartEndService: DailyStartEndService,
    val availableTablesService: AvailableTablesService
) {
    fun execute(spec: CompetitionSpec): CompetitionDTO {
        findClub.byId(spec.organizingClubId)
        val competition = repository.store(spec)

        // Add default competition schedule metadata
        scheduleMetadataService.addDefaultScheduleMetadata(competition.id)
        dailyStartEndService.addDailyStartAndEndForWholeCompetition(competition.id)
        availableTablesService.registerTablesAvailableForWholeCompetition(
            competition.id,
            AvailableTablesWholeCompetitionSpec(0)
        )

        return competition
    }
}