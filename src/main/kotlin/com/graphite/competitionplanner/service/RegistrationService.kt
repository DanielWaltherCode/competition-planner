package com.graphite.competitionplanner.service

import com.graphite.competitionplanner.repositories.CompetitionAndPlayingCategoryRepository
import com.graphite.competitionplanner.repositories.PlayingCategoryRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import kotlin.random.Random
import kotlin.streams.toList

@Service
class RegistrationService(val registrationRepository: RegistrationRepository,
                          val competitionService: CompetitionService,
                          val playingCategoryRepository: PlayingCategoryRepository,
                          val playerService: PlayerService,
                          val competitionAndPlayingCategoryRepository: CompetitionAndPlayingCategoryRepository) {

    fun registerPlayerSingles(registrationSinglesDTO: RegistrationSinglesDTO): Boolean {
        val registration = registrationRepository.addRegistration(LocalDate.now())
        registrationRepository.registerPlayer(registration.id, registrationSinglesDTO.playerId)
        val playingInRecord = registrationRepository.registerPlayingIn(registration.id, null, registrationSinglesDTO.competitionPlayingCategoryId)
        return playingInRecord.id != null
    }

    fun unregister(registrationId: Int): Boolean {
        val successfulDelete = registrationRepository.deleteRegistration(registrationId)
        if (!successfulDelete) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "No registration with id $registrationId found")
        }
        return true
    }

    fun getRegistrationByCompetition(competitionId: Int): CompetitionRegistrationsDTO {
        val categoryRegistrations = mutableListOf<CategoryRegistrations>()
        val playersAndCategories = competitionAndPlayingCategoryRepository.getCategoriesAndPlayers(competitionId)
        val uniqueCategories = playersAndCategories.stream().map { it.categoryName }.distinct().toList()

        for (category in uniqueCategories) {
            val registeredPlayers = mutableListOf<PlayerDTO>()
            var currentCategoryId = 0
            for (entry in playersAndCategories) {
                if (entry.categoryName == category) {
                    registeredPlayers.add(playerService.getPlayer(entry.playerId))
                    currentCategoryId = entry.playingCategoryId
                }
            }
            categoryRegistrations.add(
                    CategoryRegistrations(
                            Category(currentCategoryId, category),
                            registeredPlayers)
            )
        }

        return CompetitionRegistrationsDTO(competitionService.getById(competitionId), categoryRegistrations)
    }

    fun getRegistrationByPlayerId(playerId: Int): PlayerCompetitionDTO {
        // Get player and competitions
        val playerCompetitions = mutableListOf<CompetitionAndCategoriesDTO>()
        val player = playerService.getPlayer(playerId)
        val competitionPlayingCategories = competitionAndPlayingCategoryRepository.getByPlayerId(playerId)
        val uniqueCompetitionIds = competitionPlayingCategories.map { it.competitionId }.distinct().toList()

        for (id in uniqueCompetitionIds) {
            // Get playing categories for each competitions and add player data to dto
            val competition = competitionService.getById(id)
            val categories = competitionPlayingCategories.filter { it.competitionId == id }
            playerCompetitions.add(
                    CompetitionAndCategoriesDTO(
                            competition = competition,
                            categories = categories.map { Category(it.playingCategoryId, it.categoryName) }
                    )
            )
        }
        return PlayerCompetitionDTO(player = player, competitionsAndCategories = playerCompetitions)
    }

    fun getSeed(): Int {
        return Random.nextInt(0, 16)
    }
}

data class RegistrationSinglesDTO(
        val id: Int?,
        val playerId: Int,
        val competitionPlayingCategoryId: Int
)

data class RegistrationDoublesDTO(
        val id: Int?,
        val firstPlayerId: Int,
        val secondPlayerId: Int,
        val competitionPlayingCategoryId: Int
)


data class PlayerCompetitionDTO(
        val player: PlayerDTO,
        val competitionsAndCategories: List<CompetitionAndCategoriesDTO>
)

data class CompetitionAndCategoriesDTO(
        val competition: CompetitionDTO,
        val categories: List<Category>
)

data class CompetitionRegistrationsDTO(
        val competition: CompetitionDTO,
        val registrations: List<CategoryRegistrations>
)

data class CategoryRegistrations(
        val category: Category,
        val registeredPlayers: List<PlayerDTO>
)

data class Category(
        val competitionCategory: Int,
        val categoryName: String
)