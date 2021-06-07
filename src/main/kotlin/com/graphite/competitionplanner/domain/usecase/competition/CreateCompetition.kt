package com.graphite.competitionplanner.domain.usecase.competition

import com.graphite.competitionplanner.domain.dto.CompetitionEntityDTO
import com.graphite.competitionplanner.domain.dto.NewCompetitionDTO
import com.graphite.competitionplanner.domain.entity.Club
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
    fun execute(dto: NewCompetitionDTO): CompetitionEntityDTO {
        val clubDto = findClub.byId(dto.organizingClubId)
        Competition(0, Location(dto.location), dto.name, dto.welcomeText, Club(clubDto), dto.startDate, dto.endDate)
        val competition = repository.store(dto)
        return CompetitionEntityDTO(
            competition.id,
            competition.location,
            competition.name,
            competition.welcomeText,
            clubDto,
            competition.startDate,
            competition.endDate
        )
    }

}