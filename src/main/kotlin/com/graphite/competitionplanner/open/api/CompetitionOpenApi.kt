package com.graphite.competitionplanner.open.api

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategory
import com.graphite.competitionplanner.draw.api.DrawDTO
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.PlayerRegistrationDTO
import com.graphite.competitionplanner.registration.service.RegisteredPlayersDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/competition")
class CompetitionOpenApi(
    val drawService: DrawService,
    val registrationService: RegistrationService,
    val matchService: MatchService,
    val findPlayer: FindPlayer,
    val findCompetitions: FindCompetitions,
    val findCompetitionCategory: FindCompetitionCategory,
    val getCompetitionCategories: GetCompetitionCategories
) {

    @GetMapping
    fun getAllCompetitions(
        @RequestParam(required = false) weekStartDate: LocalDate?,
        @RequestParam(required = false) weekEndDate: LocalDate?
    ): List<CompetitionWithClubDTO> {
        return findCompetitions.thatStartOrEndWithin(weekStartDate, weekEndDate)
    }

    @GetMapping("/{competitionId}")
    fun getCompetition(@PathVariable competitionId: Int): CompetitionDTO {
        return findCompetitions.byId(competitionId)
    }

    @GetMapping("/{competitionId}/categories")
    fun getCompetitionCategories(@PathVariable competitionId: Int): List<CompetitionCategoryDTO> {
        val competitionCategories = getCompetitionCategories.execute(competitionId)
        return competitionCategories.sortedBy { c -> c.category.name }
    }

    @GetMapping("/{competitionId}/draw/{competitionCategoryId}")
    fun getDraw(@PathVariable competitionCategoryId: Int): DrawDTO {
        return drawService.getDraw(competitionCategoryId)
    }

    @GetMapping("/{competitionId}/draw/{competitionCategoryId}/is-draw-made")
    fun isDrawMade(@PathVariable competitionCategoryId: Int): Boolean {
        return drawService.isDrawMade(competitionCategoryId)
    }
    @GetMapping("/{competitionId}/registration/{competitionCategoryId}")
    fun getPlayersInCategory(@PathVariable competitionCategoryId: Int): List<List<PlayerWithClubDTO>> {
        return registrationService.getPlayersInCompetitionCategory(competitionCategoryId)
    }

    @GetMapping("/{competitionId}/registration")
    @Operation(summary = "Allowed values: club, category, name")
    fun getPlayersInCompetition(
        @PathVariable competitionId: Int,
        @RequestParam searchType: String
    ): RegisteredPlayersDTO {
        return registrationService.getRegisteredPlayers(competitionId, searchType)
    }

    // Retrieves player information and all the categories and player is registered in
    // in a given competition (including who they are playing with in doubles)
    @GetMapping("/{competitionId}/player/{playerId}")
    fun getRegistrationsForPlayer(@PathVariable competitionId: Int,
                                  @PathVariable playerId: Int): PlayerRegistrationDTO {
        return registrationService.getRegistrationsForPlayerInCompetition(competitionId, playerId)
    }

    @GetMapping("name-search/{competitionId}")
    fun searchPlayerInCompetition(@PathVariable competitionId: Int, @RequestParam partOfName: String): List<PlayerWithClubDTO> {
        val competition = findCompetitions.byId(competitionId)
        return findPlayer.byPartNameInCompetition(partOfName, competition)
    }

    @GetMapping("/{competitionId}/categories/{categoryId}")
    fun getCategory(@PathVariable categoryId: Int): CompetitionCategory {
        val dto = findCompetitionCategory.byId(categoryId)
        return CompetitionCategory(dto.id, dto.category.name, dto.status.name)
    }

    @GetMapping("/{competitionId}/matches")
    fun getMatches(@PathVariable competitionId: Int): List<MatchAndResultDTO> {
        return matchService.getMatchesInCompetition(competitionId)
    }
}