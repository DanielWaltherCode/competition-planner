package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.api.competition.RegistrationSinglesSpec
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import com.graphite.competitionplanner.service.competition.CompetitionDTO
import com.graphite.competitionplanner.service.competition.CompetitionService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import kotlin.random.Random

@Service
class RegistrationService(
    val registrationRepository: RegistrationRepository,
    val competitionService: CompetitionService,
    val categoryRepository: CategoryRepository,
    val playerService: PlayerService,
    val competitionCategoryRepository: CompetitionCategoryRepository
) {

    fun registerPlayerSingles(registrationSinglesSpec: RegistrationSinglesSpec): Int {
        val registration = registrationRepository.addRegistration(LocalDate.now())
        registrationRepository.registerPlayer(registration.id, registrationSinglesSpec.playerId)
        val playingInRecord = registrationRepository.registerInCategory(
            registration.id,
            null,
            registrationSinglesSpec.competitionCategoryId
        )
        if(playingInRecord.id == null) {
            return -1
        }
        return registration.id
    }

    fun unregister(registrationId: Int): Boolean {
        val successfulDelete = registrationRepository.deleteRegistration(registrationId)
        if (!successfulDelete) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No registration with id $registrationId found")
        }
        return true
    }

    fun getPlayersFromRegistrationId(registrationId: Int?): List<PlayerDTO> {
        if (registrationId == null) {
            return emptyList()
        }
        val playerRecords = registrationRepository.getPlayersFromRegistrationId(registrationId)
        return playerRecords.map { playerService.getPlayerLegacy(it.id) }
    }

    fun getRegisteredPlayers(competitionId: Int, searchType: String): RegisteredPlayersDTO {
            if (searchType.toUpperCase() == SearchType.NAME.name) {
                val players = registrationRepository.getRegistrationsInCompetition(competitionId)
                val initialsAndPlayersMap = mutableMapOf<String, MutableSet<PlayerDTO>>()
                for (player in players) {
                    val playerDTO = playerService.getPlayerLegacy(player.id)
                    val initial: String = player.lastName[0].toUpperCase() + ""

                    // If initial has already been added, add to list. Else create player list and add initial
                    if(initialsAndPlayersMap.containsKey(initial)) {
                        initialsAndPlayersMap[initial]?.add(playerDTO)
                    }
                    else {
                        initialsAndPlayersMap[initial] = mutableSetOf()
                        initialsAndPlayersMap[initial]?.add(playerDTO)
                    }
                }
                return RegisteredPlayersDTO(SearchType.NAME, initialsAndPlayersMap)
            } else if (searchType.toUpperCase() == SearchType.CLUB.name) {
                val players = registrationRepository.getRegistrationsInCompetition(competitionId)
                val clubsAndPlayersMap = mutableMapOf<String, MutableSet<PlayerDTO>>()
                for (player in players) {
                    val playerDTO = playerService.getPlayerLegacy(player.id)
                    val clubName = playerDTO.club.name ?: ""
                    // If initial has already been added, add to list. Else create player list and add initial
                    if(clubsAndPlayersMap.containsKey(clubName)) {
                        clubsAndPlayersMap[clubName]?.add(playerDTO)
                    }
                    else {
                        clubsAndPlayersMap[clubName] = mutableSetOf()
                        clubsAndPlayersMap[clubName]?.add(playerDTO)
                    }
                }
                return RegisteredPlayersDTO(SearchType.CLUB, clubsAndPlayersMap)
            } else {
                val groupingsAndPlayers = getPlayersInCategories(competitionId)
                return RegisteredPlayersDTO(SearchType.CATEGORY, groupingsAndPlayers)
            }
    }

    private fun getPlayersInCategories(competitionId: Int): MutableMap<String, Set<PlayerDTO>> {
        val groupingAndPlayers = mutableMapOf<String, Set<PlayerDTO>>()
        val playersAndCategories = competitionCategoryRepository.getCategoriesAndPlayers(competitionId)
        val uniqueCategories = playersAndCategories.map { it.categoryName }.distinct()

        for (category in uniqueCategories) {
            val registeredPlayers = mutableSetOf<PlayerDTO>()
            for (entry in playersAndCategories) {
                if (entry.categoryName == category) {
                    registeredPlayers.add(playerService.getPlayerLegacy(entry.playerId))
                }
            }
            groupingAndPlayers[category] = registeredPlayers
        }

        return groupingAndPlayers
    }

    fun getRegistrationByPlayerId(playerId: Int): PlayerCompetitionDTO {
        // Get player and competitions
        val playerCompetitions = mutableListOf<CompetitionAndCategoriesDTO>()
        val player = playerService.getPlayerLegacy(playerId)
        val competitionPlayingCategories = competitionCategoryRepository.getByPlayerId(playerId)
        val uniqueCompetitionIds = competitionPlayingCategories.map { it.competitionId }.distinct()

        for (id in uniqueCompetitionIds) {
            // Get playing categories for each competitions and add player data to dto
            val competition = competitionService.getById(id)
            val categories = competitionPlayingCategories.filter { it.competitionId == id }
            playerCompetitions.add(
                CompetitionAndCategoriesDTO(
                    competition = competition,
                    categories = categories.map { CompetitionCategoryDTO(it.categoryId, it.categoryName) }
                )
            )
        }
        return PlayerCompetitionDTO(player = player, competitionsAndCategories = playerCompetitions)
    }

    fun getPlayersInCompetitionCategory(competitionCategoryId: Int): List<List<PlayerDTO>> {
        val registrationIds = competitionCategoryRepository.getRegistrationsInCategory(competitionCategoryId)
        return registrationIds.map{getPlayersFromRegistrationId(it)}
    }

    fun getSeed(): Int {
        return Random.nextInt(0, 16)
    }
}

data class RegistrationSinglesDTO(
    val id: Int?,
    val playerId: Int,
    val competitionCategoryId: Int
)

data class RegistrationDoublesDTO(
    val id: Int?,
    val firstPlayerId: Int,
    val secondPlayerId: Int,
    val competitionCategoryId: Int
)


data class PlayerCompetitionDTO(
    val player: PlayerDTO,
    val competitionsAndCategories: List<CompetitionAndCategoriesDTO>
)

data class CompetitionAndCategoriesDTO(
    val competition: CompetitionDTO,
    val categories: List<CompetitionCategoryDTO>
)

data class RegisteredPlayersDTO(
    val groupingType: SearchType,
    val groupingsAndPlayers: Map<String, Set<PlayerDTO>>
)


data class CompetitionRegistrationsDTO(
    val competition: CompetitionDTO,
    val registrations: List<CategoryRegistrations>
)

data class CategoryRegistrations(
    val category: CompetitionCategoryDTO,
    val registeredPlayers: List<PlayerDTO>
)

data class CompetitionCategoryDTO(
    val competitionCategoryId: Int,
    val categoryName: String
)

enum class SearchType {
    CLUB, NAME, CATEGORY
}
