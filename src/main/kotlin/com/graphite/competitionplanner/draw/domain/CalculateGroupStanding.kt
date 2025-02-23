package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.GroupStandingDTO
import com.graphite.competitionplanner.draw.interfaces.GroupStandingWithRegistrationId
import com.graphite.competitionplanner.draw.interfaces.SortedBy
import com.graphite.competitionplanner.draw.interfaces.WonLostDTO
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.domain.MatchType
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
                    originalPoolDrawList.find { it.registrationId == standingWithRegistrationId.registrationId }?.playerNumber
                        ?: 0,
                    standingWithRegistrationId.matchesWon,
                    standingWithRegistrationId.matchesPlayed,
                    standingWithRegistrationId.matchesWonLost,
                    standingWithRegistrationId.gamesWonLost,
                    standingWithRegistrationId.pointsWonLost,
                    0,
                    null,
                    SortedBy.ORIGINAL
                )
            }.sortedBy { it.groupPosition }
        }
        val sortedStandingList = sortGroupStanding(groupStandingList, matchesInSelectedGroup)

        // If pool is already finished, fetch final result and re-sort the group based on this
        // In all cases except for when draw decided the group, the order should be the same
        val pool = competitionDrawRepository.getPool(categoryId, groupName)
        if (competitionDrawRepository.isPoolFinished(pool.id)) {
            val finalPoolResult = competitionDrawRepository.getPoolResult(pool.id)
            val groupStandingDTOList: MutableList<GroupStandingDTO> = mutableListOf()
            for (standing in sortedStandingList) {
                groupStandingDTOList.add(
                    convertToGroupStandingDTO(
                        standing,
                        finalPoolResult.first { it.registrationId == standing.registrationId }.poolPosition
                    )
                )
            }
            return groupStandingDTOList.sortedBy { it.groupPosition }
        } else {
            var counter = 0
            val groupStandingDTOList: MutableList<GroupStandingDTO> = mutableListOf()
            for (standing in sortedStandingList) {
                counter += 1
                groupStandingDTOList.add(
                    convertToGroupStandingDTO(standing, counter)
                )
            }
            return groupStandingDTOList
        }

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
            sortedGroupStanding.addAll(
                getHighestRemainingPlayers(groupStandingList
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

    private fun getHighestRemainingPlayers(
        groupStandingList: List<GroupStandingWithRegistrationId>,
        matches: List<MatchRecord>
    ): List<GroupStandingWithRegistrationId> {
        val bestRemainingScore = groupStandingList.map { it.groupScore }.distinct().maxOrNull()
            ?: throw RuntimeException("Group point should not be null")

        val bestRemainingPlayers = groupStandingList.filter { it.groupScore == bestRemainingScore }

        // If only one player has best remaining score, return that one
        if (bestRemainingPlayers.size == 1) {
            val playerToReturn = bestRemainingPlayers[0]
            playerToReturn.sortedBy = SortedBy.MATCH_SCORE
            return mutableListOf(playerToReturn)
        }

        val registrationIds = bestRemainingPlayers.map { it.registrationId }.toSet()

        // If 2 or more players shared the best remaining score, their mutual meetings are sent in here to calculate a new
        // subgroup standing.
        val standingInSubGroup: List<GroupStandingWithRegistrationId> = getGroupStanding(
            registrationIds,
            matches.filter {
                registrationIds.contains(it.firstRegistrationId)
                        && registrationIds.contains(it.secondRegistrationId)
            }
        )
        val bestScoreInSubGroup = standingInSubGroup.maxOfOrNull { it.groupScore }
            ?: throw RuntimeException("Group points should not be null")

        // The best players have not played against each other. Return random order
        if (bestScoreInSubGroup == 0) {
            val playersToReturn: MutableList<GroupStandingWithRegistrationId> = mutableListOf()
            val shuffledPlayers = standingInSubGroup.shuffled()
            for (player in shuffledPlayers) {
                playersToReturn.add(groupStandingList.first { it.registrationId == player.registrationId })
            }
            return playersToReturn
        }

        // Add subgroup result calculation to list we will return
        for (fullResult in groupStandingList) {
            try {
                val subGroupResult = standingInSubGroup.first { it.registrationId == fullResult.registrationId }
                fullResult.subgroupStanding = subGroupResult
            }
            // If the player in the group is not in this particular subgroup, just continue
            catch (exception: NoSuchElementException) {
                continue
            }
        }

        // If players had distinct group scores, sort on that measure and return here
        val distinctSubGroupScores = standingInSubGroup.map { it.groupScore }.distinct()
        if (distinctSubGroupScores.size == standingInSubGroup.size) {
            val subgroupList: MutableList<GroupStandingWithRegistrationId> = mutableListOf()
            for (standing in standingInSubGroup.sortedByDescending { it.groupScore }) {
                val fullResult = groupStandingList.first { it.registrationId == standing.registrationId }
                fullResult.sortedBy = SortedBy.MATCH_SCORE
                subgroupList.add(fullResult)
            }
            return subgroupList
        }

        // If players are still tied, there are at least 3 people with the same score.
        // We need to look at games first, then points

        val distinctGameQuotients = standingInSubGroup.map { it.gameQuotient }.distinct().sortedDescending()
        if (distinctGameQuotients.size == standingInSubGroup.size) {
            val subgroupList: MutableList<GroupStandingWithRegistrationId> = mutableListOf()
            for (standing in standingInSubGroup.sortedByDescending { it.gameQuotient }) {
                val fullResult = groupStandingList.first { it.registrationId == standing.registrationId }
                fullResult.sortedBy = SortedBy.GAME_QUOTIENT
                subgroupList.add(fullResult)
            }
            return subgroupList
        }

        // If at least some players are still tied, group them by game quotients and check points won
        val playersToReturn: MutableList<GroupStandingWithRegistrationId> = mutableListOf()
        for (gameQuotient in distinctGameQuotients) {
            val playersWithThatGameQuotient = standingInSubGroup.filter { it.gameQuotient == gameQuotient }
            if (playersWithThatGameQuotient.size == 1) {
                val fullResult =
                    groupStandingList.first { it.registrationId == playersWithThatGameQuotient[0].registrationId }
                fullResult.sortedBy = SortedBy.POINT_QUOTIENT
                playersToReturn.add(fullResult)
            } else {
                val distinctPointQuotients =
                    playersWithThatGameQuotient.map { it.pointQuotient }.distinct().sortedDescending()
                if (distinctPointQuotients.size == playersWithThatGameQuotient.size) {
                    for (player in playersWithThatGameQuotient.sortedByDescending { it.pointQuotient }) {
                        val fullResult = groupStandingList.first { it.registrationId == player.registrationId }
                        fullResult.sortedBy = SortedBy.POINT_QUOTIENT
                        playersToReturn.add(fullResult)
                    }
                }
                // If players with same game quotient also have same point quotient, order randomly
                else {
                    for (pointQuotient in distinctPointQuotients) {
                        val playersWithSameGameAndPointQuotient =
                            playersWithThatGameQuotient.filter { it.pointQuotient == pointQuotient }
                        // This happens if three players had same game quotient, and two of them had same point quotient
                        if (playersWithSameGameAndPointQuotient.size == 1) {
                            val fullResult =
                                groupStandingList.first { it.registrationId == playersWithSameGameAndPointQuotient[0].registrationId }
                            fullResult.sortedBy = SortedBy.POINT_QUOTIENT
                            playersToReturn.add(fullResult)
                        }
                        // Use .shuffled() to randomly sort and add the tied players
                        else {
                            val shuffledPlayers = playersWithSameGameAndPointQuotient.shuffled()
                            for (player in shuffledPlayers) {
                                val fullResult = groupStandingList.first { it.registrationId == player.registrationId }
                                fullResult.sortedBy = SortedBy.DRAW
                                playersToReturn.add(fullResult)
                            }
                        }
                    }
                }
            }
        }

        return playersToReturn
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
                    gameQuotient = gamesWon.toFloat() / gamesLost.toFloat(),
                    pointQuotient = pointsWon.toFloat() / pointsLost.toFloat(),
                    groupScore = groupPoints,
                    null,
                    SortedBy.ORIGINAL
                )
            )
        }
        return groupStandingList
    }

    private fun convertToGroupStandingDTO(
        groupWithRegId: GroupStandingWithRegistrationId,
        positionInGroup: Int,
    ): GroupStandingDTO {
        return GroupStandingDTO(
            player = registrationService.getPlayersWithClubFromRegistrationId(groupWithRegId.registrationId),
            groupPosition = positionInGroup,
            matchesWon = groupWithRegId.matchesWon,
            matchesPlayed = groupWithRegId.matchesPlayed,
            matchesWonLost = groupWithRegId.matchesWonLost,
            gamesWonLost = groupWithRegId.gamesWonLost,
            pointsWonLost = groupWithRegId.pointsWonLost,
            groupScore = groupWithRegId.groupScore,
            subgroupStanding = if (groupWithRegId.subgroupStanding == null) null else
                GroupStandingDTO(
                    player = registrationService.getPlayersWithClubFromRegistrationId(groupWithRegId.subgroupStanding!!.registrationId),
                    groupPosition = 0, // Maybe work on actually setting this correctly in the sorting algorithm
                    matchesWon = groupWithRegId.subgroupStanding!!.matchesWon,
                    matchesPlayed = groupWithRegId.subgroupStanding!!.matchesPlayed,
                    matchesWonLost = groupWithRegId.subgroupStanding!!.matchesWonLost,
                    gamesWonLost = groupWithRegId.subgroupStanding!!.gamesWonLost,
                    pointsWonLost = groupWithRegId.subgroupStanding!!.pointsWonLost,
                    groupScore = groupWithRegId.subgroupStanding!!.groupScore,
                    subgroupStanding = null,
                    sortedBy = groupWithRegId.sortedBy
                ),
            sortedBy = groupWithRegId.sortedBy
        )
    }
}