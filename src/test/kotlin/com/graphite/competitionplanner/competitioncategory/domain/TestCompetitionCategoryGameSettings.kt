package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionCategoryGameSettings {

    val dataGenerator = DataGenerator()

    @Test
    fun numberOfSetsMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(numberOfSets = 0)
        }
    }

    @Test
    fun winScoreMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(winScore = 0)
        }
    }

    @Test
    fun winMarginMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(winMargin = 0)
        }
    }

    @Test
    fun numberOfSetsInFinalMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(numberOfSetsFinal = 0)
        }
    }

    @Test
    fun winScoreFinalMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(winScoreFinal = 0)
        }
    }

    @Test
    fun winMarginInFinalMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(winMarginFinal = 0)
        }
    }

    @Test
    fun winScoreTieBreakMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(winScoreTiebreak = 0)
        }
    }

    @Test
    fun winMarginInTieBreakMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(winMarginTieBreak = 0)
        }
    }

    @Test
    fun shouldNotThrowException() {
        Assertions.assertDoesNotThrow {
            dataGenerator.newGameSettingsDTO()
        }
    }

}