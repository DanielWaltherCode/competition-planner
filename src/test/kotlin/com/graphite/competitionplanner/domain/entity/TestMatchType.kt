package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.competitioncategory.entity.MatchType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestMatchType {

    @Test
    fun shouldThrowIllegalExceptionIfWrongType() {
        Assertions.assertThrows(IllegalArgumentException::class.java) { MatchType("POL") }
    }

    @Test
    fun poolIsOneType() {
        Assertions.assertDoesNotThrow { MatchType("POOL") }
    }

    @Test
    fun playoffIsOneType() {
        Assertions.assertDoesNotThrow { MatchType("PLAYOFF") }
    }
}