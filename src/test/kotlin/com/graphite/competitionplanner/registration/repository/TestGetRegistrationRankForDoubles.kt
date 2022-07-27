package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.interfaces.CategoryType
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
class TestGetRegistrationRankForDoubles(
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
        // Override this so we set up competition category as a doubles
        category = categoryRepository.getAvailableCategories().first { it.type == CategoryType.DOUBLES.name }
        competitionCategory = competitionCategoryRepository.store(
            competitionId = competition.id,
            spec = dataGenerator.newCompetitionCategorySpec(
                category = dataGenerator.newCategorySpec(
                        id = category.id,
                        name = category.name,
                        type = CategoryType.valueOf(category.type))))
    }

    @Test
    fun shouldGetCorrectRanksForDoubles() {
        // Setup
        val reg1 = setupDoubleRegistration()
        val reg2 = setupDoubleRegistration()

        playerRepository.addPlayerRanking(reg1.playerOneId, 31, CategoryType.DOUBLES.name)
        playerRepository.addPlayerRanking(reg1.playerTwoId, 22, CategoryType.DOUBLES.name)

        playerRepository.addPlayerRanking(reg2.playerOneId, 15, CategoryType.DOUBLES.name)
        playerRepository.addPlayerRanking(reg2.playerTwoId, 12, CategoryType.DOUBLES.name)

        // Act
        val registrationRanks = registrationRepository.getRegistrationRanking(competitionCategory)

        // Assert
        Assertions.assertTrue(registrationRanks.size == 2, "We only registered two doubles")
        val actualReg1 = registrationRanks.first { it.registrationId == reg1.id }
        Assertions.assertEquals(31 + 22, actualReg1.rank)
        val actualReg2 = registrationRanks.first { it.registrationId == reg2.id }
        Assertions.assertEquals(15 + 12, actualReg2.rank)
    }
}