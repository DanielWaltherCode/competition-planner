package com.graphite.competitionplanner.draw.service

import com.graphite.competitionplanner.category.api.CategoryApi
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.service.ClubService
import com.graphite.competitionplanner.competition.api.CompetitionApi
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.domain.interfaces.DrawTypeDTO
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.domain.entity.DrawType
import com.graphite.competitionplanner.match.service.MatchService
import com.graphite.competitionplanner.player.api.PlayerApi
import com.graphite.competitionplanner.player.api.PlayerSpec
import com.graphite.competitionplanner.player.domain.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.api.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.registration.service.RegistrationService
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestDrawCupOnlyMatchOrder(
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationService: RegistrationService,
    @Autowired val matchService: MatchService,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val registrationRepository: RegistrationRepository,
    @Autowired val drawService: DrawService,
    @Autowired val competitionApi: CompetitionApi,
    @Autowired val playerApi: PlayerApi,
    @Autowired val categoryApi: CategoryApi,
    @Autowired val clubService: ClubService
) {
    lateinit var club: ClubDTO
    lateinit var competition: CompetitionDTO
    lateinit var competitionCategory: CompetitionCategoryDTO
    var players = mutableListOf<PlayerWithClubDTO>()
    val dataGenerator = DataGenerator()

    @BeforeEach
    fun setUp() {
        club = clubService.addClub(dataGenerator.newClubSpec())
        competition = setupCompetitionFor(club.id)
        competitionCategory = addCompetitionCategoryTo(competition, "Flickor 12")
        setModeToCupOnlyFor(competitionCategory.id)

        for ((index, i) in listOf("A", "B", "C", "D", "F", "G", "H", "I").withIndex()) {
            val player = addPlayer("Player$i", club)
            setSingleRankOn(player, 100 - index * 10)
            registerPlayerTo(player, competitionCategory.id)
            players.add(player)
        }

        drawService.createDraw(competitionCategory.id)
    }

    private fun addCompetitionCategoryTo(competition: CompetitionDTO, categoryName: String):
            CompetitionCategoryDTO {
        val categories = categoryApi.getCategories()
        val category = categories.filter { it.name == categoryName }[0]
        return competitionCategoryService.addCompetitionCategory(
            competition.id,
            CategoryDTO(category.id, category.name, category.type)
        )
    }

    private fun setModeToCupOnlyFor(competitionCategoryId: Int) {
        val original = competitionCategoryService.getByCompetitionCategoryId(competitionCategoryId)

        val updatedSettings = dataGenerator.newCompetitionCategoryUpdateDTO(
            original.id,
            settings = dataGenerator.newGeneralSettingsDTO(
                cost = original.settings.cost,
                drawType = DrawTypeDTO(DrawType.CUP_ONLY.name),
                playersPerGroup = original.settings.playersPerGroup,
                playersToPlayOff = original.settings.playersToPlayOff,
                poolDrawStrategy = original.settings.poolDrawStrategy
            ),
            gameSettings = original.gameSettings
        )

        competitionCategoryService.updateCompetitionCategory(updatedSettings)
    }

    private fun registerPlayerTo(player: PlayerWithClubDTO, competitionCategoryId: Int) {
        registrationService.registerPlayerSingles(
            RegistrationSinglesSpec(
                player.id,
                competitionCategoryId
            )
        )
    }

    private fun setSingleRankOn(player: PlayerWithClubDTO, value: Int) {
        playerRepository.addPlayerRanking(player.id, value, "SINGLES")
    }

    private fun addPlayer(firstName: String, club: ClubDTO): PlayerWithClubDTO {
        return playerApi.addPlayer(
            PlayerSpec(
                firstName,
                "Testsson",
                club.id,
                LocalDate.of(2000, 3, 3)
            )
        )
    }

    private fun setupCompetitionFor(clubId: Int): CompetitionDTO {
        return competitionApi.addCompetition(
            CompetitionSpec(
                LocationSpec("LocationA"),
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