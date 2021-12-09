package com.graphite.competitionplanner.competition.domain

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
        return repository.findCompetitionsThatBelongsTo(clubId)
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
}