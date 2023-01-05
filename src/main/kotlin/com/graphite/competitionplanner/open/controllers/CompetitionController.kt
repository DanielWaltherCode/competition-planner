package com.graphite.competitionplanner.open.controllers

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.domain.GetDaysOfCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionWithClubDTO
import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.draw.domain.GetDraw
import com.graphite.competitionplanner.draw.interfaces.PlayoffRoundDTO
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.registration.domain.SearchRegistrations
import com.graphite.competitionplanner.registration.service.RegistrationService
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.bind.Bindable
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters


@Controller
@RequestMapping("/competitions")
class CompetitionController(
        val findCompetitions: FindCompetitions,
        val getCompetitionCategories: GetCompetitionCategories,
        val drawService: DrawService,
        val getDraw: GetDraw,
        val registrationService: RegistrationService,
        val matchService: MatchService,
        val searchRegistrations: SearchRegistrations,
        val getDaysOfCompetition: GetDaysOfCompetition
) {

    private val LOGGER = LoggerFactory.getLogger(CompetitionController::class.java)

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
            val draw = getDraw.execute(chosenCategory.id)
            model.addAttribute("draw", draw)
            model.addAttribute("shouldShowPlayoff", shouldShowPlayoff(draw.playOff))
        }
        else {
            model.addAttribute("draw", null)
        }
        if (chosenCategory.category.type == CategoryType.SINGLES) {
            model.addAttribute("registeredPlayers",
                    searchRegistrations.getResultGroupedByLastNameForCategory(chosenCategory.id))
        }
        else {
            model.addAttribute("registeredPlayers",
                    registrationService.getPlayersInCompetitionCategory(chosenCategory.id))
        }

        return "competition-detail/categories"
    }

    @GetMapping("/{competitionId}/results")
    fun getResults(model: Model, @PathVariable competitionId: Int): String {
        val competition = findCompetitions.byId(competitionId)
        val categories = getCompetitionCategories.execute(competitionId)
        val dates = getDaysOfCompetition.execute(competition)
        model.addAttribute("competition", competition)
        model.addAttribute("categories", categories)
        model.addAttribute("dates", dates)
        return "competition-detail/results"
    }

    @GetMapping("/{competitionId}/results/table")
    fun getResultsTable(model: Model, @PathVariable competitionId: Int,
                        @RequestParam(defaultValue = "") date: String,
                        @RequestParam(defaultValue = "") categoryId: String): String {
        val competition = findCompetitions.byId(competitionId)
        val matches = matchService.getMatchesInCompetition(competitionId)
        var filteredMatches = mutableListOf<MatchAndResultDTO>()

        if(date == "") {
            filteredMatches.addAll(matches)
        }
        else {
            try {
                val parsedDate: LocalDate = LocalDate.parse(date)
                for (match in matches) {
                    if (match.startTime != null && match.startTime.toLocalDate().equals(parsedDate)) {
                        filteredMatches.add(match)
                    }
                }
            }
            catch (ex: Exception) {
                LOGGER.error("Couldn't parse date {}", date)
                filteredMatches.addAll(matches)
            }
        }

        if (categoryId != "") {
            val parsedCategoryId = categoryId.toInt()
            filteredMatches = filteredMatches.filter { it.competitionCategory.id == parsedCategoryId }.toMutableList()
        }

        model.addAttribute("competition", competition)
        model.addAttribute("matches", filteredMatches)
        return "fragments/results-table"
    }

    @GetMapping("/{competitionId}/players")
    @Operation(summary = "Allowed values: club, category, name")
    fun getPlayersInCompetition(
            model: Model,
            @PathVariable competitionId: Int,
            @RequestParam(required = false, defaultValue = "club") searchType: String
    ): String {
        val competition = findCompetitions.byId(competitionId)
        model.addAttribute("competition", competition)
        return "competition-detail/players"
    }

    @GetMapping("/{competitionId}/players/htmx")
    @Operation(summary = "Allowed values: club, category, name")
    fun getPlayersInCompetitionHtmx(
            model: Model,
            @PathVariable competitionId: Int,
            @RequestParam searchType: String
    ): String {
        val registeredPlayersDTO = registrationService.getRegisteredPlayers(competitionId, searchType)
        model.addAttribute("sortingChoice", registeredPlayersDTO.groupingType)
        model.addAttribute("groupingsAndPlayers", registeredPlayersDTO.groupingsAndPlayers)
        model.addAttribute("competitionId", competitionId)
        return "fragments/registered-players"
    }

    @GetMapping("/{searchPeriod}")
    fun getCompetitionsBySearchPeriod(model: Model, @PathVariable searchPeriod: SearchPeriod): String {
        val startDate: LocalDate
        val endDate: LocalDate
        val today: LocalDate = LocalDate.now()
        if (searchPeriod == SearchPeriod.PREVIOUS) {
            startDate = today.minusDays(90)
            endDate = today.minusDays(today.dayOfWeek.value.toLong()) // End of last week
        }
        else if (searchPeriod == SearchPeriod.CURRENT) {
            startDate = today.minusDays(today.dayOfWeek.value.toLong() -1)
            endDate = today.plusDays(7).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        }
        else { // Coming competitions
            startDate = today.plusDays(7).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            endDate = today.plusDays(60)
        }
        val competitions = findCompetitions.thatStartOrEndWithin(startDate, endDate)
        model.addAttribute("competitions", competitions.map { convertCompetitionDTO(it) })
        return "fragments/competition-list"
    }

    private fun shouldShowPlayoff(playoff: List<PlayoffRoundDTO>?): Boolean {
        if (playoff.isNullOrEmpty()) {
            return false
        }
        return !playoff[0].matches.flatMap { m -> listOf(m.firstPlayer[0], m.secondPlayer[0])}.any { r -> r.id == -1 }
    }
}

enum class SearchPeriod {
    PREVIOUS, CURRENT, COMING
}