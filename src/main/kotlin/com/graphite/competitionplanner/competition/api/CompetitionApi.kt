package com.graphite.competitionplanner.competition.api

import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionDays
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.domain.entity.Round
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


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
    fun updateCompetition(
        @PathVariable competitionId: Int,
        @RequestBody competitionSpec: CompetitionSpec
    ): CompetitionDTO {
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
    ): List<CompetitionWithClubDTO> {
        return competitionService.getByDate(weekStartDate, weekEndDate)
    }

    @GetMapping("/{competitionId}/days")
    fun getDaysInCompetition(@PathVariable competitionId: Int): CompetitionDays {
        val dates = competitionService.getDaysOfCompetition(competitionId)
        return CompetitionDays(dates)
    }

    // Get possible rounds in playoff
    @GetMapping("/rounds")
    fun getPossibleRounds(): Array<Round> {
        return Round.values()
    }
}

