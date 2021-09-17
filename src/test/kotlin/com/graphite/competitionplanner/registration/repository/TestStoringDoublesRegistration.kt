package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestStoringDoublesRegistration(
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
}