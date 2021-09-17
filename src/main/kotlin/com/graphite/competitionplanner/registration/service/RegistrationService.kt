package com.graphite.competitionplanner.registration.service

import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.service.PlayerService
import com.graphite.competitionplanner.registration.domain.RegisterPlayerToCompetition
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.random.Random

@Service
class RegistrationService(
    val registrationRepository: RegistrationRepository,
    val competitionService: CompetitionService,
    val playerService: PlayerService,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val registerPlayerToCompetition: RegisterPlayerToCompetition
) {

    fun registerPlayerSingles(spec: RegistrationSinglesSpec): Int {
        val registration = registerPlayerToCompetition.execute(spec)
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
        return playerRecords.map { PlayerDTO(it.id, it.firstName, it.lastName, it.clubId, it.dateOfBirth) }
    }

    fun getPlayersWithClubFromRegistrationId(registrationId: Int?): List<PlayerWithClubDTO> {
        if (registrationId == null) {
            return emptyList()
        }
        val playerRecords = registrationRepository.getPlayersFromRegistrationId(registrationId)
        return playerRecords.map { playerService.getPlayer(it.id) }
    }

    fun getRegisteredPlayers(competitionId: Int, searchType: String): RegisteredPlayersDTO {
            if (searchType.toUpperCase() == SearchType.NAME.name) {
                val players = registrationRepository.getRegistrationsInCompetition(competitionId)
                val initialsAndPlayersMap = mutableMapOf<String, MutableSet<PlayerWithClubDTO>>()
                for (player in players) {
                    val playerDtoWithClub = playerService.getPlayer(player.id)
                    val initial: String = player.lastName[0].toUpperCase() + ""

                    // If initial has already been added, add to list. Else create player list and add initial
                    if (initialsAndPlayersMap.containsKey(initial)) {
                        initialsAndPlayersMap[initial]?.add(playerDtoWithClub)
                    } else {
                        initialsAndPlayersMap[initial] = mutableSetOf()
                        initialsAndPlayersMap[initial]?.add(playerDtoWithClub)
                    }
                }
                return RegisteredPlayersDTO(SearchType.NAME, initialsAndPlayersMap)
            } else if (searchType.toUpperCase() == SearchType.CLUB.name) {
                val players = registrationRepository.getRegistrationsInCompetition(competitionId)
                val clubsAndPlayersMap = mutableMapOf<String, MutableSet<PlayerWithClubDTO>>()
                for (player in players) {
                    val playerDtoWithClub = playerService.getPlayer(player.id)
                    val clubName = playerDtoWithClub.club.name
                    // If initial has already been added, add to list. Else create player list and add initial
                    if (clubsAndPlayersMap.containsKey(clubName)) {
                        clubsAndPlayersMap[clubName]?.add(playerDtoWithClub)
                    } else {
                        clubsAndPlayersMap[clubName] = mutableSetOf()
                        clubsAndPlayersMap[clubName]?.add(playerDtoWithClub)
                    }
                }
                return RegisteredPlayersDTO(SearchType.CLUB, clubsAndPlayersMap)
            } else {
                val groupingsAndPlayers = getPlayersInCategories(competitionId)
                return RegisteredPlayersDTO(SearchType.CATEGORY, groupingsAndPlayers)
            }
    }

    private fun getPlayersInCategories(competitionId: Int): MutableMap<String, Set<PlayerWithClubDTO>> {
        val groupingAndPlayers = mutableMapOf<String, Set<PlayerWithClubDTO>>()
        val playersAndCategories = competitionCategoryRepository.getCategoriesAndPlayers(competitionId)
        val uniqueCategories = playersAndCategories.map { it.categoryName }.distinct()

        for (category in uniqueCategories) {
            val registeredPlayers = mutableSetOf<PlayerWithClubDTO>()
            for (entry in playersAndCategories) {
                if (entry.categoryName == category) {
                    val playerDtoWithClub = playerService.getPlayer(entry.playerId)
                    registeredPlayers.add(playerDtoWithClub)
                }
            }
            groupingAndPlayers[category] = registeredPlayers
        }

        return groupingAndPlayers
    }

    fun getRegistrationByPlayerId(playerId: Int): PlayerCompetitionDTO {
        // Get player and competitions
        val playerCompetitions = mutableListOf<CompetitionAndCategoriesDTO>()
        val player = playerService.getPlayer(playerId)
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
        return PlayerCompetitionDTO(player = player,
            competitionsAndCategories = playerCompetitions)
    }

    fun getPlayersInCompetitionCategory(competitionCategoryId: Int): List<List<PlayerWithClubDTO>> {
        val registrationIds = competitionCategoryRepository.getRegistrationsInCategory(competitionCategoryId)
        return registrationIds.map{getPlayersWithClubFromRegistrationId(it)}
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
    val player: PlayerWithClubDTO,
    val competitionsAndCategories: List<CompetitionAndCategoriesDTO>
)

data class CompetitionAndCategoriesDTO(
    val competition: CompetitionDTO,
    val categories: List<CompetitionCategoryDTO>
)

data class RegisteredPlayersDTO(
    val groupingType: SearchType,
    val groupingsAndPlayers: Map<String, Set<PlayerWithClubDTO>>
)


data class CompetitionRegistrationsDTO(
    val competition: CompetitionDTO,
    val registrations: List<CategoryRegistrations>
)

data class CategoryRegistrations(
    val category: CompetitionCategoryDTO,
    val registeredPlayers: List<PlayerWithClubDTO>
)

data class CompetitionCategoryDTO(
    val id: Int,
    val name: String
)

enum class SearchType {
    CLUB, NAME, CATEGORY
}
