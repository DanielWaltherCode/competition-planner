package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCompetitionDrawRepository(
    @Autowired val repository: ICompetitionDrawRepository,
    @Autowired val clubRepository: IClubRepository,
    @Autowired val competitionRepository: ICompetitionRepository,
    @Autowired val competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired val categoryRepository: ICategoryRepository,
    @Autowired val playerRepository: PlayerRepository,
    @Autowired val registrationRepository: IRegistrationRepository
) {

    protected val dataGenerator = DataGenerator()

    fun CompetitionCategoryDTO.registerPlayers(players: List<PlayerDTO>): List<RegistrationSinglesDTO> {
        return players.map {
            dataGenerator.newRegistrationSinglesSpecWithDate(playerId = it.id, competitionCategoryId = this.id)
        }.map { registrationRepository.storeSingles(it) }
    }

    fun CompetitionDTO.createCategory(): CompetitionCategoryDTO {
        val category = categoryRepository.getAvailableCategories().first()
        return competitionCategoryRepository.store(
            this.id, dataGenerator.newCompetitionCategorySpec(
                category = dataGenerator.newCategorySpec(category.id, category.name, category.type),
                settings = dataGenerator.newGeneralSettingsSpec(drawType = DrawType.CUP_ONLY)
            )
        )
    }

    fun ClubDTO.addCompetition(): CompetitionDTO {
        val competitionSpec = dataGenerator.newCompetitionSpec(organizingClubId = this.id)
        return competitionRepository.store(competitionSpec)
    }

    fun ClubDTO.addPlayers(count: Int): List<PlayerDTO> {
        return (1..count).map { playerRepository.store(dataGenerator.newPlayerSpec(clubId = this.id)) }
    }
}