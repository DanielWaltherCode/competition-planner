package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.common.toList
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class GetDaysOfCompetition {

    fun execute(competition: CompetitionDTO): List<LocalDate> {
        return (competition.startDate .. competition.endDate).toList().sorted()
    }
}