package com.graphite.competitionplanner.player.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestFindByNameInCompetition(
    @Autowired val competitionRepository: ICompetitionRepository,
    @Autowired val competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired val registrationRepository: IRegistrationRepository,
    @Autowired val clubRepository: IClubRepository,
    @Autowired val playerRepository: IPlayerRepository,
    @Autowired val categoryRepository: ICategoryRepository
) {

    private val dataGenerator = DataGenerator()

    @Test
    fun testFindByNameInCompetition() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val player1 = playerRepository.store(
            dataGenerator.newPlayerSpec(
                firstName = "Nisse",
                lastName = "Hanson",
                clubId = club.id
            )
        )
        val player2 =
            playerRepository.store(dataGenerator.newPlayerSpec(firstName = "Bisse", "Nisseson", clubId = club.id))
        val player3 =
            playerRepository.store(dataGenerator.newPlayerSpec(firstName = "Cisse", "Filipsson", clubId = club.id))
        val competition1 = club.createCompetitionAndRegister(listOf(player1, player2, player3))

        val player4 =
            playerRepository.store(dataGenerator.newPlayerSpec(firstName = "Nisse", "Hanson", clubId = club.id))
        club.createCompetitionAndRegister(listOf(player4)) // Create additional competition. Should not get result from this

        // Act
        val players = playerRepository.findByNameInCompetition("Nis", competition1.id)

        // Assert
        val expectedPlayerIds = listOf(player1, player2).map { it.id }
        val actualPlayerIds = players.map { it.id }
        Assertions.assertEquals(expectedPlayerIds, actualPlayerIds)
    }

    private fun ClubDTO.createCompetitionAndRegister(players: List<PlayerDTO>): CompetitionDTO {
        val competition = competitionRepository.store(dataGenerator.newCompetitionSpec(organizingClubId = this.id))
        val category = categoryRepository.getAvailableCategories().first()
        val competitionCategory =
            competitionCategoryRepository.store(
                competition.id,
                dataGenerator.newCompetitionCategorySpec(
                    category = dataGenerator.newCategorySpec(
                        id = category.id,
                        name = category.name,
                        type = category.type
                    )
                )
            )
        for (p in players) {
            registrationRepository.storeSingles(
                dataGenerator.newRegistrationSinglesSpecWithDate(
                    playerId = p.id,
                    competitionCategoryId = competitionCategory.id
                )
            )
        }
        return competition
    }
}