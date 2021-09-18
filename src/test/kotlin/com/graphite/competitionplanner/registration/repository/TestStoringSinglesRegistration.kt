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
class TestStoringSinglesRegistration(
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
        val player = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val spec = dataGenerator.newRegistrationSinglesSpecWithDate(
            date = LocalDate.now(),
            playerId = player.id,
            competitionCategoryId = competitionCategory.id
        )

        // Act
        val registration = registrationRepository.storeSingles(spec)

        // Assertions
        Assertions.assertTrue(registration.id > 0)
        Assertions.assertEquals(spec.playerId, registration.playerId)
        Assertions.assertEquals(spec.competitionCategoryId, registration.competitionCategoryId)
        Assertions.assertEquals(spec.date, registration.registrationDate)
    }

    @Test
    fun shouldReturnPlayerIdsForTheGivenCompetitionCategory() {
        // Setup
        val registrationOne = setupSingleRegistration()
        val registrationTwo = setupSingleRegistration()

        // Act
        val playerIds = registrationRepository.getAllPlayerIdsRegisteredTo(competitionCategory.id)

        // Assert
        Assertions.assertTrue(playerIds.contains(registrationOne.playerId))
        Assertions.assertTrue(playerIds.contains(registrationTwo.playerId))
    }

    @Test
    fun shouldBeAbleToGetExistingRegistration() {
        // Setup
        val expected = setupSingleRegistration()

        // Act
        val actual = registrationRepository.getRegistrationFor(
            dataGenerator.newRegistrationSinglesSpec(
                playerId = expected.playerId,
                competitionCategoryId = expected.competitionCategoryId)
        )

        // Assert
        Assertions.assertEquals(expected, actual)
    }
}