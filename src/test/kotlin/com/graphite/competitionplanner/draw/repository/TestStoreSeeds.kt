package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CupDrawSpec
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TestStoreSeeds(
    @Autowired repository: ICompetitionDrawRepository,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: PlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository
) : TestCompetitionDrawRepository(repository,
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository) {

    @Test
    fun shouldOnlyReturnSeededRegistrations() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CupDrawSpec(
            competitionCategoryId = competitionCategory.id,
            matches = listOf()
        )
        spec.seeding = listOf(
            RegistrationSeedDTO(registrations[0].id, competitionCategory.id, 1),
            RegistrationSeedDTO(registrations[1].id, competitionCategory.id, 2),
            RegistrationSeedDTO(registrations[2].id, competitionCategory.id, null),
            RegistrationSeedDTO(registrations[3].id, competitionCategory.id, null)
        )

        // Act
        repository.store(spec)
        val seeds = repository.getSeeds(competitionCategory.id)

        // Assert
        Assertions.assertEquals(2, seeds.size, "Not the expected number of seeds returned")
        Assertions.assertTrue(seeds.all { it.seed != null }, "Should only return seeded registrations.")
    }
}