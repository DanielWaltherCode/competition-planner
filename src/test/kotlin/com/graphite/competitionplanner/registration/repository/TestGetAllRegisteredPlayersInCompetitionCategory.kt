package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGetAllRegisteredPlayersInCompetitionCategory(
    @Autowired clubRepository: ClubRepository,
    @Autowired playerRepository: PlayerRepository,
    @Autowired competitionRepository: CompetitionRepository,
    @Autowired categoryRepository: CategoryRepository,
    @Autowired competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired registrationRepository: IRegistrationRepository
) : BaseTestRegistration(
    clubRepository,
    playerRepository,
    competitionRepository,
    categoryRepository,
    competitionCategoryRepository,
    registrationRepository
) {

    @Test
    fun getAllPlayersWithTheirCorrectClubs(){
        // Setup
        val reg1 = setupSingleRegistration()
        val reg2 = setupSingleRegistration()
        val reg3 = setupDoubleRegistration()

        // Act
        val result = registrationRepository.getAllRegisteredPlayersInCompetitionCategory(competitionCategory.id)

        // Assert
        val players = result.map { it.second }
        val actualPlayerIds = players.map { it.id }.sorted()
        val expectedPlayerIds = listOf(reg1.playerId, reg2.playerId, reg3.playerOneId, reg3.playerTwoId).sorted()
        Assertions.assertEquals(
            expectedPlayerIds,
            actualPlayerIds,
            "There were some missing player IDs of registered players."
        )

        for (p in players) {
            Assertions.assertEquals(p.club, club, "Player $p not belonging to the expected club $club")
        }
    }
}