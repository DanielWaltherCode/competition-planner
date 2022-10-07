package com.graphite.competitionplanner.open.controllers

import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@RequestMapping("open/players")
@Controller
class PlayerController(
        val findPlayer: FindPlayer
) {

    @GetMapping
    fun getPlayerOverview(): String {
       return "player/player-search"
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
}