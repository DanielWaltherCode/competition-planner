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
class TestGetRegistrationRankForSingles(
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
        // Override this so we set up competition category as a singles
        category = categoryRepository.getAvailableCategories().first { it.type == CategoryType.SINGLES.name }
        competitionCategory = competitionCategoryRepository.store(
            competitionId = competition.id,
            spec = dataGenerator.newCompetitionCategorySpec(
                category = dataGenerator.newCategorySpec(
                        id = category.id,
                        name = category.name,
                        type = CategoryType.valueOf(category.type))))
    }

    @Test
    fun shouldGetCorrectRanksForSingles() {
        // Setup
        val reg1 = setupSingleRegistration()
        val reg2 = setupSingleRegistration()

        playerRepository.addPlayerRanking(reg1.playerId, 39, CategoryType.SINGLES.name)
        playerRepository.addPlayerRanking(reg2.playerId, 98, CategoryType.SINGLES.name)

        // Act
        val registrationRanks = registrationRepository.getRegistrationRanking(competitionCategory)

        // Assert
        val actualReg1 = registrationRanks.first { it.registrationId.id == reg1.id }
        Assertions.assertEquals(39, actualReg1.rank)
        val actualReg2 = registrationRanks.first { it.registrationId.id == reg2.id }
        Assertions.assertEquals(98, actualReg2.rank)
    }
}