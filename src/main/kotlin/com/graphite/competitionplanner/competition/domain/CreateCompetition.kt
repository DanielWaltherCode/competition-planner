package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.domain.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.domain.interfaces.NewCompetitionDTO
import com.graphite.competitionplanner.domain.entity.Competition
import com.graphite.competitionplanner.domain.entity.Location
import com.graphite.competitionplanner.competition.domain.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.club.domain.FindClub
import org.springframework.stereotype.Component

@Component
class CreateCompetition(
    val repository: ICompetitionRepository,
    val findClub: FindClub
) {
    fun execute(dto: NewCompetitionDTO): CompetitionDTO {
        Competition(0, Location(dto.location), dto.name, dto.welcomeText, dto.startDate, dto.endDate)
        findClub.byId(dto.organizingClubId)
        return repository.store(dto)
    }
}