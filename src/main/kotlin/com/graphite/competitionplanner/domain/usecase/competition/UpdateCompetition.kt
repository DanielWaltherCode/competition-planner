package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.entity.Competition
import com.graphite.competitionplanner.domain.interfaces.ICompetitionRepository
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