package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.GroupStandingDTO
import com.graphite.competitionplanner.draw.interfaces.GroupStandingWithRegistrationId
import com.graphite.competitionplanner.draw.interfaces.WonLostDTO
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.tables.records.MatchRecord
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

@Component
class CalculateGroupStanding(
    val matchRepository: MatchRepository,
    val resultService: ResultService,
    val registrationService: RegistrationService,
    @Lazy val competitionDrawRepository: CompetitionDrawRepository
) {

    fun execute(categoryId: Int, groupName: String): List<GroupStandingDTO> {
        val allGroupMatches: List<MatchRecord> =
            matchRepository.getMatchesInCategoryForMatchType(categoryId, MatchType.GROUP)
        val matchesInSelectedGroup = allGroupMatches.filter { it.groupOrRound == groupName }
        val uniquePlayerRegistrations: MutableSet<Int> = mutableSetOf()
        for (match in matchesInSelectedGroup) {
            uniquePlayerRegistrations.add(match.firstRegistrationId)
            uniquePlayerRegistrations.add(match.secondRegistrationId)
        }

        val groupStandingList: List<GroupStandingWithRegistrationId> =
            getGroupStanding(uniquePlayerRegistrations, matchesInSelectedGroup)

        // If all players have played zero matches, return here with original pool positions
        if (groupStandingList.sumOf { it.matchesPlayed } == 0) {
            val originalPoolDrawList = competitionDrawRepository.getPoolDraw(categoryId, groupName)
            return groupStandingList.map { standingWithRegistrationId ->
                GroupStandingDTO(
                registrationService.getPlayersWithClubFromRegistrationId(standingWithRegistrationId.registrationId),
                originalPoolDrawList.find { it.registrationId == standingWithRegistrationId.registrationId }?.playerNumber ?: 0,
                standingWithRegistrationId.matchesWon,
                standingWithRegistrationId.matchesPlayed,
                standingWithRegistrationId.matchesWonLost,
                standingWithRegistrationId.gamesWonLost,
                standingWithRegistrationId.pointsWonLost)
            }.sortedBy { it.groupPosition }
        }
        val sortedStandingList = sortGroupStanding(groupStandingList, matchesInSelectedGroup)

        var counter = 0
        val groupStandingDTOList: MutableList<GroupStandingDTO> = mutableListOf()
        for (standing in sortedStandingList) {
            counter += 1
            groupStandingDTOList.add(
                GroupStandingDTO(
                    registrationService.getPlayersWithClubFromRegistrationId(standing.registrationId),
                    counter,
                    standing.matchesWon,
                    standing.matchesPlayed,
                    standing.matchesWonLost,
                    standing.gamesWonLost,
                    standing.pointsWonLost
                )
            )
        }
        return groupStandingDTOList
    }

    /* Groups should be sorted by:
   § 23 a. Uträkning av resultat vid poolspel
    Vid uträkning av placering i poolgrupp gäller följande:
    1. I poolspelet ger vunnen match 2 poäng, förlorad match 1 poäng och ospelad match (wo)
    eller avbruten match 0 poäng. Spelare med högst poäng kommer etta, den med näst högst
    poäng tvåa osv.
    2. Om två eller fler spelare hamnar på samma poäng, avgörs den inbördes placeringen genom
    att räkna samman poängen enbart mellan inblandade spelare.
    3. Om den inbördes poängen mellan berörda spelare fortfarande är lika beräknas den
    inbördes setkvoten.
    4. Om den inbördes setkvoten mellan berörda spelare fortfarande är lika beräknas den
    inbördes bollkvoten.
    5. Så snart en placering eller fler kan bestämmas enligt punkt 2-4 enligt ovan, tas den eller de
    spelarna bort och övriga kvarstående spelares placering börjar beräknas på nytt från punkt 2.
    6. Om det med ovanstående beräkning inte går att skilja två eller flera spelare åt, avgörs deras
    inbördes placering med hjälp av lott.
    Om en spelare lämnar wo i någon match eller avbryter pågående match kvarstår spelaren i
    poolen.
    Om en spelare lämnar wo eller avbryter en match, betraktas matchen som förlorad med 0-3 i
    set och 0-33 i bollar (om femsetsmatcher tillämpas).
     */
    private fun sortGroupStanding(
        groupStandingList: List<GroupStandingWithRegistrationId>,
        matches: List<MatchRecord>
    ): List<GroupStandingWithRegistrationId> {


        val sortedGroupStanding: MutableList<GroupStandingWithRegistrationId> = mutableListOf()

        while (sortedGroupStanding.size < groupStandingList.size) {
            // Keep adding highest remaining players of the players in the groupStandingList that have not already been added to the sorted list
            sortedGroupStanding.add(
                getHighestRemainingPlayer(groupStandingList
                    .filterNot {
                        sortedGroupStanding
                            .map { it.registrationId }
                            .contains(it.registrationId)
                    } as MutableList<GroupStandingWithRegistrationId>,
                    matches)
            )
        }

        return sortedGroupStanding
    }

    private fun getHighestRemainingPlayer(
        groupStandingList: List<GroupStandingWithRegistrationId>,
        matches: List<MatchRecord>
    ): GroupStandingWithRegistrationId {
        val bestRemainingScore = groupStandingList.map { it.groupScore }.distinct().maxOrNull()
            ?: throw RuntimeException("Group point should not be null")

        val bestRemainingPlayers = groupStandingList.filter { it.groupScore == bestRemainingScore }

        // If only one player has best remaining score, return that one
        if (bestRemainingPlayers.size == 1) {
            return bestRemainingPlayers[0]
        } else {
            val registrationIds = bestRemainingPlayers.map { it.registrationId }.toSet()

            // If two players shared the best remaining score, their mutual meeting is sent in here to calculate a new
            // subgroup standing. If so, one of them is the best remaining and will be returned here
            val standingInSubGroup = getGroupStanding(
                registrationIds,
                matches.filter {
                    registrationIds.contains(it.firstRegistrationId)
                            && registrationIds.contains(it.secondRegistrationId)
                }
            )
            val bestScoreInSubGroup = standingInSubGroup.maxOfOrNull { it.groupScore }
                ?: throw RuntimeException("Group points should not be null")
            val bestPlayersInSubGroup = standingInSubGroup.filter { it.groupScore == bestScoreInSubGroup }
            if (bestPlayersInSubGroup.size == 1) {
                return bestPlayersInSubGroup[0]
            }

            // If 3 players had best remainingScore, we need to look at games first, then points
            val bestGameDifference = standingInSubGroup.maxOfOrNull { it.gameDifference }
                ?: throw RuntimeException("Game difference should not be null")
            val playersWithBestGameDifference = standingInSubGroup.filter { it.gameDifference == bestGameDifference }
            if (playersWithBestGameDifference.size == 1) {
                return playersWithBestGameDifference[0]
            } else {
                // Look at points difference, but only for the players with the best game difference
                val bestPointsDifference = playersWithBestGameDifference.maxOfOrNull { it.pointDifference }
                    ?: throw RuntimeException("Points difference should not be null")
                val playersWithBestPointsDifference =
                    playersWithBestGameDifference.filter { it.pointDifference == bestPointsDifference }
                if (playersWithBestPointsDifference.size == 1) {
                    return playersWithBestPointsDifference[0]
                } else {
                    // If the players had exactly the same points difference too, winner should be determined by draw
                    // So select a player at random
                    return playersWithBestGameDifference[(0..1).random()]
                }
            }
        }
    }

    /* Calculated standing in a given group
    * Can also be used to calculate standing in a subgroup. For example, if three players end up with the same result,
    * their results should be recalculated without the fourth player in the group. Then this function can be reused.
     */
    private fun getGroupStanding(
        playerRegistrationIds: Set<Int>,
        matchesInGroup: List<MatchRecord>
    ): List<GroupStandingWithRegistrationId> {
        val groupStandingList = mutableListOf<GroupStandingWithRegistrationId>()
        for (playerRegistration in playerRegistrationIds) {
            val matchesWithPlayer = matchesInGroup.filter {
                it.firstRegistrationId == playerRegistration
                        || it.secondRegistrationId == playerRegistration
            }
            var matchesWon = 0
            var matchesLost = 0
            var pointsWon = 0
            var pointsLost = 0
            var gamesWon = 0
            var gamesLost = 0
            var groupPoints = 0
            for (match in matchesWithPlayer) {
                if (match.winner != null) {
                    if (match.winner == playerRegistration) {
                        matchesWon += 1
                        groupPoints += 2
                    } else {
                        matchesLost += 1
                        // Player only gets point if match was not lost on walkover
                        if (!match.wasWalkover) {
                            groupPoints += 1
                        }
                    }
                    // Calculate games and points
                    val result: ResultDTO = resultService.getResult(match.id)
                    var playerPointsInMatch = 0
                    var otherPlayerPointsInMatch = 0
                    if (playerRegistration == match.firstRegistrationId) {
                        for (game in result.gameList) {
                            playerPointsInMatch += game.firstRegistrationResult
                            otherPlayerPointsInMatch += game.secondRegistrationResult
                            if (game.firstRegistrationResult > game.secondRegistrationResult) gamesWon += 1 else gamesLost += 1

                        }
                    } else if (playerRegistration == match.secondRegistrationId) {
                        for (game in result.gameList) {
                            playerPointsInMatch += game.secondRegistrationResult
                            otherPlayerPointsInMatch += game.firstRegistrationResult
                            if (game.secondRegistrationResult > game.firstRegistrationResult) gamesWon += 1 else gamesLost += 1
                        }
                    }
                    pointsWon += playerPointsInMatch
                    pointsLost += otherPlayerPointsInMatch
                }
            }
            groupStandingList.add(
                GroupStandingWithRegistrationId(
                    registrationId = playerRegistration,
                    matchesWon = matchesWon,
                    matchesPlayed = matchesWon + matchesLost,
                    matchesWonLost = WonLostDTO(matchesWon, matchesLost),
                    gamesWonLost = WonLostDTO(gamesWon, gamesLost),
                    pointsWonLost = WonLostDTO(pointsWon, pointsLost),
                    matchDifference = matchesWon - matchesLost,
                    gameDifference = gamesWon - gamesLost,
                    pointDifference = pointsWon - pointsLost,
                    groupScore = groupPoints
                )
            )
        }
        return groupStandingList
    }
}