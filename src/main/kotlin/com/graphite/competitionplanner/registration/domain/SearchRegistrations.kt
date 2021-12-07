package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.service.RegisteredPlayersDTO
import com.graphite.competitionplanner.registration.service.SearchType
import org.springframework.stereotype.Component

@Component
class SearchRegistrations(
    val registrationRepository: IRegistrationRepository
) {

    fun execute(competition: CompetitionDTO, searchType: SearchType): RegisteredPlayersDTO {

        return when(searchType) {
            SearchType.CLUB -> getResultGroupedByClub(competition)
            SearchType.NAME -> getResultGroupedByLastName(competition)
            SearchType.CATEGORY -> getResultGroupedByCompetitionCategory(competition)
        }
    }

    fun getResultGroupedByClub(competition: CompetitionDTO) : RegisteredPlayersDTO {
        val players = registrationRepository.getAllRegisteredPlayersInCompetition(competition.id).sortedBy { it.club.name }
        val clubNames = players.map { it.club.name }.distinct()
        val pairs = clubNames.associateWith { clubName ->
            players.filter { player ->
                player.club.name.uppercase() == clubName.uppercase()
            }.toSet()
        }
        return RegisteredPlayersDTO(SearchType.CLUB, pairs)
    }

    fun getResultGroupedByLastName(competition: CompetitionDTO) : RegisteredPlayersDTO {
        val players = registrationRepository.getAllRegisteredPlayersInCompetition(competition.id).sortedBy { it.lastName }
        val initials = players.map { it.lastName.first().uppercase() }.distinct()
        val pairs = initials.associateWith { initial ->
            players.filter { player ->
                player.lastName.first().uppercase() == initial.uppercase()
            }.toSet()
        }
        return RegisteredPlayersDTO(SearchType.NAME, pairs)
    }

    fun getResultGroupedByCompetitionCategory(competition: CompetitionDTO): RegisteredPlayersDTO {
        val categoriesAndPlayers = registrationRepository.getCategoriesAndPlayersInCompetition(competition.id)
        val categoryNames = categoriesAndPlayers.map { (category, _) -> category.name  }
        val pairs = categoryNames.associateWith { categoryName ->
            categoriesAndPlayers.filter { (category, _) ->
                category.name == categoryName
            }.map { (_, player) -> player }.toSet()
        }

        return RegisteredPlayersDTO(SearchType.CATEGORY, pairs)
    }
}