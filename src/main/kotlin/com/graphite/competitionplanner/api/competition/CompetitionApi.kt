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
    fun addCompetition(@RequestBody competitionSpec: CompetitionSpec): CompetitionDTO {
        return competitionService.addCompetition(competitionSpec)
    }

    @PutMapping("/{competitionId}")
    fun updateCompetition(@PathVariable competitionId: Int,
                          @RequestBody competitionSpec: CompetitionSpec): CompetitionDTO {
        return competitionService.updateCompetition(competitionId, competitionSpec)
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

data class CompetitionSpec(
    val location: String,
    val welcomeText: String,
    val organizingClubId: Int,
    val startDate: LocalDate,
    val endDate: LocalDate
)