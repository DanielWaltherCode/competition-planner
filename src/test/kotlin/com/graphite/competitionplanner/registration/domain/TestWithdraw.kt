package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competitioncategory.domain.DeleteCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.PlayerRegistrationStatus
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.domain.AddResult
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestWithdraw(
    @Autowired val testUtil: TestUtil,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationService: RegistrationService,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val matchService: MatchService,
    @Autowired val matchRepository: MatchRepository,
    @Autowired val findCompetitionCategory: FindCompetitionCategory,
    @Autowired val deleteCompetitionCategory: DeleteCompetitionCategory,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val createDraw: CreateDraw,
    @Autowired val withdraw: Withdraw,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val resultService: ResultService,
    @Autowired val addResult: AddResult
) {
    var competitionCategoryId = 0
    var competitionId = 0

    @BeforeEach
    fun setupCompetition() {
        competitionCategoryId = testUtil.addCompetitionCategory("Flickor 14")
        val umeaId = testUtil.getClubIdOrDefault("Umeå IK")
        val umeaCompetitions = findCompetitions.thatBelongTo(umeaId)
        competitionId = umeaCompetitions[0].id
    }

    @AfterEach
    fun removeMatchesAndRegistrations() {
        // Remove matches
        matchService.deleteMatchesInCategory(competitionCategoryId)

        // Remove pool draw
        competitionDrawRepository.deleteGroupsInCategory(competitionCategoryId)

        // Remove registrations and delete category
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competitionCategoryId)
        for (id in registrationIds) {
            registrationService.unregister(id)
        }
        deleteCompetitionCategory.execute(competitionCategoryId)
    }

    @Test
    fun testWithdrawBeforeCompetition() {
        // Setup
        val allPlayers = playerRepository.getAll()
        val registrations = mutableListOf<RegistrationSinglesDTO>()
        for (player in allPlayers) {
            registrations.add(
                registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
            )
        }
        createDraw.execute(competitionCategoryId)

        // Act
        val playerToWithdraw = registrations[0].id
        withdraw.beforeCompetition(competitionId, competitionCategoryId, playerToWithdraw)


        // Assert

        // Player status should be withdrawn
        val playerRegistration = registrationRepository.getPlayerRegistration(playerToWithdraw)
        Assertions.assertEquals(playerRegistration.status, PlayerRegistrationStatus.WITHDRAWN.name)

        // All matches should be lost
        val matches = matchRepository.getMatchesInCompetitionForRegistration(competitionId, playerToWithdraw)
        for (match in matches) {
            Assertions.assertTrue(match.winner != null)
            Assertions.assertTrue(match.winner != playerToWithdraw)
            Assertions.assertTrue(match.wasWalkover)
        }

    }

    @Test
    fun testGiveWalkover() {
        // Setup
        val allPlayers = playerRepository.getAll()
        val registrations = mutableListOf<RegistrationSinglesDTO>()
        for (player in allPlayers) {
            registrations.add(
                registrationService.registerPlayerSingles(RegistrationSinglesSpec(player.id, competitionCategoryId))
            )
        }
        createDraw.execute(competitionCategoryId)

        // Act
        val playerToWithdraw = registrations[2].id

        // Register result for one match
        val matches = matchRepository.getMatchesInCompetitionForRegistration(competitionId, playerToWithdraw)
        val match = matches[0]
        val gameResults = mutableListOf<GameSpec>()
        for (i in 1..3) {
            gameResults.add(
                GameSpec(
                    gameNumber = i,
                    firstRegistrationResult = 5,
                    secondRegistrationResult = 11
                )
            )
        }
        addResult.execute(
            matchRepository.getMatch2(match.id),
            ResultSpec(gameResults),
            findCompetitionCategory.byId(competitionCategoryId)
        )
        withdraw.walkOver(competitionId, competitionCategoryId, playerToWithdraw)

        // Assert

        // Player status should be withdrawn
        val playerRegistration = registrationRepository.getPlayerRegistration(playerToWithdraw)
        Assertions.assertEquals(playerRegistration.status, PlayerRegistrationStatus.WALK_OVER.name)

        val updatedMatches = matchRepository.getMatchesInCompetitionForRegistration(competitionId, playerToWithdraw)

        val retiredPlayersResults = mutableListOf<Int>()
        var nrWalkoverMatches = 0
        var nrWonMatches = 0
        for (updatedMatch in updatedMatches) {
            Assertions.assertTrue(updatedMatch.winner != null)
            if (updatedMatch.wasWalkover) {
                nrWalkoverMatches++
            }
            if( updatedMatch.winner == playerToWithdraw) {
                nrWonMatches++
            }
            val result = resultService.getResult(updatedMatch.id)
            for (gameResult in result.gameList) {
                if (updatedMatch.firstRegistrationId == playerToWithdraw) {
                    retiredPlayersResults.add(gameResult.firstRegistrationResult)
                } else {
                    if (updatedMatch.secondRegistrationId == playerToWithdraw) {
                        retiredPlayersResults.add(gameResult.secondRegistrationResult)
                    }
                }
            }
        }
        Assertions.assertEquals(2, nrWalkoverMatches)
        Assertions.assertTrue(nrWonMatches <= 1)
        Assertions.assertEquals(9, retiredPlayersResults.size)
        // Two full matches should be 11-0 in each game,
        Assertions.assertEquals(6, retiredPlayersResults.filter { r -> r == 0 }.size )

    }
}