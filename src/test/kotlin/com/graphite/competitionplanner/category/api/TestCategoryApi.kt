package com.graphite.competitionplanner.category.api

import com.graphite.competitionplanner.category.domain.GetCategories
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCategoryApi {

    private val mockedGetCategories = mock(GetCategories::class.java)
    private val api = CategoryApi(mockedGetCategories)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldDelegateToService() {
        // Setup
        val expectedCategories =
            listOf(dataGenerator.newCategoryDTO(name = "Class1"), dataGenerator.newCategoryDTO(name = "class2"))
        `when`(mockedGetCategories.execute()).thenReturn(expectedCategories)

        // Act
        val actual = api.getCategories()

        // Assert
        Assertions.assertEquals(expectedCategories, actual)
        verify(mockedGetCategories, times(1)).execute()
    }
}