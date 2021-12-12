package com.graphite.competitionplanner.competitioncategory.api

import com.graphite.competitionplanner.competitioncategory.domain.*
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.verify
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionCategoryApi {

    private val cancelCompetitionCategory = mock(CancelCompetitionCategory::class.java)
    private val findCompetitionCategory = mock(FindCompetitionCategory::class.java)
    private val getCompetitionCategories = mock(GetCompetitionCategories::class.java)
    private val updateCompetitionCategory = mock(UpdateCompetitionCategory::class.java)
    private val addCompetitionCategory = mock(AddCompetitionCategory::class.java)
    private val deleteCompetitionCategory = mock(DeleteCompetitionCategory::class.java)

    private val dataGenerator = DataGenerator()

    private val api = CompetitionCategoryApi(
        cancelCompetitionCategory,
        findCompetitionCategory,
        getCompetitionCategories,
        updateCompetitionCategory,
        addCompetitionCategory,
        deleteCompetitionCategory
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

    @Test
    fun getCompetitionCategories_shouldDelegateToDomain() {
        // Setup
        val competitionCategory1 =
            dataGenerator.newCompetitionCategoryDTO(category = dataGenerator.newCategorySpec(name = "Z"))
        val competitionCategory2 =
            dataGenerator.newCompetitionCategoryDTO(category = dataGenerator.newCategorySpec(name = "A"))
        val competition = dataGenerator.newCompetitionDTO()
        `when`(getCompetitionCategories.execute(competition.id)).thenReturn(
            listOf(
                competitionCategory1,
                competitionCategory2
            )
        )

        // Act
        val result = api.getCompetitionCategories(competition.id)

        // Assert
        Assertions.assertEquals(
            listOf(competitionCategory2, competitionCategory1),
            result,
            "Result might not be sorted by category name"
        )
        verify(getCompetitionCategories, times(1)).execute(competition.id)
        verify(getCompetitionCategories, times(1)).execute(anyInt())
    }

    @Test
    fun updateCompetitionCategory_shouldDelegateToDomain() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(id = 11223)
        val spec = dataGenerator.newCompetitionCategoryUpdateSpec()

        // Act
        api.updateCompetitionCategory(888, competitionCategory.id, spec)

        // Assert
        verify(updateCompetitionCategory, times(1)).execute(competitionCategory.id, spec)
        verify(updateCompetitionCategory, times(1)).execute(anyInt(), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun addCategoryToCompetition_shouldDelegateToDomain() {
        // Setup
        val competition = dataGenerator.newCompetitionDTO(id=12300)
        val spec = dataGenerator.newCategorySpec()
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO()
        `when`(addCompetitionCategory.execute(competition.id, spec)).thenReturn(competitionCategory)

        // Act
        val result = api.addCategoryToCompetition(competition.id, spec)

        // Assert
        Assertions.assertEquals(competitionCategory, result)
        verify(addCompetitionCategory, times(1)).execute(competition.id, spec)
        verify(addCompetitionCategory, times(1)).execute(eq(competition.id), TestHelper.MockitoHelper.anyObject())
    }

    @Test
    fun deleteCompetitionCategory_shouldDelegateToDomain() {
        // Setup
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(1231)

        // Act
        api.deleteCompetitionCategory(181, competitionCategory.id)

        verify(deleteCompetitionCategory, times(1)).execute(competitionCategory.id)
        verify(deleteCompetitionCategory, times(1)).execute(anyInt())
    }
}