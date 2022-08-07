package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestGameSettingsDto {

    private val dataGenerator = DataGenerator()

    @Test
    fun numberOfSetHasToBeOdd() {
        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(numberOfSets = 2)
        }
    }

    @Test
    fun whenDifferentRulesInEndGameIsEnabledThenNumberOfSetsInFinalMustBeOdd() {
        // Act & Assert
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGameSettingsDTO(
                numberOfSets = 5,
                numberOfSetsFinal = 2,
                useDifferentRulesInEndGame = true
            )
        }
    }

    @Test
    fun whenDifferentRulesInEndGameIsDisabledThenNumberOfSetsInFinalDoesNotMatter() {
        // Act & Assert
        Assertions.assertDoesNotThrow {
            dataGenerator.newGameSettingsDTO(
                numberOfSets = 5,
                numberOfSetsFinal = 2,
                useDifferentRulesInEndGame = false
            )
        }
    }
}