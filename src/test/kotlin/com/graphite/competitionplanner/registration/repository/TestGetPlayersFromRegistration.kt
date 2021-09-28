package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGetPlayersFromRegistration(
    @Autowired clubRepository: ClubRepository,
    @Autowired playerRepository: PlayerRepository,
    @Autowired competitionRepository: CompetitionRepository,
    @Autowired categoryRepository: CategoryRepository,
    @Autowired competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired registrationRepository: IRegistrationRepository
) : BaseTestRegistration(
    clubRepository,
    playerRepository,
    competitionRepository,
    categoryRepository,
    competitionCategoryRepository,
    registrationRepository
) {

    @Test
    fun shouldBeAbleToGetPlayersFromDoublesRegistration() {
        // Setup
        val registration = setupDoubleRegistration()

        // Act
        val players = registrationRepository.getPlayersFrom(registration.id)

        // Assert
        Assertions.assertTrue(players.size == 2, "Expected to find exactly two players. Found ${players.size} players")
        val playerIds = players.map { it.id }
        Assertions.assertTrue(playerIds.contains(registration.playerOneId))
        Assertions.assertTrue(playerIds.contains(registration.playerTwoId))
    }

    @Test
    fun shouldBeAbleToGetPlayersFromSinglesRegistration() {
        // Setup
        val registration = setupSingleRegistration()

        // Act
        val players = registrationRepository.getPlayersFrom(registration.id)

        // Assert
        Assertions.assertTrue(players.size == 1, "Expected to find only one player. Found ${players.size} players")
        Assertions.assertEquals(registration.playerId, players.first().id)
    }

    @Test
    fun shouldReturnEmptyListIfRegistrationIdDoesNotExist() {
        // Act
        val players = registrationRepository.getPlayersFrom(-3)

        // Assert
        Assertions.assertTrue(players.isEmpty(), "Expected to not get any players")
    }
}

