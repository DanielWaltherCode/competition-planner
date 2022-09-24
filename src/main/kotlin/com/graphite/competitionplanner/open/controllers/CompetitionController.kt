package com.graphite.competitionplanner.open.controllers

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.draw.domain.GetDraw
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.registration.service.RegistrationService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("open/competitions")
class CompetitionController(
        val findCompetitions: FindCompetitions,
        val getCompetitionCategories: GetCompetitionCategories,
        val drawService: DrawService,
        val getDraw: GetDraw,
        val registrationService: RegistrationService
) {


    @GetMapping("/search")
    fun searchCompetitions(model: Model,
                           @RequestParam search: String): String {
        var searchResults = listOf<CompetitionWithClubDTO>()
        if (search.isNotEmpty()) {
            searchResults = findCompetitions.byName(search)
        }
        model.addAttribute("searchResults", searchResults.map { convertCompetitionDTO(it) })
        return "fragments/competition-search-results"
    }

    @GetMapping
    fun competitionPage(model: Model): String {
        return "competition-search"
    }

    @GetMapping("/{competitionId}/overview")
    fun getOverview(model: Model, @PathVariable competitionId: Int): String {
        val competition = findCompetitions.byId(competitionId)
        model.addAttribute("competition", competition)
        return "competition-detail/overview"
    }

    @GetMapping("/{competitionId}/categories/{categoryId}")
    fun getCategoriesDetail(model: Model, @PathVariable competitionId: Int, @PathVariable categoryId: Int): String {
        val competition = findCompetitions.byId(competitionId)
        val competitionCategories = getCompetitionCategories.execute(competitionId)
        if (competitionCategories.isEmpty()) {
            model.addAttribute("competition", competition)
            model.addAttribute("categories", emptyList<Int>())
            return "competition-detail/categories"
        }
        val sortedCategories = competitionCategories.sortedBy { it.category.name }
        model.addAttribute("competition", competition)
        model.addAttribute("categories", sortedCategories)

        val chosenCategory = if (categoryId == 0) sortedCategories[0] else sortedCategories.first { it.id == categoryId }
        model.addAttribute("selectedCategory", chosenCategory)
        val isDrawMade = drawService.isDrawMade(chosenCategory.id)
        model.addAttribute("isCategoryDrawn", isDrawMade)
        if (isDrawMade) {
            model.addAttribute("draw", getDraw.execute(chosenCategory.id))
        }
        else {
            model.addAttribute("draw", null)
        }
        model.addAttribute("registeredPlayers", registrationService.getPlayersInCompetitionCategory(chosenCategory.id))
        return "competition-detail/categories"
    }


}

enum class SearchPeriod {
    PREVIOUS, CURRENT, COMING
}