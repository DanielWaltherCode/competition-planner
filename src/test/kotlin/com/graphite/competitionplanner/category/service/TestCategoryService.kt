package com.graphite.competitionplanner.category.service

import com.graphite.competitionplanner.category.domain.GetCategories
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCategoryService {

    private final val getCategories = mock(GetCategories::class.java)
    private final val service = CategoryService(getCategories)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldDelegateToUseCase() {
        // Setup
        val expectedCategories =
            listOf(dataGenerator.newCategoryDTO(name = "Class1"), dataGenerator.newCategoryDTO(name = "class2"))
        `when`(getCategories.execute()).thenReturn(expectedCategories)

        // Act
        val actual = service.getCategories()

        // Assert
        Assertions.assertEquals(expectedCategories, actual)
        verify(getCategories, times(1)).execute()
    }
}