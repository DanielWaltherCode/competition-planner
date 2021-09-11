package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import org.springframework.stereotype.Component

@Component
class UpdateCompetition(
    val repository: ICompetitionRepository
) {
    fun execute(id: Int, spec: CompetitionSpec): CompetitionDTO {
        return repository.update(id, spec)
    }
}