package com.graphite.competitionplanner.api.open

import com.graphite.competitionplanner.api.competition.DrawDTO
import com.graphite.competitionplanner.repositories.competition.CompetitionCategory
import com.graphite.competitionplanner.service.CompetitionAndCategoriesDTO
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.service.RegisteredPlayersDTO
import com.graphite.competitionplanner.service.RegistrationService
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionDTO
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.service.competition.DrawService
import io.swagger.annotations.ApiModelProperty
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/open/competition")
class CompetitionOpenApi(
    val competitionService: CompetitionService,
    val drawService: DrawService,
    val registrationService: RegistrationService,
    val competitionCategoryService: CompetitionCategoryService
) {

    @GetMapping
    fun getAll(
        @RequestParam(required = false) weekStartDate: LocalDate?,
        @RequestParam(required = false) weekEndDate: LocalDate?
    ): List<CompetitionDTO> {
        return competitionService.getCompetitions(weekStartDate, weekEndDate)
    }

    @GetMapping("/{competitionId}")
    fun getCompetition(@PathVariable competitionId: Int): CompetitionDTO {
        return competitionService.getById(competitionId)
    }

    @GetMapping("/{competitionId}/categories")
    fun getCompetitionCategories(@PathVariable competitionId: Int): CompetitionAndCategoriesDTO {
        return competitionService.getCategoriesInCompetition(competitionId)
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
    fun getPlayersInCategory(@PathVariable competitionCategoryId: Int): List<List<PlayerDTO>> {
        return registrationService.getPlayersInCompetitionCategory(competitionCategoryId)
    }

    @GetMapping("/{competitionId}/categories/{categoryId}")
    fun getCategory(@PathVariable categoryId: Int): CompetitionCategory {
        return competitionCategoryService.getByCompetitionCategoryId(categoryId)
    }

    @GetMapping("/{competitionId}/matches/")
    fun getResults(@PathVariable competitionId: Int): CompetitionCategory {
        return competitionCate
    }
}