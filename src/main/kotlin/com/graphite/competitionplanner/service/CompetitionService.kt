package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.CompetitionDTO
import com.graphite.competitionplanner.repositories.CompetitionRepository
import com.graphite.competitionplanner.tables.pojos.Competition
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CompetitionService(val competitionRepository: CompetitionRepository) {

    fun getCompetitions(weekStartDate: LocalDate?, weekEndDate: LocalDate?): List<Competition> {
        return competitionRepository.getCompetitions(weekStartDate, weekEndDate)
    }

    fun addCompetition(competitionDTO: CompetitionDTO): Competition {
        return competitionRepository.addCompetition(competitionDTO)
    }
}