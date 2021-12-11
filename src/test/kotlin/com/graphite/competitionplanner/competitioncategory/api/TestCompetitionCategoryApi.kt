package com.graphite.competitionplanner.competitioncategory.api

import com.graphite.competitionplanner.competitioncategory.domain.CancelCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionCategoryApi(
    @Autowired competitionCategoryService: CompetitionCategoryService
) {

    private val cancelCompetitionCategory = mock(CancelCompetitionCategory::class.java)
    private val findCompetitionCategory = mock(FindCompetitionCategory::class.java)

    private val dataGenerator = DataGenerator()

    private val api = CompetitionCategoryApi(
        competitionCategoryService,
        cancelCompetitionCategory,
        findCompetitionCategory,
    )

    @Test
    fun cancelCompetitionCategory_shouldDelegateToDomain() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        `when`(findCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)

        // Act
        api.cancelCompetitionCategory(1, competitionCategory.id)

        // Assert
        verify(cancelCompetitionCategory, times(1)).execute(competitionCategory)
        verify(cancelCompetitionCategory, times(1)).execute(TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun getCategoryGeneralSettings_shouldDelegateToDomain() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        `when`(findCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)

        // Act
        val result = api.getCategoryGeneralSettings(competitionCategory.id)

        // Assert
        Assertions.assertEquals(competitionCategory.settings, result)
        verify(findCompetitionCategory, times(1)).byId(competitionCategory.id)
        verify(findCompetitionCategory, times(1)).byId(anyInt())
    }

    @Test
    fun getCategoryGameSettings_shouldDelegateToDomain() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        `when`(findCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)

        // Act
        val result = api.getCategoryGameSettings(competitionCategory.id)

        // Assert
        Assertions.assertEquals(competitionCategory.gameSettings, result)
        verify(findCompetitionCategory, times(1)).byId(competitionCategory.id)
        verify(findCompetitionCategory, times(1)).byId(anyInt())
    }
}