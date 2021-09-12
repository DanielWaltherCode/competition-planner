package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class FindCompetitions(
    @Autowired val repository: ICompetitionRepository
) {

    fun thatBelongsTo(clubId: Int): List<CompetitionDTO> {
        return repository.findCompetitionsThatBelongsTo(clubId).filter { it.name != "BYE" }
    }

    fun thatStartOrEndWithin(start: LocalDate, end: LocalDate): List<CompetitionWithClubDTO> {
        return repository.findCompetitions(start, end).filter { it.name != "BYE" }
    }

    fun byId(competitionId: Int): CompetitionDTO {
        val competition = repository.findById(competitionId)
        if (competition.name == "BYE") {
            throw NotFoundException("Competition with id $competitionId was not found.")
        }
        return competition
    }
}