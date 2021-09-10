package com.graphite.competitionplanner.category

import com.graphite.competitionplanner.api.competition.CategoryGameRulesSpec
import com.graphite.competitionplanner.service.CategoryGameRulesDTO
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCategoryGameRules(
    @Autowired val testUtil: TestUtil,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val categoryService: CategoryService
) {

    var categoryId: Int = 0
    lateinit var categoryGameRulesDTO: CategoryGameRulesDTO;

    @BeforeEach
    fun setUpCompetitionCategory() {
        categoryId = testUtil.addCompetitionCategory("Pojkar 9")
        categoryGameRulesDTO = categoryService.getCategoryGameRules(categoryId)
    }

    @AfterEach
    fun cleanUpAddedCategory() {
        competitionCategoryService.deleteCategoryInCompetition(categoryId)
    }

    @Test
    fun testAddCategoryGameRules() {
        Assertions.assertNotNull(categoryGameRulesDTO)
        Assertions.assertNotNull(categoryGameRulesDTO.id)
    }

    @Test
    fun updateCategoryGameRules() {
        val newNrSetsFinal = 8
        val newWinMarginFinal = 1
        val newCategoryGameRulesSpec = CategoryGameRulesSpec(
            categoryGameRulesDTO.nrSets,
            categoryGameRulesDTO.winScore,
            categoryGameRulesDTO.winMargin,
            categoryGameRulesDTO.differentNumberOfGamesFromRound,
            newNrSetsFinal,
            categoryGameRulesDTO.winScoreFinal,
            newWinMarginFinal,
            categoryGameRulesDTO.tiebreakInFinalGame,
            null, null
        )
        val updatedCategory = categoryService.updateCategoryGameRules(
            categoryGameRulesDTO.id,
            categoryId, newCategoryGameRulesSpec
        )

        Assertions.assertNotNull(updatedCategory)
        Assertions.assertEquals(updatedCategory.id, categoryGameRulesDTO.id)
        Assertions.assertEquals(updatedCategory.nrSetsFinal, newNrSetsFinal)
        Assertions.assertEquals(updatedCategory.winMarginFinal, newWinMarginFinal)
    }

    @Test
    fun getCategoryGameRules() {
        val retrievedCategory = categoryService.getCategoryGameRules(
            categoryGameRulesDTO.competitionCategoryId
        )

        Assertions.assertNotNull(retrievedCategory)
        Assertions.assertEquals(categoryGameRulesDTO.id, retrievedCategory.id)
    }
}