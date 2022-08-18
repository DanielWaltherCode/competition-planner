package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.PlayerRegistrationStatus
import com.graphite.competitionplanner.result.api.GameSpec
import com.graphite.competitionplanner.result.api.ResultSpec
import com.graphite.competitionplanner.result.domain.AddResult
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.result.service.ResultService
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestWithdraw(
    @Autowired val matchRepository2: MatchRepository,
    @Autowired val createDraw: CreateDraw,
    @Autowired val withdraw: Withdraw,
    @Autowired val findCompetitions: FindCompetitions,
    @Autowired val resultService: ResultService,
    @Autowired val addResult: AddResult,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: IPlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository,
    @Autowired matchRepository: MatchRepository,
    @Autowired resultRepository: IResultRepository
): BaseRepositoryTest(
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository,
    matchRepository,
    resultRepository
) {

    @Test
    fun testWithdrawBeforeCompetition() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()

        val suffix = listOf("A", "B", "C", "D")
        val players = suffix.map { club.addPlayer("Player$it") }
        val registrations = players.map { competitionCategory.registerPlayer(it) }

        val walkoverRegistrations = registrations.first()

        matchRepository2.store(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = walkoverRegistrations.id,
            secondRegistrationId = registrations.take(2).last().id
        ))

        matchRepository2.store(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = walkoverRegistrations.id,
            secondRegistrationId = registrations.take(3).last().id
        ))

        matchRepository2.store(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = walkoverRegistrations.id,
            secondRegistrationId = registrations.take(4).last().id
        ))

        // Act
        withdraw.walkOver(competition.id, competitionCategory.id, walkoverRegistrations.id)

        // Assert
        val playerRegistration = registrationRepository.getPlayerRegistration(walkoverRegistrations.id)
        Assertions.assertEquals(playerRegistration.status, PlayerRegistrationStatus.WALK_OVER.name)

        val updatedMatches = matchRepository2.getMatchesInCompetitionForRegistration(competition.id, walkoverRegistrations.id)

        Assertions.assertTrue(updatedMatches.all { it.winner != null && it.winner != walkoverRegistrations.id },
            "All matches should have a winner and none should be the registration that went walkover")
    }

    @Test
    fun testGiveWalkoverAfterMatchHasBeenPlayed() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()

        val suffix = listOf("A", "B", "C", "D")
        val players = suffix.map { club.addPlayer("Player$it") }
        val registrations = players.map { competitionCategory.registerPlayer(it) }

        val walkoverRegistrations = registrations.first()

        val match1 = matchRepository2.store(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = walkoverRegistrations.id,
            secondRegistrationId = registrations.take(2).last().id
        ))

        matchRepository2.store(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = walkoverRegistrations.id,
            secondRegistrationId = registrations.take(3).last().id
        ))

        matchRepository2.store(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = walkoverRegistrations.id,
            secondRegistrationId = registrations.take(4).last().id
        ))

        // Register result for first match
        val gameResults = (1..3).map { GameSpec(it, 11, 5) }
        addResult.execute(
            match1,
            ResultSpec(gameResults),
            competitionCategory
        )

        // Act
        withdraw.walkOver(competition.id, competitionCategory.id, walkoverRegistrations.id)

        // Assert
        val playerRegistration = registrationRepository.getPlayerRegistration(walkoverRegistrations.id)
        Assertions.assertEquals(playerRegistration.status, PlayerRegistrationStatus.WALK_OVER.name)

        val updatedMatches = matchRepository2.getMatchesInCompetitionForRegistration(competition.id, walkoverRegistrations.id)

        val retiredPlayersResults = mutableListOf<Int>()
        var nrWalkoverMatches = 0
        var nrWonMatches = 0
        for (updatedMatch in updatedMatches) {
            if (updatedMatch.wasWalkover) {
                nrWalkoverMatches++
            }
            if (updatedMatch.winner == walkoverRegistrations.id) {
                nrWonMatches++
            }
            val result = resultService.getResult(updatedMatch.id)
            for (gameResult in result.gameList) {
                retiredPlayersResults.add(gameResult.firstRegistrationResult)
            }
        }
        Assertions.assertTrue(updatedMatches.all { it.winner != null }, "All matches should have a winner")

        Assertions.assertEquals(2, nrWalkoverMatches,
            "We played one match and then went walkover. Expected the remaining 2 matches to be marked as walkover")
        Assertions.assertEquals(1, nrWonMatches,
            "We won that first game. It should have been registered.")
        Assertions.assertEquals(9, retiredPlayersResults.size,
            "There's not correct number of registered sets.")
        Assertions.assertEquals(6, retiredPlayersResults.filter { r -> r == 0 }.size,
            "Two full matches i.e. 6 sets should been marked 11-0")
    }
}