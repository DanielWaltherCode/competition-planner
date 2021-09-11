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
        return repository.findCompetitionsFor(clubId)
    }

    fun thatStartOrEndWithin(start: LocalDate, end: LocalDate): List<CompetitionWithClubDTO> {
        return repository.findCompetitions(start, end)
    }

    fun byId(competitionId: Int): CompetitionDTO {
        return repository.findById(competitionId)
    }
}