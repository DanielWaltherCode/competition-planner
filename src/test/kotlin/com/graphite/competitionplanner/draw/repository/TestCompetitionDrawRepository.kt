package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.domain.CompetitionCategoryPlayOffDrawSpec
import com.graphite.competitionplanner.draw.domain.PlayOffMatch
import com.graphite.competitionplanner.draw.domain.Registration
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Test
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


    private val dataGenerator = DataGenerator()

    @Test
    fun canStorePlayOff() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(2)
        val registrations = competitionCategory.registerPlayers(players)
        val dto = CompetitionCategoryPlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.FINAL,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations.first().id),
                    registrationTwoId = Registration.Real(registrations.last().id),
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        // Act
        repository.store(dto)
    }

    @Test
    fun canStorePlayOffWithBye() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val dto = CompetitionCategoryPlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.FINAL,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations.first().id),
                    registrationTwoId = Registration.Bye,
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        // Act
        repository.store(dto)
    }

    @Test
    fun canStorePlayOffWithPlaceholder() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val dto = CompetitionCategoryPlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.FINAL,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder,
                    registrationTwoId = Registration.Real(registrations.first().id),
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        // Act
        repository.store(dto)
    }

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