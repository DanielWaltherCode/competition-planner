package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionUpdateSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import org.springframework.stereotype.Component

@Component
class UpdateCompetition(
    val repository: ICompetitionRepository
) {
    fun execute(id: Int, spec: CompetitionUpdateSpec): CompetitionDTO {
        return repository.update(id, spec)
    }
}