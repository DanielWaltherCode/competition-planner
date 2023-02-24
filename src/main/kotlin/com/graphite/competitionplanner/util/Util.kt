package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithWithdrawnStatus
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Component
class Util(
        val clubRepository: ClubRepository,
        val env: Environment,
        val registrationRepository: RegistrationRepository
) {

    fun getClubIdOrDefault(clubName: String): Int {
        return clubRepository.findByName(clubName).id
    }

    fun getTokenSecret(): String {
        return env.getProperty("tokenSecret")!!
    }

    fun getPlayersAndWithdrawnStatus(playerList: List<PlayerWithClubDTO>,
                                            competitionCategoryId: Int): List<PlayerWithWithdrawnStatus> {
        val playersWithWithdrawnStatus = mutableListOf<PlayerWithWithdrawnStatus>()
        for (player in playerList) {
            val registration = registrationRepository.getRegistrationStatus(player.id, competitionCategoryId)
            var hasWithdrawn = false
            if (registration != null && registration.status != "PLAYING") {
                hasWithdrawn = true
            }
            playersWithWithdrawnStatus.add(PlayerWithWithdrawnStatus(player, hasWithdrawn))
        }
        return playersWithWithdrawnStatus
    }

}
