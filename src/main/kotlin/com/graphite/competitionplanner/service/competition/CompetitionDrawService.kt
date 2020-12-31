package com.graphite.competitionplanner.service.competition

import com.graphite.competitionplanner.repositories.DrawTypes
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.service.PlayerDTO
import com.graphite.competitionplanner.service.RegistrationService
import org.springframework.stereotype.Service

@Service
class CompetitionDrawService(
    val categoryService: CategoryService,
    val registrationService: RegistrationService,
    val registrationRepository: RegistrationRepository
) {

    fun createDraw(competitionCategoryId: Int): MutableMap<Int, MutableList<PlayerDTO>> {
        // Check draw type and fetch players signed up in this category
        // its easier to work with registration ids since it could be doubles and then translate back to
        // players after the draw is made

        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId) ;
        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)
        val playerGroups = mutableMapOf<Int, MutableList<PlayerDTO>>()

        if(categoryMetadata.drawType.name == DrawTypes.POOL_ONLY.name ||
            categoryMetadata.drawType.name == DrawTypes.POOL_AND_CUP.name) {
            val numberOfPlayers = registrationIds.size

            // If remainder is zero, number of registered players fits perfectly with
            // group size. Otherwise add one more group
            val remainder = numberOfPlayers.rem(categoryMetadata.nrPlayersPerGroup)
            var nrGroups = numberOfPlayers / categoryMetadata.nrPlayersPerGroup
            if (remainder != 0) {
                nrGroups += 1
            }

            val groupMap = mutableMapOf<Int, MutableList<Int>>()

            // Add lists to hold registration ids for each group
            var counter = 0
            while (counter < nrGroups) {
                groupMap.put(counter +1, mutableListOf())
                counter += 1
            }

            // TODO: Add seeding
            // Add one player to each group, then start over from the top
            counter = 0
            for(id in registrationIds) {
                groupMap[counter+1]?.add(id)
                counter += 1

                if (counter == nrGroups) {
                    counter = 0
                }
            }

            // TODO: Decide how to handle doubles
            // For now, replace registration ids with PlayerDTO
            for (key in groupMap.keys) {
                playerGroups[key] = mutableListOf()
                for (registrationId in groupMap[key]!!) {
                    val players = registrationService.getPlayersFromRegistrationId(registrationId)
                    if (players.size > 1) {
                        println("Warning, more than one player returned for singles")
                    }
                    playerGroups[key]?.add(players[0])
                }
            }
        }
        return playerGroups
    }
}

