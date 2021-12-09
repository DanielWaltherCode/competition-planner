package com.graphite.competitionplanner.competition.api

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.UpdateCompetition
import com.graphite.competitionplanner.competition.interfaces.*
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.entity.Round
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
@RequestMapping("/competition")
class CompetitionApi(
    val competitionService: CompetitionService,
    val updateCompetition: UpdateCompetition,
    val findCompetitions: FindCompetitions
) {

    @PostMapping
    fun addCompetition(@RequestBody competitionSpec: CompetitionSpec): CompetitionDTO {
        return competitionService.addCompetition(competitionSpec)
    }

    @PutMapping("/{competitionId}")
    fun updateCompetition(
        @PathVariable competitionId: Int,
        @RequestBody competitionSpec: CompetitionUpdateSpec
    ): CompetitionDTO {
        return updateCompetition.execute(competitionId, competitionSpec)
    }

    @GetMapping("/{competitionId}")
    fun getCompetition(@PathVariable competitionId: Int): CompetitionDTO {
        return findCompetitions.byId(competitionId)
    }

    @GetMapping
    fun getAll(
        @RequestParam(required = false) weekStartDate: LocalDate?,
        @RequestParam(required = false) weekEndDate: LocalDate?
    ): List<CompetitionWithClubDTO> {
        return findCompetitions.thatStartOrEndWithin(weekStartDate, weekEndDate)
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

