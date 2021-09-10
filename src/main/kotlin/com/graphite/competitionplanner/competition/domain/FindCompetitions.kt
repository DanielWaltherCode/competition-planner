package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.competition.domain.interfaces.ICompetitionRepository
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