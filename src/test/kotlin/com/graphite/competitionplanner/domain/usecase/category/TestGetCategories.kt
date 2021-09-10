package com.graphite.competitionplanner.domain.usecase.category

import com.graphite.competitionplanner.DataGenerator
import com.graphite.competitionplanner.category.domain.GetCategories
import com.graphite.competitionplanner.category.domain.interfaces.ICategoryRepository
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
            dataGenerator.newCategoryDTO(id = 0, name = "DAMER 1"),
            dataGenerator.newCategoryDTO(id = 1, name = "HERRAR 1"),
            dataGenerator.newCategoryDTO(id = 2, name = "HERRAR 13")
        )
        `when`(repository.getAvailableCategories()).thenReturn(expected)

        // Act
        val categories = getCategories.execute()

        // Assert
        verify(repository, times(1)).getAvailableCategories()
        Assertions.assertEquals(expected, categories)
    }

}