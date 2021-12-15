package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestRemoveRegistration(
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
    fun shouldThrowNotFoundIfRegistrationIdCannotBeFound() {
        Assertions.assertThrows(NotFoundException::class.java) {
            registrationRepository.remove(-991)
        }
    }

    @Test
    fun shouldBeAbleToRemoveDoublesRegistration() {
        // Setup
        val registration = setupDoubleRegistration()

        // Act
        registrationRepository.remove(registration.id)

        // Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            registrationRepository.remove(registration.id)
        }
    }

    @Test
    fun shouldBeAbleToRemoveSinglesRegistration() {
        // Setup
        val registration = setupSingleRegistration()

        // Act
        registrationRepository.remove(registration.id)

        // Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            registrationRepository.remove(registration.id)
        }
    }
}