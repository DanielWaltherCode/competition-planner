package com.graphite.competitionplanner.competitioncategory.domain

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionCategoryGeneralSettings {

    val dataGenerator = DataGenerator()

    @Test
    fun costMustBeGreaterOrEqualToZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGeneralSettingsSpec(cost = -1f)
        }

        Assertions.assertDoesNotThrow {
            dataGenerator.newGeneralSettingsSpec(cost = 0f)
        }
    }

    @Test
    fun playerPerGroupMustBeGreaterThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGeneralSettingsSpec(playersPerGroup = 1)
        }
    }

    @Test
    fun playersToPlayOffMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGeneralSettingsSpec(playersToPlayOff = 0)
        }
    }

    @Test
    fun playersPerGroupMustBeGreaterOrEqualToPlayersThatGoesToPlayOff() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            dataGenerator.newGeneralSettingsSpec(playersPerGroup = 2, playersToPlayOff = 3)
        }

        Assertions.assertDoesNotThrow {
            dataGenerator.newGeneralSettingsSpec(playersPerGroup = 2, playersToPlayOff = 2)
        }
    }

    @Test
    fun shouldNotThrowException() {
        Assertions.assertDoesNotThrow {
            dataGenerator.newGeneralSettingsSpec()
        }
    }
}