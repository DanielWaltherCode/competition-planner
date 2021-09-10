package com.graphite.competitionplanner.draw

import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.api.competition.DrawDTO
import com.graphite.competitionplanner.registration.api.RegistrationSinglesSpec
import com.graphite.competitionplanner.domain.entity.DrawType
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.draw.repository.CompetitionDrawRepository
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.draw.service.DrawService
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.*
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
    @Autowired val drawService: DrawService,
    @Autowired val categoryService: CategoryService
) {
    var competitionCategoryId = 0
    lateinit var draw : DrawDTO


    @BeforeEach
    fun setupCompetition(){

        competitionCategoryId = testUtil.addCompetitionCategory("Flickor 13")

        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategoryId)
        val categoryMetadataSpec = CategoryMetadataSpec(
            cost = categoryMetadata.cost,
            drawType = DrawType.CUP_ONLY,
            nrPlayersPerGroup = categoryMetadata.nrPlayersPerGroup,
            nrPlayersToPlayoff = categoryMetadata.nrPlayersToPlayoff,
            poolDrawStrategy = categoryMetadata.poolDrawStrategy
        )

        categoryService.updateCategoryMetadata(competitionCategoryId, categoryMetadata.id, categoryMetadataSpec)
        //registrationService.registerPlayerSingles(RegistrationSinglesDTO(null, 0, competitionCategoryId))
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