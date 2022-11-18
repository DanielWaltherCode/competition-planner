package com.graphite.competitionplanner.registration.service

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.domain.FindPlayer
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.domain.*
import com.graphite.competitionplanner.registration.interfaces.*
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import org.springframework.stereotype.Service

@Service
class RegistrationService(
    val registrationRepository: RegistrationRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val registerPlayerToCompetition: RegisterPlayerToCompetition,
    val registerDoubleToCompetition: RegisterDoubleToCompetition,
    val unregister: Unregister,
    val getPlayersFromRegistration: GetPlayersFromRegistration,
    val findPlayer: FindPlayer,
    val matchService: MatchService,
    val searchRegistrations: SearchRegistrations,
    val findCompetitions: FindCompetitions,
) {

    fun registerPlayerSingles(spec: RegistrationSinglesSpec): RegistrationSinglesDTO {
        return registerPlayerToCompetition.execute(spec)
    }

    fun registerPlayersDoubles(spec: RegistrationDoublesSpec): RegistrationDoublesDTO {
        return registerDoubleToCompetition.execute(spec)
    }

    fun unregister(registrationId: Int) {
        unregister.execute(registrationId)
    }

    fun getPlayersWithClubFromRegistrationId(registrationId: Int?): List<PlayerWithClubDTO> {
        if (registrationId == null) {
            return emptyList()
        }
        val players: List<PlayerDTO> = getPlayersFromRegistration.execute(registrationId)
        return findPlayer.byIds(players.map { it.id })
    }

    fun getRegisteredPlayers(competitionId: Int, searchType: String): RegisteredPlayersDTO {
        val competition = findCompetitions.byId(competitionId)
        return searchRegistrations.execute(competition, SearchType.valueOf(searchType.uppercase()))
    }

    fun getRegistrationByPlayerId(playerId: Int): PlayerCompetitionDTO {
        // Get player and competitions
        val playerCompetitions = mutableListOf<CompetitionAndCategoriesDTO>()
        val player = findPlayer.byId(playerId)
        val competitionPlayingCategories = competitionCategoryRepository.getByPlayerId(playerId)
        val uniqueCompetitionIds = competitionPlayingCategories.map { it.competitionId }.distinct()

        for (id in uniqueCompetitionIds) {
            // Get playing categories for each competition and add player data to dto
            val competition = findCompetitions.byId(id)
            val categories = competitionPlayingCategories.filter { it.competitionId == id }
            playerCompetitions.add(
                CompetitionAndCategoriesDTO(
                    competition = competition,
                    categories = categories.map { SimpleCompetitionCategoryDTO(it.categoryId, it.categoryName) }
                )
            )
        }
        return PlayerCompetitionDTO(
            player = player,
            competitionsAndCategories = playerCompetitions
        )
    }

    fun getPlayersInCompetitionCategory(competitionCategoryId: Int): List<List<PlayerWithClubDTO>> {
        val players: List<Pair<Registration, PlayerWithClubDTO>> = registrationRepository.getAllRegisteredPlayersInCompetitionCategory(competitionCategoryId)
            .filter { it.first is Registration.Real } // Sort out BYEs and Placeholders if any.
        return players
            .groupBy {(registration, _) -> (registration as Registration.Real).id }
            .map { (_, listOfPairs) -> listOfPairs.map { (_, player) -> player } }
    }

    fun getRegistrationsForPlayerInCompetition(competitionId: Int, playerId: Int): PlayerRegistrationDTO {
        val player = findPlayer.byId(playerId)
        val registrationsInCompetition =
            competitionCategoryRepository.getRegistrationsForPlayerInCompetition(competitionId, playerId)
        val registrationDTOs = mutableListOf<CategoryRegistrationDTO>()
        for (registration in registrationsInCompetition) {
            var accompanyingPlayer: PlayerWithClubDTO? = null
            // If categoryType == doubles, fetch accompanying player
            if (registration.categoryType == "DOUBLES") {
                val accompanyingPlayerId =
                    competitionCategoryRepository.getAccompanyingPlayerId(registration.registrationId, playerId)
                if (accompanyingPlayerId != null) {
                    accompanyingPlayer = findPlayer.byId(accompanyingPlayerId)
                }
            }
            // Get matches
            val matches = matchService.getMatchesInCompetitionForRegistration(competitionId, registration.registrationId)
            registrationDTOs.add(
                CategoryRegistrationDTO(
                    registration.registrationId,
                    CompetitionCategoryWithTypeDTO(registration.categoryId, registration.categoryName, registration.categoryType),
                    matches,
                    accompanyingPlayer,
                    registration.registrationStatus
                )
            )
        }
        return PlayerRegistrationDTO(player, registrationDTOs)
    }
}

data class PlayerCompetitionDTO(
    val player: PlayerWithClubDTO,
    val competitionsAndCategories: List<CompetitionAndCategoriesDTO>
)

data class CompetitionAndCategoriesDTO(
    val competition: CompetitionDTO,
    val categories: List<SimpleCompetitionCategoryDTO>
)

data class RegisteredPlayersDTO(
    val groupingType: SearchType,
    val groupingsAndPlayers: Map<String, Set<PlayerWithClubDTO>>,
    val numberOfPlayers: Int = 0
)

data class SimpleCompetitionCategoryDTO(
    val id: Int,
    val name: String
)

data class CompetitionCategoryWithTypeDTO(
    val id: Int,
    val name: String,
    val type: String
)

enum class SearchType {
    CLUB, NAME, CATEGORY
}
