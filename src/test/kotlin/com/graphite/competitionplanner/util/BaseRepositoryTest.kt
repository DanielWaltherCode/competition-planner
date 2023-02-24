package com.graphite.competitionplanner.util

import com.graphite.competitionplanner.category.domain.CategoryType
import com.graphite.competitionplanner.category.interfaces.CustomCategorySpec
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BaseRepositoryTest(
    val clubRepository: IClubRepository,
    val competitionRepository: ICompetitionRepository,
    val competitionCategoryRepository: ICompetitionCategoryRepository,
    val categoryRepository: ICategoryRepository,
    val playerRepository: IPlayerRepository,
    val registrationRepository: IRegistrationRepository,
    val matchRepository: MatchRepository,
    val resultRepository: IResultRepository
) {

    protected val dataGenerator = DataGenerator()

    fun newClub(): ClubDTO {
        return clubRepository.store(dataGenerator.newClubSpec())
    }

    fun ClubDTO.addCompetition(): CompetitionDTO {
        return competitionRepository.store(dataGenerator.newCompetitionSpec(organizingClubId = this.id))
    }

    fun ClubDTO.addPlayer(name: String): PlayerDTO {
        return playerRepository.store(dataGenerator.newPlayerSpec(clubId = this.id, firstName = name))
    }

    fun CompetitionDTO.addCompetitionCategory(): CompetitionCategoryDTO {
        val category = categoryRepository.addCustomCategory(this.id, CustomCategorySpec("MY-TEST-CATEGORY", CategoryType.SINGLES))
        return competitionCategoryRepository.store(
            this.id,
            dataGenerator.newCompetitionCategorySpec(
                category = dataGenerator.newCategorySpec(
                    id = category.id,
                    name = category.name,
                    type = category.type
                )
            )
        )
    }

    fun CompetitionDTO.addCompetitionCategory(
        categoryName: String,
        drawType: DrawType = DrawType.POOL_ONLY,
        playersToPlayoff: Int = 2
    ): CompetitionCategoryDTO {
        val category = categoryRepository.addCustomCategory(this.id, CustomCategorySpec(categoryName, CategoryType.SINGLES))
        return competitionCategoryRepository.store(
            this.id,
            dataGenerator.newCompetitionCategorySpec(
                category = dataGenerator.newCategorySpec(
                    id = category.id,
                    name = category.name,
                    type = category.type
                ),
                settings = dataGenerator.newGeneralSettingsDTO(
                    drawType = drawType,
                    playersToPlayOff = playersToPlayoff
                )
            )
        )
    }

    fun CompetitionCategoryDTO.registerPlayer(playerDTO: PlayerDTO): RegistrationSinglesDTO {
        return registrationRepository.storeSingles(
            dataGenerator.newRegistrationSinglesSpecWithDate(
                playerId = playerDTO.id,
                competitionCategoryId = this.id
            )
        )
    }

}