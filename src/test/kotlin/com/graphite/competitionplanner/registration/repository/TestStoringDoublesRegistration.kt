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
import java.time.LocalDate

@SpringBootTest
class TestStoringDoublesRegistration(
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
    fun shouldBeAbleToStoreSinglesRegistration() {
        // Setup
        val playerOne = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val playerTwo = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val spec = dataGenerator.newRegistrationDoublesSpecWithDate(
            date = LocalDate.now(),
            playerOneId = playerOne.id,
            playerTwoId = playerTwo.id,
            competitionCategoryId = competitionCategory.id
        )

        // Act
        val registration = registrationRepository.storeDoubles(spec)

        // Assertions
        Assertions.assertTrue(registration.id > 0)
        Assertions.assertEquals(spec.playerOneId, registration.playerOneId)
        Assertions.assertEquals(spec.playerTwoId, registration.playerTwoId)
        Assertions.assertEquals(spec.competitionCategoryId, registration.competitionCategoryId)
        Assertions.assertEquals(spec.date, registration.registrationDate)
    }

    @Test
    fun shouldReturnPlayerIdsForTheGivenCompetitionCategory() {
        // Setup
        val registrationOne = setupDoubleRegistration()
        val registrationTwo = setupDoubleRegistration()

        // Act
        val playerIds = registrationRepository.getAllPlayerIdsRegisteredTo(competitionCategory.id)

        // Assert
        Assertions.assertTrue(playerIds.contains(registrationOne.playerOneId))
        Assertions.assertTrue(playerIds.contains(registrationOne.playerTwoId))
        Assertions.assertTrue(playerIds.contains(registrationTwo.playerOneId))
        Assertions.assertTrue(playerIds.contains(registrationTwo.playerTwoId))
    }

    @Test
    fun shouldBeAbleToGetExistingRegistration() {
        // Setup
        val expected = setupDoubleRegistration()

        // Act
        val actual = registrationRepository.getRegistrationFor(
            dataGenerator.newRegistrationDoublesSpec(
                playerOneId = expected.playerOneId,
                playerTwoId = expected.playerTwoId,
                competitionCategoryId = expected.competitionCategoryId)
        )

        // Assert
        Assertions.assertEquals(expected, actual)
    }
}