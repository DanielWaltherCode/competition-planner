package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.entity.Competition
import com.graphite.competitionplanner.competition.domain.interfaces.ICompetitionRepository
import org.springframework.stereotype.Component

@Component
class UpdateCompetition(
    val repository: ICompetitionRepository
) {
    fun execute(dto: CompetitionDTO): CompetitionDTO {
        Competition(dto)
        return repository.update(dto)
    }
}