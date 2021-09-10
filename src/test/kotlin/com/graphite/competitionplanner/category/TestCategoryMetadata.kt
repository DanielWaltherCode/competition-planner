package com.graphite.competitionplanner.category

import com.graphite.competitionplanner.competitioncategory.api.CategoryMetadataSpec
import com.graphite.competitionplanner.domain.entity.DrawType
import com.graphite.competitionplanner.domain.entity.PoolDrawStrategy
import com.graphite.competitionplanner.category.service.CategoryMetadataDTO
import com.graphite.competitionplanner.category.service.CategoryService
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.util.TestUtil
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCategoryMetadata(
    @Autowired val categoryService: CategoryService,
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
            PoolDrawStrategy.NORMAL
        )

        val updatedCategory = categoryService.updateCategoryMetadata(
            competitionCategoryId,
            categoryMetadataDTO.id, categoryWithNewValues
        )

        Assertions.assertEquals(categoryMetadataDTO.id, updatedCategory.id)
        Assertions.assertEquals(updatedCategory.nrPlayersPerGroup, playersPerGroup)
    }
}