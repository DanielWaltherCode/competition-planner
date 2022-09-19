package com.graphite.competitionplanner.open.controllers

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.LocalDate

@Controller
@RequestMapping("/open/competitions")
class CompetitionController(
        val findCompetitions: FindCompetitions
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

}

enum class SearchPeriod {
    PREVIOUS, CURRENT, COMING
}