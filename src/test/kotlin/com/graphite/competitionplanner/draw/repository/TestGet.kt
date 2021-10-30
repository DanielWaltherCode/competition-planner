package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.domain.PlayOffDrawSpec
import com.graphite.competitionplanner.draw.domain.PlayOffMatch
import com.graphite.competitionplanner.draw.domain.Registration
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.PlayOffMatchDTO
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationDoublesDTO
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestGet(
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
    fun canGetSinglesPlayOff() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = PlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.SEMI_FINAL,
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
                    registrationOneId = Registration.Placeholder,
                    registrationTwoId = Registration.Placeholder,
                    order = 1,
                    round = Round.FINAL
                )

            )
        )

        // Act
        repository.store(spec)
        val result = repository.get(competitionCategory.id)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(3, result.playOff.size, "Expected to 3 matches.")

        val semifinals = result.playOff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinals.size, "Expected to find 2 semifinal matches")

        val firstSemi = semifinals.first { it.order == 1 }
        Assertions.assertEquals(registrations[0].playerId, firstSemi.player1.first().id)
        Assertions.assertEquals(registrations[1].playerId, firstSemi.player2.first().id)

        val secondSemi = semifinals.first { it.order == 2 }
        Assertions.assertEquals(registrations[2].playerId, secondSemi.player1.first().id)
        Assertions.assertEquals(registrations[3].playerId, secondSemi.player2.first().id)

        val final = result.playOff.filter { it.round == Round.FINAL }
        Assertions.assertEquals(1, final.size, "Expected to find 1 final match")
        Assertions.assertEquals(1, final.first().player1.first().id, "Placeholder id should be 1")
        Assertions.assertEquals(1, final.first().player2.first().id, "Placeholder id should be 1")
    }

    @Test
    fun canGetDoublesPlayOff() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(8)
        val registrationOne = competitionCategory.registerForDoubles(players.take(4).take(2))
        val registrationTwo = competitionCategory.registerForDoubles(players.take(4).takeLast(2))
        val registrationThree = competitionCategory.registerForDoubles(players.takeLast(4).take(2))
        val registrationFour = competitionCategory.registerForDoubles(players.takeLast(4).takeLast(2))
        val spec = PlayOffDrawSpec(
            competitionCategoryId = competitionCategory.id,
            startingRound = Round.SEMI_FINAL,
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrationOne.id),
                    registrationTwoId = Registration.Real(registrationTwo.id),
                    order = 1,
                    round = Round.SEMI_FINAL
                ),
                PlayOffMatch(
                    registrationOneId = Registration.Real(registrationThree.id),
                    registrationTwoId = Registration.Real(registrationFour.id),
                    order = 2,
                    round = Round.SEMI_FINAL
                )
            )
        )

        // Act
        repository.store(spec)
        val result = repository.get(competitionCategory.id)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(2, result.playOff.size, "Expected to find 2 matches")

        val semifinals = result.playOff.filter { it.round == Round.SEMI_FINAL }
        Assertions.assertEquals(2, semifinals.size, "Expected to find 2 semifinal matches")

        val firstSemi = semifinals.first { it.order == 1 }
        assertSemifinal(firstSemi, registrationOne, registrationTwo)

        val secondSim = semifinals.first { it.order == 2 }
        assertSemifinal(secondSim, registrationThree, registrationFour)
    }

    private fun assertSemifinal(
        semifinal: PlayOffMatchDTO,
        registrationOne: RegistrationDoublesDTO,
        registrationTwo: RegistrationDoublesDTO
    ) {
        Assertions.assertEquals(2, semifinal.player1.size, "Expected to find 2 players in first team")
        Assertions.assertEquals(2, semifinal.player2.size, "Expected to find 2 players in second team")

        val player1Ids = semifinal.player1.map { it.id }
        Assertions.assertTrue(player1Ids.contains(registrationOne.playerOneId),
            "Expected to find player with id ${registrationOne.playerOneId} in $player1Ids")
        Assertions.assertTrue(player1Ids.contains(registrationOne.playerTwoId),
            "Expected to find player with id ${registrationOne.playerTwoId} in $player1Ids")

        val player2Ids = semifinal.player2.map { it.id }
        Assertions.assertTrue(player2Ids.contains(registrationTwo.playerOneId),
            "Expected to find player with id ${registrationTwo.playerOneId} in $player2Ids")
        Assertions.assertTrue(player2Ids.contains(registrationTwo.playerTwoId),
            "Expected to find player with id ${registrationTwo.playerTwoId} in $player2Ids")
    }

    fun CompetitionCategoryDTO.registerForDoubles(players: List<PlayerDTO>): RegistrationDoublesDTO {
        val doubleRegistration = dataGenerator.newRegistrationDoublesSpecWithDate(
            playerOneId = players.first().id,
            playerTwoId = players.last().id,
            competitionCategoryId = this.id)
        return registrationRepository.storeDoubles(doubleRegistration)
    }
}