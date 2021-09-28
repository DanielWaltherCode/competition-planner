package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateSeed(
    @Autowired val createSeed: CreateSeed
) {

    val dataGenerator = DataGenerator()

    @Test
    fun shouldReturnEmptyListIfThereAreNoRegistrations() {
        // Act
        val seededRegistrations = createSeed.execute(emptyList())

        // Assert
        Assertions.assertTrue(seededRegistrations.isEmpty())
    }

    @Test
    fun highestRankedRegistrationGetsSeedOne() {
        // Setup
        val competitionCategoryId = 101
        val lowestRank = dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategoryId, rank = 10)
        val middleRank = dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategoryId, rank = 15)
        val highestRank = dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategoryId, rank = 20)
        val registrationRanks = listOf(lowestRank, highestRank, middleRank)

        // Act
        val seededRegistrations = createSeed.execute(registrationRanks)

        // Assert
        val firstSeed = seededRegistrations.first { it.seed == 1 }
        Assertions.assertEquals(highestRank.id, firstSeed.id)
    }

}