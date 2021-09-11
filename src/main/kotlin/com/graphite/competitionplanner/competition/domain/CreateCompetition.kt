package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import org.springframework.stereotype.Component

@Component
class CreateCompetition(
    val repository: ICompetitionRepository,
    val findClub: FindClub
) {
    fun execute(spec: CompetitionSpec): CompetitionDTO {
        findClub.byId(spec.organizingClubId)
        return repository.store(spec)
    }
}