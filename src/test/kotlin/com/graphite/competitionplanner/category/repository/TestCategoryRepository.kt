package com.graphite.competitionplanner.category.repository

import com.graphite.competitionplanner.category.domain.interfaces.ICategoryRepository
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

        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 1" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 2" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 3" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 4" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 5" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrar 6" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 1" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 2" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 3" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damer 4" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damjuniorer 17" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 15" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 14" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 13" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 12" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 11" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 10" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 9" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Flickor 8" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrjuniorer 17" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 15" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 14" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 13" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 12" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 11" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 10" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 9" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Pojkar 8" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Herrdubbel" })
        Assertions.assertNotNull(availableCategories.find { it.name == "Damdubbel" })
    }
}