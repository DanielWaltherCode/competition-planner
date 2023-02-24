package com.graphite.competitionplanner.registration.api

import com.graphite.competitionplanner.competitioncategory.domain.GetCompetitionCategories
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithWithdrawnStatus
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesSpec
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.util.Util
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("external-registration")
class ExternalRegistrationApi(
        val registrationService: RegistrationService,
        val getCompetitionCategories: GetCompetitionCategories,
        val registrationRepository: RegistrationRepository,
        val util: Util
) {
    @GetMapping("/{competitionCategoryId}/club/{clubId}")
    fun getPlayersInCategoryForClub(@PathVariable competitionCategoryId: Int,
                                    @PathVariable clubId: Int): List<List<PlayerWithWithdrawnStatus>> {
        val playersInCategory = registrationService.getPlayersInCompetitionCategory(competitionCategoryId)
        val filteredList = mutableListOf<List<PlayerWithClubDTO>>()
        for (playerList in playersInCategory) {
            if (playerList.size == 1) {
                if (playerList.get(0).club.id == clubId) {
                    filteredList.add(playerList)
                }
            } else if (playerList.size == 2) {
                if (playerList.get(0).club.id == clubId || playerList.get(1).club.id == clubId) {
                    filteredList.add(playerList)
                }
            }
        }
        val playersWithWithdrawnStatus = mutableListOf<List<PlayerWithWithdrawnStatus>>()
        for (playerList in filteredList) {
            playersWithWithdrawnStatus.add(util.getPlayersAndWithdrawnStatus(playerList, competitionCategoryId))
        }
        return playersWithWithdrawnStatus
    }

    @GetMapping("competition/{selectedCompetition}/category")
    fun getCompetitionCategories(@PathVariable selectedCompetition: Int): List<CompetitionCategoryDTO> {
        val competitionCategories = getCompetitionCategories.execute(selectedCompetition)
        return competitionCategories.sortedBy { c -> c.category.name }
    }

    @PostMapping("register/singles")
    fun registerPlayerSingles(@RequestBody registrationSinglesSpec: RegistrationSinglesSpec): RegistrationSinglesDTO {
        return registrationService.registerPlayerSingles(registrationSinglesSpec)
    }

    @PostMapping("register/doubles")
    fun registerPlayerDoubles(@RequestBody spec: RegistrationDoublesSpec): RegistrationDoublesDTO {
        return registrationService.registerPlayersDoubles(spec)
    }
}