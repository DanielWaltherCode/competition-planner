package com.graphite.competitionplanner.category

import com.graphite.competitionplanner.api.competition.CategoryMetadataApi
import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.service.CategoryGameRulesDTO
import com.graphite.competitionplanner.service.CategoryMetadataDTO
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.draw.DrawStrategy
import com.graphite.competitionplanner.service.draw.DrawType
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class TestCategoryMetadata(
    @Autowired val categoryService: CategoryService,
    @Autowired val categoryMetadataApi: CategoryMetadataApi,
    @Autowired val testUtil: TestUtil,
    @Autowired val competitionCategoryService: CompetitionCategoryService
) {

    var competitionCategoryId: Int = 0
    lateinit var categoryMetadataDTO: CategoryMetadataDTO

    @BeforeEach
    fun setUpCompetitionCategory() {
        competitionCategoryId = testUtil.addCompetitionCategory("Flickor 8")
        categoryMetadataDTO = categoryService.getCategoryMetadata(competitionCategoryId)
    }

    @AfterEach
    fun cleanUpAddedCategory() {
        competitionCategoryService.deleteCategoryInCompetition(competitionCategoryId)
    }

    @Test
    fun getCategoryMetadata() {
        val fetchedCategory = categoryService.getCategoryMetadata(competitionCategoryId )
        Assertions.assertNotNull(fetchedCategory)
    }

    @Test
    fun updateCategoryMetadata() {
        val playersPerGroup = 5
        val categoryWithNewValues = CategoryMetadataSpec(
            15.55f,
            DrawType.POOL_ONLY,
            playersPerGroup,
            2,
            DrawStrategy.NORMAL
        )

        val updatedCategory = categoryService.updateCategoryMetadata(
            competitionCategoryId,
            categoryMetadataDTO.id, categoryWithNewValues
        )

        Assertions.assertEquals(categoryMetadataDTO.id, updatedCategory.id)
        Assertions.assertEquals(updatedCategory.nrPlayersPerGroup, playersPerGroup)
    }

    @Test
    fun testgetPossibleValues() {
        val result = categoryMetadataApi.getPossibleCategoryMetadataValues()
        Assertions.assertTrue(result.drawStrategies.isNotEmpty())
        Assertions.assertTrue(result.drawTypes.isNotEmpty())
    }
}