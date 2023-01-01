package com.graphite.competitionplanner.open.controllers

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.service.RegistrationService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("/players")
@Controller
class PlayerController(
        val findPlayer: FindPlayer,
        val findCompetitions: FindCompetitions,
        val registrationService: RegistrationService,
        val drawService: DrawService
) {

    @GetMapping
    fun getPlayerOverview(): String {
       return "player/player-search"
    }

    @GetMapping("/{playerId}")
    fun getPlayerDetail(model: Model, @PathVariable playerId: Int): String {
        val player = findPlayer.byId(playerId)
        val competitions = registrationService.getRegistrationByPlayerId(playerId)
        model.addAttribute("player", player)
        model.addAttribute("competitions", competitions)
        return "player/player-detail"
    }

    @GetMapping("/search")
    fun findPlayers(model: Model,
                    @RequestParam search: String): String {
        if (search.isNotEmpty()) {
            val players: List<PlayerWithClubDTO> = findPlayer.byPartName(search)
            model.addAttribute("players", players)
        }
        else {
            model.addAttribute("players", emptyList<PlayerWithClubDTO>())
        }
        return "player/player-search-results"
    }

    @GetMapping("/{competitionId}/{playerId}")
    fun getPlayerInCompetition(model: Model, @PathVariable competitionId: Int, @PathVariable playerId: Int): String {
        val player = findPlayer.byId(playerId)
        val competition = findCompetitions.byId(competitionId)
        val registrationsInCompetition = registrationService.getRegistrationsForPlayerInCompetition(competitionId, playerId)
        val playerCategories = registrationsInCompetition.registrations.map { it.competitionCategory.id }
        val areCategoriesDrawn = mutableMapOf<Int, Boolean>()
        for (category in playerCategories) {
            areCategoriesDrawn[category] = drawService.isDrawMade(category)
        }
        model.addAttribute("player", player)
        model.addAttribute("competition", competition)
        model.addAttribute("registrations", registrationsInCompetition)
        model.addAttribute("areCategoriesDrawn", areCategoriesDrawn)

        return "competition-detail/player-detail"
    }
}