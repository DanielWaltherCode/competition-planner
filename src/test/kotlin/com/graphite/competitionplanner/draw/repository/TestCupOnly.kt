package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.domain.CupDrawSpec
import com.graphite.competitionplanner.draw.domain.DeleteDraw
import com.graphite.competitionplanner.draw.domain.PlayOffMatch
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.match.service.MatchAndResultDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

/**
 * A test class to test database operations on a draw for DrawType.CUP_ONLY
 */
class TestCupOnly(
    @Autowired val deleteDraw: DeleteDraw,
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
    fun canStoreSinglesPlayOffWithRealPlayers() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(2)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CupDrawSpec(
            competitionCategoryId = competitionCategory.id,
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
        val drawDTO: CompetitionCategoryDrawDTO = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, drawDTO.competitionCategoryId)
        Assertions.assertEquals(1, drawDTO.playOff.size, "Expected to find 1 match")

        val final: MatchAndResultDTO = drawDTO.playOff.first().matches.first()
        Assertions.assertEquals(registrations[0].playerId, final.firstPlayer.first().id)
        Assertions.assertEquals(registrations[1].playerId, final.secondPlayer.first().id)
        Assertions.assertEquals(1, final.matchOrderNumber)
        Assertions.assertEquals(Round.FINAL.name, final.groupOrRound)

        val status = competitionCategoryRepository.get(drawDTO.competitionCategoryId).status
        Assertions.assertEquals(CompetitionCategoryStatus.DRAWN, status)
    }

    @Test
    fun canStoreSinglesPlayOffWithBye() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CupDrawSpec(
            competitionCategoryId = competitionCategory.id,
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
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(1, result.playOff.size, "Expected to find 1 match")

        val final = result.playOff.first().matches.first()
        Assertions.assertEquals(registrations[0].playerId, final.firstPlayer.first().id)
        Assertions.assertEquals(0, final.secondPlayer.first().id,
            "Expected to find id 0, which represents the bye player")
        Assertions.assertEquals(1, final.matchOrderNumber)
        Assertions.assertEquals(Round.FINAL.name, final.groupOrRound)
    }

    @Test
    fun canStoreSinglesPlayOffWithPlaceholder() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CupDrawSpec(
            competitionCategoryId = competitionCategory.id,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder(),
                    registrationTwoId = Registration.Real(registrations.first().id),
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        // Act
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(1, result.playOff.size, "Expected to find 1 match")

        val final = result.playOff.first().matches.first()
        Assertions.assertEquals(-1, final.firstPlayer.first().id,
            "Expected to find player id 1 which represents the placeholder player")
        Assertions.assertEquals(registrations[0].playerId, final.secondPlayer.first().id)
        Assertions.assertEquals(1, final.matchOrderNumber)
        Assertions.assertEquals(Round.FINAL.name, final.groupOrRound)
    }

    @Test
    fun canStoreDoublesPlayOffWithRealPlayers() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrationOne = competitionCategory.registerForDoubles(players.take(2))
        val registrationTwo = competitionCategory.registerForDoubles(players.takeLast(2))

        val spec = CupDrawSpec(
            competitionCategoryId = competitionCategory.id,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrationOne.id),
                    registrationTwoId = Registration.Real(registrationTwo.id),
                    order = 1,
                    round = Round.QUARTER_FINAL
                )
            )
        )

        // Act
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(1, result.playOff.size)
        AssertionHelper.assertDoubleRegistrationPlayOffMatch(result.playOff.first().matches.first(), registrationOne, registrationTwo)
    }

    @Test
    fun canDeleteCupOnlyDraw() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(2)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CupDrawSpec(
            competitionCategoryId = competitionCategory.id,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations.first().id),
                    registrationTwoId = Registration.Real(registrations.last().id),
                    order = 1,
                    round = Round.FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Bye,
                    registrationTwoId = Registration.Placeholder(),
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        val result = repository.store(spec)

        // Act
        deleteDraw.execute(result.competitionCategoryId)

        // Assert
        val draw = repository.get(competitionCategory.id)
        Assertions.assertEquals(0, draw.groups.size)
        Assertions.assertEquals(0, draw.playOff.size)

        val status = competitionCategoryRepository.get(competitionCategory.id).status
        Assertions.assertEquals(CompetitionCategoryStatus.CLOSED_FOR_REGISTRATION, status, "Status of category was not reset")
    }

    @Test
    fun getAndStoreShouldReturnTheSame() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = CupDrawSpec(
            competitionCategoryId = competitionCategory.id,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations[0].id),
                    registrationTwoId = Registration.Real(registrations[1].id),
                    order = 1,
                    round = Round.SEMI_FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrations[2].id),
                    registrationTwoId = Registration.Real(registrations[3].id),
                    order = 2,
                    round = Round.SEMI_FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Bye,
                    registrationTwoId = Registration.Placeholder(),
                    order = 1,
                    round = Round.FINAL
                )

            )
        )

        // Act
        val storeResult = repository.store(spec)
        val getResult = repository.get(competitionCategory.id)

        // Assert
        Assertions.assertEquals(storeResult, getResult)
    }
}