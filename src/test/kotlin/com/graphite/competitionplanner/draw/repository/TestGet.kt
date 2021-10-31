package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.GroupDrawDTO
import com.graphite.competitionplanner.draw.interfaces.GroupMatchDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.PlayOffMatchDTO
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
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

    @Test
    fun canGetSinglesGroupDraw() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(9)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val spec = GroupsDrawSpec(
            competitionCategoryId = competitionCategory.id,
            groups = listOf(
                Group(
                    name = "A",
                    registrationIds = registrationIds.take(3),
                    matches = listOf(
                        GroupMatch(registrationIds[0], registrationIds[1]),
                        GroupMatch(registrationIds[0], registrationIds[2]),
                        GroupMatch(registrationIds[1], registrationIds[2])
                    )
                ),
                Group(
                    name = "B",
                    registrationIds = registrationIds.drop(3).take(3),
                    matches = listOf(
                        GroupMatch(registrationIds[3], registrationIds[4]),
                        GroupMatch(registrationIds[3], registrationIds[5]),
                        GroupMatch(registrationIds[4], registrationIds[5])
                    )
                ),
                Group(
                    name = "C",
                    registrationIds = registrationIds.drop(6).take(3),
                    matches = listOf(
                        GroupMatch(registrationIds[6], registrationIds[7]),
                        GroupMatch(registrationIds[6], registrationIds[8]),
                        GroupMatch(registrationIds[7], registrationIds[8])
                    )
                )
            ),
            matches = emptyList()
        )

        // Act
        repository.store(spec)
        val result = repository.get(competitionCategory.id)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(3, result.groupDraw.size, "Expected to find 3 groups")
        Assertions.assertEquals(0, result.playOff.size, "Expected to find 0 playoff matches.")

        val playersWithClub = players.map { PlayerWithClubDTO(it.id, it.firstName, it.lastName, club, it.dateOfBirth) }

        // Validate group A
        val expectedGroupA = GroupDrawDTO(
            name = "A",
            players = listOf(playersWithClub[0], playersWithClub[1], playersWithClub[2]),
            matches = listOf(
                GroupMatchDTO(listOf(playersWithClub[0]), listOf(playersWithClub[1])),
                GroupMatchDTO(listOf(playersWithClub[0]), listOf(playersWithClub[2])),
                GroupMatchDTO(listOf(playersWithClub[1]), listOf(playersWithClub[2]))
            )
        )

        val actualGroupA = result.groupDraw.first { it.name == "A" }
        assertGroupsEqual(expectedGroupA, actualGroupA)

        // Validate group B
        val expectedGroupB = GroupDrawDTO(
            name = "B",
            players = listOf(playersWithClub[3], playersWithClub[4], playersWithClub[5]),
            matches = listOf(
                GroupMatchDTO(listOf(playersWithClub[3]), listOf(playersWithClub[4])),
                GroupMatchDTO(listOf(playersWithClub[3]), listOf(playersWithClub[5])),
                GroupMatchDTO(listOf(playersWithClub[4]), listOf(playersWithClub[5]))
            )
        )

        val actualGroupB = result.groupDraw.first { it.name == "B" }
        assertGroupsEqual(expectedGroupB, actualGroupB)

        // Validate group C
        val expectedGroupC = GroupDrawDTO(
            name = "C",
            players = listOf(playersWithClub[6], playersWithClub[7], playersWithClub[8]),
            matches = listOf(
                GroupMatchDTO(listOf(playersWithClub[6]), listOf(playersWithClub[7])),
                GroupMatchDTO(listOf(playersWithClub[6]), listOf(playersWithClub[8])),
                GroupMatchDTO(listOf(playersWithClub[7]), listOf(playersWithClub[8]))
            )
        )

        val actualGroupC = result.groupDraw.first { it.name == "C" }
        assertGroupsEqual(expectedGroupC, actualGroupC)
    }

    private fun assertGroupsEqual(expected: GroupDrawDTO, actual: GroupDrawDTO) {
        Assertions.assertEquals(expected.name, actual.name)

        Assertions.assertEquals(
            expected.players.sortedBy { it.id },
            actual.players.sortedBy { it.id }
        )

        Assertions.assertEquals(expected.matches.size, actual.matches.size)
        for (match in expected.matches) {
            Assertions.assertTrue(actual.matches.contains(match), "Expected to find match $match")
        }
    }
}