package com.graphite.competitionplanner.competition.api

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.competition.service.CompetitionDTO
import com.graphite.competitionplanner.competition.service.CompetitionDays
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
    fun updateCompetition(@PathVariable competitionId: Int,
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
    ): List<CompetitionDTO> {
        return competitionService.getCompetitions(weekStartDate, weekEndDate)
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

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
data class CompetitionSpec(
    val location: String,
    val name: String,
    val welcomeText: String?,
    val organizingClubId: Int,
    @JsonFormat(pattern="yyyy-MM-dd")
    val startDate: LocalDate?,
    @JsonFormat(pattern="yyyy-MM-dd")
    val endDate: LocalDate?
)