package com.graphite.competitionplanner.api.competition

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonFormat
import com.graphite.competitionplanner.service.competition.CompetitionDTO
import com.graphite.competitionplanner.service.competition.CompetitionService
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