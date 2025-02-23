package com.graphite.competitionplanner.category.domain

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCategory {

    @Test
    fun shouldThrowIllegalArgumentExceptionIfCategoryNameIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CategorySpec(0, "", CategoryType.SINGLES)
        }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionIfCategoryIsBlank() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            CategorySpec(0, "   ", CategoryType.SINGLES)
        }
    }
}