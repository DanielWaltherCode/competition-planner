package com.graphite.competitionplanner.domain.entity

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestCategory {

    @Test
    fun shouldThrowIllegalArgumentExceptionIfCategoryNameIsEmpty() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Category(0, "") }
    }

    @Test
    fun shouldThrowIllegalArgumentExceptionIfCategoryIsBlank() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { Category(0, "   ") }
    }
}