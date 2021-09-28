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
class TestGetRegistrationsInCompetitionCategory(
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
    fun shouldReturnEmptyListIfCompetitionCategoryDoesNotExist() {
        // Act
        val registrations = registrationRepository.getRegistrationsIn(-13)

        // Assert
        Assertions.assertTrue(registrations.isEmpty())
    }

    @Test
    fun shouldReturnTheCorrectRegistrations() {
        // Setup
        val registrationOne = setupSingleRegistration()
        val registrationTwo = setupSingleRegistration()

        // Act
        val registrations = registrationRepository.getRegistrationsIn(competitionCategory.id)

        // Assert
        val registrationIds = registrations.map { it.id }
        Assertions.assertTrue(registrations.size == 2, "Expected to get exactly two registrations")
        Assertions.assertTrue(registrationIds.contains(registrationOne.id))
        Assertions.assertTrue(registrationIds.contains(registrationTwo.id))
    }
}