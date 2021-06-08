package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.interfaces.ICompetitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class FindCompetitions(
    @Autowired val repository: ICompetitionRepository
) {

    fun thatBelongsTo(clubId: Int): List<CompetitionDTO> {
        return repository.findCompetitionsFor(clubId)
    }
}