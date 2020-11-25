package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.repositories.CompetitionRepository
import com.graphite.competitionplanner.service.CompetitionAndCategoriesDTO
import com.graphite.competitionplanner.service.CompetitionDTO
import com.graphite.competitionplanner.service.CompetitionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import javax.validation.Valid


@RestController
@RequestMapping("/competition")
class CompetitionApi(val competitionRepository: CompetitionRepository, val competitionService: CompetitionService) {

    @PostMapping
    fun addCompetition(@Valid @RequestBody competitionDTO: CompetitionDTO): CompetitionDTO {
        return competitionService.addCompetition(competitionDTO)
    }

    @PutMapping
    fun updateCompetition(@Valid @RequestBody competitionDTO: CompetitionDTO): CompetitionDTO {
        if (competitionDTO.id == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Competition id should not be null")
        }
        return competitionService.updateCompetition(competitionDTO)
    }

    @GetMapping("/{competitionId}")
    fun getCompetition(@PathVariable competitionId: Int): CompetitionDTO {
        return competitionService.getById(competitionId)
    }

    @GetMapping("/{competitionId}/categories")
    fun getCompetitionCategories(@PathVariable competitionId: Int): CompetitionAndCategoriesDTO {
        return competitionService.getCategoriesInCompetition(competitionId)
    }

    @GetMapping
    fun getAll(@RequestParam(required = false) weekStartDate: LocalDate?,
               @RequestParam(required = false) weekEndDate: LocalDate?): List<CompetitionDTO> {
        return competitionService.getCompetitions(weekStartDate, weekEndDate)
    }

}


