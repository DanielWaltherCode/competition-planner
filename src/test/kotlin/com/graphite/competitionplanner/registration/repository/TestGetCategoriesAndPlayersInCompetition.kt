package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.category.interfaces.CustomCategorySpec
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
class TestGetCategoriesAndPlayersInCompetition(
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

    override fun setupCompetitionCategory() {
        // Override this, so we set up competition category as a doubles
        category = categoryRepository.addCustomCategory(competition.id,
            CustomCategorySpec("CATEGORY-FOR-TestGetCategoriesAndPlayersInCompetition", CategoryType.SINGLES))
        competitionCategory = competitionCategoryRepository.store(
            competitionId = competition.id,
            spec = dataGenerator.newCompetitionCategorySpec(
                category = dataGenerator.newCategorySpec(
                        id = category.id,
                        name = category.name,
                        type = category.type)))
    }

    @Test
    fun getsAllCategoriesAndPlayers() {
        // Setup
        val reg1 = setupSingleRegistration()
        val reg2 = setupSingleRegistration()
        val reg3 = setupDoubleRegistration()

        // Act
        val categoriesAndPlayers = registrationRepository.getCategoriesAndPlayersInCompetition(competition.id)

        // Assert
        for ((actualCategory, _) in categoriesAndPlayers) {
            Assertions.assertEquals(category, actualCategory)
        }

        val actualPlayerIds = categoriesAndPlayers.map { (_, player) -> player.id }.sorted()
        val expectedPlayerIds = listOf(reg1.playerId, reg2.playerId, reg3.playerOneId, reg3.playerTwoId).sorted()
        Assertions.assertEquals(
            expectedPlayerIds,
            actualPlayerIds,
            "There were some missing player IDs of registered players."
        )
    }
}