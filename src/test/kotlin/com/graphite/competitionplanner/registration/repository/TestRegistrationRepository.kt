package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.club.repository.ClubRepository
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

@SpringBootTest
class TestRegistrationRepository(
    @Autowired val clubRepository: ClubRepository,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val competitionCategoryRepository: CompetitionCategoryRepository,
    @Autowired val registrationRepository: IRegistrationRepository
) {

    private val dataGenerator = DataGenerator()

    @Test
    fun shouldBeAbleToStoreRegistration() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val player = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val competition = competitionRepository.store(dataGenerator.newCompetitionSpec(organizingClubId = club.id))
        val category = categoryRepository.getAvailableCategories().first()
        val competitionCategory = competitionCategoryRepository.store(
            competitionId = competition.id,
            spec = dataGenerator.newCompetitionCategorySpec(category = dataGenerator.newCategorySpec(id = category.id)))

        val spec = dataGenerator.newRegistrationSinglesSpecWithDate(
            date = LocalDate.now(),
            playerId = player.id,
            competitionCategoryId = competitionCategory.id
        )

        // Act
        val registration = registrationRepository.store(spec)

        // Assertions
        Assertions.assertTrue(registration.id > 0)
        Assertions.assertEquals(spec.playerId, registration.playerId)
        Assertions.assertEquals(spec.competitionCategoryId, registration.competitionCategoryId)
        Assertions.assertEquals(spec.date, registration.registrationDate)
    }
}