package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.api.competition.DrawDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.api.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.stream.Stream

@SpringBootTest
class TestDrawCupOnly(
    @Autowired val testUtil: TestUtil,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationService: RegistrationService,
    @Autowired val matchService: MatchService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val competitionDrawRepository: CompetitionDrawRepository,
    @Autowired val drawService: DrawService
) {
    var competitionCategoryId = 0
    lateinit var draw : DrawDTO

    val dataGenerator = DataGenerator()

    @BeforeEach
    fun setupCompetition() {

        competitionCategoryId = testUtil.addCompetitionCategory("Flickor 13")

        val original = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)

        val updatedSettings = dataGenerator.newCompetitionCategoryUpdateSpec(
            settings = dataGenerator.newGeneralSettingsSpec(
                cost = original.settings.cost,
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = original.settings.playersPerGroup,
                playersToPlayOff = original.settings.playersToPlayOff,
                poolDrawStrategy = original.settings.poolDrawStrategy
            ),
            gameSettings = dataGenerator.newGameSettingsSpec(
                original.gameSettings.numberOfSets,
                original.gameSettings.winScore,
                original.gameSettings.winMargin,
                original.gameSettings.differentNumberOfGamesFromRound,
                original.gameSettings.numberOfSetsFinal,
                original.gameSettings.winScoreFinal,
                original.gameSettings.winMarginFinal,
                original.gameSettings.tiebreakInFinalGame,
                original.gameSettings.winScoreTiebreak,
                original.gameSettings.winMarginTieBreak
            )
        )

        competitionCategoryService.updateCompetitionCategory(original.id, updatedSettings)
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
        competitionCategoryService.deleteCategoryInCompetition(competitionCategoryId)
    }

    @ParameterizedTest
    @MethodSource("playersAndMatches")
    fun thenTheCorrectNumberOfMatchesShouldBeRegistered(
        numberOfPlayers: Int,
        expectedNumberOfMatches: Int,
        expectedRound: Round
    )
    {
        // Setup
        val allPlayers = playerRepository.getAll()

        val players = allPlayers.subList(0, numberOfPlayers)
        for (player in players) {
            registrationService.registerPlayerSingles(RegistrationSinglesSpec( player.id, competitionCategoryId))
        }

        // Act
        draw = drawService.createDraw(competitionCategoryId)

        // Assert
        val matches = matchService.getMatchesInCategory(competitionCategoryId)
        Assertions.assertEquals(expectedNumberOfMatches, matches.size,
            "In a draw with $numberOfPlayers players there should be initially $expectedNumberOfMatches matches");

        for (match in matches){
            Assertions.assertEquals(expectedRound.name, match.groupOrRound)
        }

        val rounds = draw.playOff.rounds
        for (round in rounds){
            Assertions.assertEquals(expectedRound, round.round)
        }
    }

    private companion object {
        @JvmStatic
        fun playersAndMatches() = Stream.of(
            Arguments.of(9, 8, Round.ROUND_OF_16),
            Arguments.of(8, 4, Round.QUARTER_FINAL),
            Arguments.of(7, 4, Round.QUARTER_FINAL),
            Arguments.of(6, 4, Round.QUARTER_FINAL),
            Arguments.of(5, 4, Round.QUARTER_FINAL),
            Arguments.of(4, 2, Round.SEMI_FINAL),
            Arguments.of(3, 2, Round.SEMI_FINAL)
        )
    }
}