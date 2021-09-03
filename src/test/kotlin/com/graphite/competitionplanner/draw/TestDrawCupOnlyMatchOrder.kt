package com.graphite.competitionplanner.draw

import com.graphite.competitionplanner.api.*
import com.graphite.competitionplanner.api.competition.*
import com.graphite.competitionplanner.domain.dto.PlayerWithClubDTO
import com.graphite.competitionplanner.repositories.PlayerRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.service.*
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionDTO
import com.graphite.competitionplanner.service.draw.DrawService
import com.graphite.competitionplanner.service.draw.DrawType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.random.Random

@SpringBootTest
class TestDrawCupOnlyMatchOrder(
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationService: RegistrationService,
    @Autowired val matchService: MatchService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val drawService: DrawService,
    @Autowired val categoryService: CategoryService,
    @Autowired val clubApi: ClubApi,
    @Autowired val competitionApi: CompetitionApi,
    @Autowired val playerApi: PlayerApi,
    @Autowired val competitionCategoryApi: CompetitionCategoryApi,
    @Autowired val categoryApi: CategoryApi
) {
    lateinit var club: ClubSpec
    lateinit var competition: CompetitionDTO
    lateinit var competitionCategory: CompetitionCategoryDTO
    var players = mutableListOf<PlayerWithClubDTO>()

    @BeforeEach
    fun setUp() {
        club = createClub()
        competition = setupCompetitionFor(club.id)
        competitionCategory = addCompetitionCategoryTo(competition, "Flickor 12")
        setModeToCupOnlyFor(competitionCategory)

        for ((index, i) in listOf("A", "B", "C", "D", "F", "G", "H", "I").withIndex()) {
            val player = addPlayer("Player$i", club)
            setSingleRankOn(player, 100 - index * 10)
            registerPlayerTo(player, competitionCategory)
            players.add(player)
        }

        drawService.createDraw(competitionCategory.id)
    }

    private fun addCompetitionCategoryTo(competition: CompetitionDTO, categoryName: String): CompetitionCategoryDTO {
        val categories = categoryApi.getCategories()
        val category = categories.filter { it.name == categoryName }[0]
        return competitionCategoryApi.addCategoryToCompetition(
            competition.id,
            category.id
        )
    }

    private fun setModeToCupOnlyFor(competitionCategory: CompetitionCategoryDTO) {
        val categoryMetadata = categoryService.getCategoryMetadata(competitionCategory.id)
        val categoryMetadataSpec = CategoryMetadataSpec(
            cost = categoryMetadata.cost,
            drawType = DrawType.CUP_ONLY,
            nrPlayersPerGroup = categoryMetadata.nrPlayersPerGroup,
            nrPlayersToPlayoff = categoryMetadata.nrPlayersToPlayoff,
            poolDrawStrategy = categoryMetadata.poolDrawStrategy
        )

        categoryService.updateCategoryMetadata(
            competitionCategory.id,
            categoryMetadata.id,
            categoryMetadataSpec
        )
    }

    private fun registerPlayerTo(player: PlayerWithClubDTO, competitionCategory: CompetitionCategoryDTO) {
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                player.id,
                competitionCategory.id
            )
        )
    }

    private fun setSingleRankOn(player: PlayerWithClubDTO, value: Int) {
        playerRepository.addPlayerRanking(player.id, value, "SINGLES")
    }

    private fun createClub(): ClubSpec {
        return clubApi.addClub(
            NewClubSpec(
                "TestClub" + Random.nextLong().toString(),
                "Testroad 12B"
            )
        )
    }

    private fun addPlayer(firstName: String, club: ClubSpec): PlayerWithClubDTO {
        return playerApi.addPlayer(
            PlayerSpec(
                firstName,
                "Testsson",
                club.id!!,
                LocalDate.of(2000, 3, 3)
            )
        )
    }

    private fun setupCompetitionFor(clubId: Int): CompetitionDTO {
        return competitionApi.addCompetition(
            CompetitionSpec(
                "LocationA",
                "Monkey cup",
                "Welcome to competition A",
                clubId,
                LocalDate.now(),
                LocalDate.now()
            )
        )
    }

    @AfterEach
    fun removeMatchesAndRegistrations() {
        // Remove matches
        matchService.deleteMatchesInCategory(competition.id)

        // Remove registrations and delete category
        val registrationIds = registrationRepository.getRegistrationIdsInCategory(competition.id)
        for (id in registrationIds) {
            registrationService.unregister(id)
        }
        competitionCategoryService.deleteCategoryInCompetition(competition.id)

    }

    @Test
    fun thenBestAndWorstRankedPlayerShouldMeetInFirstMatch() {
        val matches = matchService.getMatchesInCategory(competitionCategory.id)

        val firstMatch = matches.filter { it.matchOrderNumber == 1 }[0]
        val bestPlayer = players[0]
        val worstPlayer = players[7]

        Assertions.assertEquals(
            bestPlayer.id, firstMatch.firstPlayer[0].id,
            "Expected to find the best ranked player in the first match"
        )
        Assertions.assertEquals(
            worstPlayer.id, firstMatch.secondPlayer[0].id,
            "Expected to find the worst ranked player in the first match."
        )
    }

    @Test
    fun thenSecondBestAndSecondWorstPlayerShouldMeetInSecondMatch() {
        val matches = matchService.getMatchesInCategory(competitionCategory.id)

        val secondMatch = matches.filter { it.matchOrderNumber == 4 }[0]
        val secondBestPlayer = players[1]
        val secondWorstPlayer = players[6]

        Assertions.assertEquals(
            secondWorstPlayer.id, secondMatch.firstPlayer[0].id,
            "Expected to find the second worst ranked player in the fourth match as top player"
        )
        Assertions.assertEquals(
            secondBestPlayer.id, secondMatch.secondPlayer[0].id,
            "Expected to find the second best ranked player in the second match as bottom player."
        )
    }
}