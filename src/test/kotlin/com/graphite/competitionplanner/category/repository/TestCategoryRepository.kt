package com.graphite.competitionplanner.category.repository

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCategoryRepository(
    @Autowired val repository: ICategoryRepository
) {

    @Test
    fun shouldGetAllAvailableCategories() {
        val availableCategories = repository.getAvailableCategories()

        DefaultCategory.values().forEach { defaultCategory ->
            Assertions.assertNotNull(availableCategories.find { it.name == defaultCategory.name })
        }
    }
}