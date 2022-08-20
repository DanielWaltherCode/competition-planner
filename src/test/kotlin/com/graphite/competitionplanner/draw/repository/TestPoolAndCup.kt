package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.PlayoffRoundDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.domain.Registration
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
        )

        // Act
        val result = repository.store(spec)

        // Assert
        val actualFinal = result.playOff.inRound(Round.FINAL)
        Assertions.assertEquals(1, actualFinal.matches.size, "There should only be one final.")
        Assertions.assertEquals(1, actualFinal.matches.first().firstPlayer.size)
        Assertions.assertEquals(1, actualFinal.matches.first().secondPlayer.size)
        Assertions.assertEquals(Registration.Placeholder().toString(), actualFinal.matches.first().firstPlayer.first().firstName)
        Assertions.assertEquals(Registration.Placeholder().toString(), actualFinal.matches.first().secondPlayer.first().firstName)

        val actualSemifinals = result.playOff.inRound(Round.SEMI_FINAL)
        Assertions.assertEquals(2, actualSemifinals.matches.size, "There should only be two semi finals")

        val firstSemi = actualSemifinals.matches.first { it.matchOrderNumber == 1 }
        Assertions.assertEquals(1, firstSemi.firstPlayer.size)
        Assertions.assertEquals(1, firstSemi.secondPlayer.size)
        Assertions.assertEquals("A1", firstSemi.firstPlayer.first().firstName)
        Assertions.assertEquals(Registration.Bye.toString(), firstSemi.secondPlayer.first().firstName)

        val secondSemi = actualSemifinals.matches.first{ it.matchOrderNumber == 2 }
        Assertions.assertEquals(1, secondSemi.firstPlayer.size)
        Assertions.assertEquals(1, secondSemi.secondPlayer.size)
        Assertions.assertEquals("B1", secondSemi.firstPlayer.first().firstName)
        Assertions.assertEquals("A2", secondSemi.secondPlayer.first().firstName)

        val status = competitionCategoryRepository.get(result.competitionCategoryId).status
        Assertions.assertEquals(CompetitionCategoryStatus.DRAWN.name, status)
    }

    @Test
    fun canDeletePoolAndCupDraw() {
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
        )

        val result = repository.store(spec)

        // Act
        repository.delete(result.competitionCategoryId)

        // Assert
        val draw = repository.get(competitionCategory.id)
        Assertions.assertEquals(0, draw.groups.size)
        Assertions.assertEquals(0, draw.playOff.size)

        val status = competitionCategoryRepository.get(competitionCategory.id).status
        Assertions.assertEquals(CompetitionCategoryStatus.ACTIVE.name, status, "Status of category was not reset")
    }

    private fun List<PlayoffRoundDTO>.inRound(round: Round): PlayoffRoundDTO {
        return this.first { it.round == round }
    }

}