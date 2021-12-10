package com.graphite.competitionplanner.player.domain

import com.graphite.competitionplanner.club.domain.FindClub
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import org.springframework.stereotype.Component

@Component
class FindPlayer(
    val repository: IPlayerRepository,
    val findClub: FindClub
) {

    /**
     * Return the player with the given id together with its club
     *
     * @throws NotFoundException If the player with the given id cannot be found
     */
    @Throws(NotFoundException::class)
    fun byId(id: Int): PlayerWithClubDTO {
        val player = repository.findById(id)
        val club = findClub.byId(player.clubId)
        return PlayerWithClubDTO(player.id, player.firstName, player.lastName, club, player.dateOfBirth)
    }

    fun byIds(playerIds: List<Int>): List<PlayerWithClubDTO> {
        return repository.findAllForIds(playerIds)
    }

    fun byPartName(partName: String): List<PlayerWithClubDTO> {
        return repository.findByName(partName)
    }

    fun byPartNameInCompetition(partName: String, competition: CompetitionDTO): List<PlayerWithClubDTO> {
        return repository.findByNameInCompetition(partName, competition.id)
    }
}