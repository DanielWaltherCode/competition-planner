package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.*
import io.swagger.annotations.ApiModelProperty
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import javax.validation.Valid


@RestController
@RequestMapping("/competition")
class CompetitionApi(
    val competitionService: CompetitionService
    ) {

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

    @GetMapping
    fun getAll(
        @RequestParam(required = false) weekStartDate: LocalDate?,
        @RequestParam(required = false) weekEndDate: LocalDate?
    ): List<CompetitionDTO> {
        return competitionService.getCompetitions(weekStartDate, weekEndDate)
    }
}

