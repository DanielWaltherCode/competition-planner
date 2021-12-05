package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.PlayOffMatchDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TestPoolAndCup(
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
    fun canStorePlaceholderMappings() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val poolA = Pool(
            name = "A",
            registrationIds = registrationIds,
            matches = listOf(PoolMatch(registrationIds[0], registrationIds[1]))
        )
        val poolB = Pool(
            name = "B",
            registrationIds = registrationIds,
            matches = listOf(PoolMatch(registrationIds[2], registrationIds[3]))
        )
        val semifinals = listOf(
            PlayOffMatch(
                registrationOneId = Registration.Placeholder("A1"),
                registrationTwoId = Registration.Bye,
                order = 1,
                round = Round.SEMI_FINAL
            ),
            PlayOffMatch(
                registrationOneId = Registration.Placeholder("B1"),
                registrationTwoId = Registration.Placeholder("A2"),
                order = 2,
                round = Round.SEMI_FINAL
            )
        )
        val final = PlayOffMatch(
            registrationOneId = Registration.Placeholder(),
            registrationTwoId = Registration.Placeholder(),
            order = 1,
            round = Round.FINAL
        )
        val spec = PoolAndCupDrawSpec(
            competitionCategoryId = competitionCategory.id,
            pools = listOf(poolA, poolB),
            matches = semifinals + listOf(final),
            poolToPlayoffMap = emptyList()
        )

        // Act
        val result = repository.store(spec)

        // Assert
        val actualFinal = result.playOff.inRound(Round.FINAL)
        Assertions.assertEquals(1, actualFinal.size, "There should only be one final.")
        Assertions.assertEquals(1, actualFinal.first().player1.size)
        Assertions.assertEquals(1, actualFinal.first().player2.size)
        Assertions.assertEquals(Registration.Placeholder().toString(), actualFinal.first().player1.first().firstName)
        Assertions.assertEquals(Registration.Placeholder().toString(), actualFinal.first().player2.first().firstName)

        val actualSemifinals = result.playOff.inRound(Round.SEMI_FINAL)
        Assertions.assertEquals(2, actualSemifinals.size, "There should only be two semi finals")

        val firstSemi = actualSemifinals.first { it.order == 1 }
        Assertions.assertEquals(1, firstSemi.player1.size)
        Assertions.assertEquals(1, firstSemi.player2.size)
        Assertions.assertEquals("A1", firstSemi.player1.first().firstName)
        Assertions.assertEquals(Registration.Bye.toString(), firstSemi.player2.first().firstName)

        val secondSemi = actualSemifinals.first { it.order == 2 }
        Assertions.assertEquals(1, secondSemi.player1.size)
        Assertions.assertEquals(1, secondSemi.player2.size)
        Assertions.assertEquals("B1", secondSemi.player1.first().firstName)
        Assertions.assertEquals("A2", secondSemi.player2.first().firstName)
    }

    private fun List<PlayOffMatchDTO>.inRound(round: Round): List<PlayOffMatchDTO> {
        return this.filter { it.round == round }
    }

}