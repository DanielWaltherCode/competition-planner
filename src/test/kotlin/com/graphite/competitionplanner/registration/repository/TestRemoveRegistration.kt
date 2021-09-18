package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestRemoveRegistration(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired val registrationRepository: IRegistrationRepository
) {

    private val dataGenerator = DataGenerator()
    private lateinit var club: ClubDTO
    private lateinit var competition: CompetitionDTO
    private lateinit var competitionCategory: CompetitionCategoryDTO

    @BeforeEach
    fun setup() {
        club = clubRepository.store(dataGenerator.newClubSpec())
        competition = competitionRepository.store(dataGenerator.newCompetitionSpec(organizingClubId = club.id))
        val category = categoryRepository.getAvailableCategories().first { it.type == CategoryType.DOUBLES.name }
        competitionCategory = competitionCategoryRepository.store(
            competitionId = competition.id,
            spec = dataGenerator.newCompetitionCategorySpec(category = dataGenerator.newCategorySpec(id = category.id)))
    }

    @Test
    fun shouldThrowNotFoundIfRegistrationIdCannotBeFound() {
        Assertions.assertThrows(NotFoundException::class.java) {
            registrationRepository.remove(-1)
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
        val registration = setupRegisterPlayer()

        // Act
        registrationRepository.remove(registration.id)

        // Assert
        Assertions.assertThrows(NotFoundException::class.java) {
            registrationRepository.remove(registration.id)
        }
    }

    private fun setupDoubleRegistration(): RegistrationDoublesDTO {
        val playerOne = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val playerTwo = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val spec = dataGenerator.newRegistrationDoublesSpecWithDate(
            date = LocalDate.now(),
            playerOneId = playerOne.id,
            playerTwoId = playerTwo.id,
            competitionCategoryId = competitionCategory.id
        )
        return registrationRepository.storeDoubles(spec)
    }

    private fun setupRegisterPlayer(): RegistrationSinglesDTO {
        val player = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val spec = dataGenerator.newRegistrationSinglesSpecWithDate(
            date = LocalDate.now(),
            playerId = player.id,
            competitionCategoryId = competitionCategory.id
        )
        return registrationRepository.storeSingles(spec)
    }
}