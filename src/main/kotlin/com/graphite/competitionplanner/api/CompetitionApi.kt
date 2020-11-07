package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.CompetitionRepository
import com.graphite.competitionplanner.tables.pojos.Competition
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.NotNull


@RestController
@RequestMapping("/competition")
class CompetitionApi(val competitionRepository: CompetitionRepository) {

        @PostMapping
        fun addCompetition(@Valid @RequestBody competitionDTO: CompetitionDTO): Competition {
                return competitionRepository.addCompetition(competitionDTO)
        }
}

data class CompetitionDTO(
        val id: Int?,
        @NotNull
        val location: String,
        @NotNull
        val welcomeText: String,
        @NotNull
        val organizingClub: Int,
        @NotNull
        val startDate: LocalDate,
        @NotNull
        val endDate: LocalDate
)