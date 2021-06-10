package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CompetitionDTO
import com.graphite.competitionplanner.domain.dto.NewCompetitionDTO
import com.graphite.competitionplanner.domain.entity.Competition
import com.graphite.competitionplanner.domain.entity.Location
import com.graphite.competitionplanner.domain.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.domain.usecase.club.FindClub
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