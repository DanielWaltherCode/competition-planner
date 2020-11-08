package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.CompetitionRepository
import com.graphite.competitionplanner.tables.pojos.Competition
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid
import javax.validation.constraints.NotNull
import kotlin.streams.toList


@RestController
@RequestMapping("/competition")
class CompetitionApi(val competitionRepository: CompetitionRepository) {

    @PostMapping
    fun addCompetition(@Valid @RequestBody competitionDTO: CompetitionDTO): CompetitionDTO {
        val addedCompetition = competitionRepository.addCompetition(competitionDTO)
        return CompetitionDTO(addedCompetition.id, addedCompetition.location, addedCompetition.welcomeText,
                addedCompetition.organizingClub, addedCompetition.startDate, addedCompetition.endDate)
    }

    @PutMapping
    fun updateCompetition(@Valid @RequestBody competitionDTO: CompetitionDTO): CompetitionDTO {
        val addedCompetition = competitionRepository.updateCompetition(competitionDTO)
        return CompetitionDTO(addedCompetition.id, addedCompetition.location, addedCompetition.welcomeText,
                addedCompetition.organizingClub, addedCompetition.startDate, addedCompetition.endDate)
    }

    @GetMapping
    fun getAll(@RequestParam(required = false) weekStartDate: LocalDate?,
               @RequestParam(required = false) weekEndDate: LocalDate?): List<CompetitionDTO> {
        val competitions = competitionRepository.getCompetitions(weekStartDate, weekEndDate)
        return competitions.stream().map { c ->
            CompetitionDTO(
                    c.id,
                    c.location,
                    c.welcomeText,
                    c.organizingClub,
                    c.startDate,
                    c.endDate
            )
        }.toList()
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