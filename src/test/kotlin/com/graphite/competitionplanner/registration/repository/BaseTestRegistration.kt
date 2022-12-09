package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CustomCategorySpec
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
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDate

open class BaseTestRegistration(
    val clubRepository: ClubRepository,
    val playerRepository: PlayerRepository,
    val competitionRepository: CompetitionRepository,
    val categoryRepository: CategoryRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    val registrationRepository: IRegistrationRepository
) {

    protected val dataGenerator = DataGenerator()
    protected lateinit var club: ClubDTO
    protected lateinit var competition: CompetitionDTO
    protected lateinit var competitionCategory: CompetitionCategoryDTO
    protected lateinit var category: CategoryDTO

    @BeforeEach
    fun setup() {
        club = clubRepository.store(dataGenerator.newClubSpec())
        competition = competitionRepository.store(dataGenerator.newCompetitionSpec(organizingClubId = club.id))
        setupCompetitionCategory()
    }

    open fun setupCompetitionCategory() {
        category = categoryRepository.addCustomCategory(competition.id, CustomCategorySpec("MY-TEST-CATEGORY", CategoryType.SINGLES))
        competitionCategory = competitionCategoryRepository.store(
            competitionId = competition.id,
            spec = dataGenerator.newCompetitionCategorySpec(category = dataGenerator.newCategorySpec(id = category.id)))
    }

    protected fun setupDoubleRegistration(): RegistrationDoublesDTO {
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

    protected fun setupSingleRegistration(): RegistrationSinglesDTO {
        val player = playerRepository.store(dataGenerator.newPlayerSpec(clubId = club.id))
        val spec = dataGenerator.newRegistrationSinglesSpecWithDate(
            date = LocalDate.now(),
            playerId = player.id,
            competitionCategoryId = competitionCategory.id
        )
        return registrationRepository.storeSingles(spec)
    }
}