package com.graphite.competitionplanner.category.api

import com.graphite.competitionplanner.category.service.CategoryService
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCategoryApi {

    private final val service = mock(CategoryService::class.java)
    private final val api = CategoryApi(service)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldDelegateToService() {
        // Setup
        val expectedCategories =

            listOf(dataGenerator.newCategoryDTO(name = "Class1"), dataGenerator.newCategoryDTO(name = "class2"))
        `when`(service.getCategories()).thenReturn(expectedCategories)

        // Act
        val actual = api.getCategories()

        // Assert
        Assertions.assertEquals(expectedCategories, actual)
        verify(service, times(1)).getCategories()
    }
}