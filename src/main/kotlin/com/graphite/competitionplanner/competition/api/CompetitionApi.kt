package com.graphite.competitionplanner.competition.api

import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.competition.domain.UpdateCompetition
import com.graphite.competitionplanner.competition.interfaces.*
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.user.service.UserDTO
import com.graphite.competitionplanner.user.service.UserService
import org.springframework.web.bind.annotation.*
import java.time.LocalDate


@RestController
@RequestMapping("/competition")
class CompetitionApi(
    val createCompetition: CreateCompetition,
    val updateCompetition: UpdateCompetition,
    val findCompetitions: FindCompetitions,
    val getDaysOfCompetition: GetDaysOfCompetition,
    val userService: UserService
) {

    @PostMapping
    fun addCompetition(@RequestBody competitionSpec: CompetitionSpec): CompetitionDTO {
        return createCompetition.execute(competitionSpec)
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

    @GetMapping("/for-club")
    fun getAllForClub(): List<CompetitionDTO> {
        val loggedInUser: UserDTO = userService.getLoggedInUser()
        return findCompetitions.thatBelongTo(loggedInUser.clubNoAddressDTO.id)
    }

    @GetMapping("/{competitionId}/days")
    fun getDaysInCompetition(@PathVariable competitionId: Int): CompetitionDays {
        val competition = findCompetitions.byId(competitionId)
        val dates = getDaysOfCompetition.execute(competition)
        return CompetitionDays(dates)
    }

    // Get possible rounds in playoff
    @GetMapping("/rounds")
    fun getPossibleRounds(): Array<Round> {
        return Round.values()
    }
}

