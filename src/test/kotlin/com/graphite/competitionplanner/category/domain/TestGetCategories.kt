package com.graphite.competitionplanner.category.domain

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGetCategories {

    private final val repository = mock(ICategoryRepository::class.java)
    val getCategories = GetCategories(repository)
    val dataGenerator = DataGenerator()

    @Test
    fun shouldFetchFromRepository() {
        // Setup
        val expected = listOf(
            dataGenerator.newCategoryDTO(id = 0, name = DefaultCategory.WOMEN_1.name),
            dataGenerator.newCategoryDTO(id = 1, name = DefaultCategory.MEN_1.name),
            dataGenerator.newCategoryDTO(id = 2, name = DefaultCategory.MEN_3.name)
        )
        `when`(repository.getAvailableCategories(anyInt())).thenReturn(expected)

        // Act
        val categories = getCategories.execute(anyInt())

        // Assert
        verify(repository, times(1)).getAvailableCategories(0)
        Assertions.assertEquals(expected, categories)
    }

}