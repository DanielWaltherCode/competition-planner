package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException

@SpringBootTest
class TestCompetitionCategoryGeneralSettings {

    val dataGenerator = DataGenerator()

    @Test
    fun costMustBeGreaterOrEqualToZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            GeneralSettings(dataGenerator.newGeneralSettingsDTO(cost = -1f))
        }

        Assertions.assertDoesNotThrow {
            GeneralSettings(dataGenerator.newGeneralSettingsDTO(cost = 0f))
        }
    }

    @Test
    fun playerPerGroupMustBeGreaterThanOne() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            GeneralSettings(dataGenerator.newGeneralSettingsDTO(playersPerGroup = 1))
        }
    }

    @Test
    fun playersToPlayOffMustBeGreaterThanZero() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            GeneralSettings(dataGenerator.newGeneralSettingsDTO(playersToPlayOff = 0))
        }
    }

    @Test
    fun playersPerGroupMustBeGreaterOrEqualToPlayersThatGoesToPlayOff() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            GeneralSettings(dataGenerator.newGeneralSettingsDTO(playersPerGroup = 2, playersToPlayOff = 3))
        }

        Assertions.assertDoesNotThrow {
            GeneralSettings(dataGenerator.newGeneralSettingsDTO(playersPerGroup = 2, playersToPlayOff = 2))
        }
    }

    @Test
    fun shouldNotThrowException() {
        Assertions.assertDoesNotThrow {
            GeneralSettings(dataGenerator.newGeneralSettingsDTO())
        }
    }
}