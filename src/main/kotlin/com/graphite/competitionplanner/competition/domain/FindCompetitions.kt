package com.graphite.competitionplanner.competition.domain

import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class FindCompetitions(
   val repository: ICompetitionRepository
) {

    fun thatBelongTo(clubId: Int): List<CompetitionDTO> {
        return repository.findCompetitionsThatBelongTo(clubId)
    }

    fun thatStartOrEndWithin(weekStartDate: LocalDate?, weekEndDate: LocalDate?): List<CompetitionWithClubDTO> {
        var start = LocalDate.now()
        if (weekStartDate != null) {
            start = weekStartDate
        }
        var end = LocalDate.now().plusMonths(1)
        if (weekEndDate != null) {
            end = weekEndDate
        }
        return repository.findCompetitions(start, end)
    }

    fun byId(competitionId: Int): CompetitionDTO {
        return repository.findById(competitionId)
    }

    fun byName(searchString: String): List<CompetitionWithClubDTO> {
        return repository.findByName(searchString)
    }
}