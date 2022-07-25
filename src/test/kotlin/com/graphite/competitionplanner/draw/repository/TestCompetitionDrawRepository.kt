package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.Pool
import com.graphite.competitionplanner.draw.interfaces.GroupDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionDrawRepository(
    @Autowired val repository: ICompetitionDrawRepository,
    @Autowired val clubRepository: IClubRepository,
    @Autowired val competitionRepository: ICompetitionRepository,
    @Autowired val competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired val categoryRepository: ICategoryRepository,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationRepository: IRegistrationRepository
) {

    protected val dataGenerator = DataGenerator()

    fun CompetitionCategoryDTO.registerPlayers(players: List<PlayerDTO>): List<RegistrationSinglesDTO> {
        return players.map {
            dataGenerator.newRegistrationSinglesSpecWithDate(playerId = it.id, competitionCategoryId = this.id)
        }.map { registrationRepository.storeSingles(it) }
    }

    fun CompetitionCategoryDTO.registerForDoubles(players: List<PlayerDTO>): RegistrationDoublesDTO {
        val doubleRegistration = dataGenerator.newRegistrationDoublesSpecWithDate(
            playerOneId = players.first().id,
            playerTwoId = players.last().id,
            competitionCategoryId = this.id)
        return registrationRepository.storeDoubles(doubleRegistration)
    }

    fun CompetitionDTO.createCategory(): CompetitionCategoryDTO {
        val category = categoryRepository.getAvailableCategories(0).first()
        return competitionCategoryRepository.store(
            this.id, dataGenerator.newCompetitionCategorySpec(
                category = dataGenerator.newCategorySpec(category.id, category.name, CategoryType.valueOf(category.type)),
                settings = dataGenerator.newGeneralSettingsDTO(drawType = DrawType.CUP_ONLY)
            )
        )
    }

    fun ClubDTO.addCompetition(): CompetitionDTO {
        val competitionSpec = dataGenerator.newCompetitionSpec(organizingClubId = this.id)
        return competitionRepository.store(competitionSpec)
    }

    fun ClubDTO.addPlayers(count: Int): List<PlayerDTO> {
        return (1..count).map { playerRepository.store(dataGenerator.newPlayerSpec(clubId = this.id)) }
    }

    object AssertionHelper {
        fun assertDoubleRegistrationPlayOffMatch(
            match: MatchAndResultDTO,
            registrationOne: RegistrationDoublesDTO,
            registrationTwo: RegistrationDoublesDTO
        ) {
            Assertions.assertEquals(2, match.firstPlayer.size, "Expected to find 2 players in first team")
            Assertions.assertEquals(2, match.secondPlayer.size, "Expected to find 2 players in second team")

            val player1Ids = match.firstPlayer.map { it.id }
            Assertions.assertTrue(player1Ids.contains(registrationOne.playerOneId),
                "Expected to find player with id ${registrationOne.playerOneId} in $player1Ids")
            Assertions.assertTrue(player1Ids.contains(registrationOne.playerTwoId),
                "Expected to find player with id ${registrationOne.playerTwoId} in $player1Ids")

            val player2Ids = match.secondPlayer.map { it.id }
            Assertions.assertTrue(player2Ids.contains(registrationTwo.playerOneId),
                "Expected to find player with id ${registrationTwo.playerOneId} in $player2Ids")
            Assertions.assertTrue(player2Ids.contains(registrationTwo.playerTwoId),
                "Expected to find player with id ${registrationTwo.playerTwoId} in $player2Ids")
        }

        fun assertGroupDrawDto(expectedPool: Pool, club: ClubDTO, expectedPlayers: List<PlayerDTO>, groupDrawDTO: GroupDrawDTO) {
            Assertions.assertEquals(expectedPool.name, groupDrawDTO.name)

            // Validate matches
            Assertions.assertEquals(expectedPool.matches.size, groupDrawDTO.matches.size)
            Assertions.assertTrue(groupDrawDTO.matches.all { it.id > 0 }, "At least one match did not have an id larger than 0.")

            // Check that players are equal. Construct comparable players
            val comparablePlayers = expectedPlayers.map { PlayerWithClubDTO(it.id, it.firstName, it.lastName, club, it.dateOfBirth) }
            val actualPlayers: MutableList<PlayerWithClubDTO> = mutableListOf()
            for (playerList in groupDrawDTO.players) {
               for (player in playerList.playerDTOs) {
                    actualPlayers.add(player)
                }
            }
            Assertions.assertEquals(
                comparablePlayers.sortedBy { it.id },
                actualPlayers.sortedBy { it.id },
                "Different players in spec and returned draw."
            )
        }
    }
}