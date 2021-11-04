package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TestStore(
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
    fun canStorePlayOff() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(2)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = PlayOffDrawSpec(
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
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(1, result.playOff.size, "Expected to find 1 match")

        val final = result.playOff.first()
        Assertions.assertEquals(registrations[0].playerId, final.player1.first().id)
        Assertions.assertEquals(registrations[1].playerId, final.player2.first().id)
        Assertions.assertEquals(1, final.order)
        Assertions.assertEquals(Round.FINAL, final.round)
    }

    @Test
    fun canStorePlayOffWithBye() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = PlayOffDrawSpec(
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
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(1, result.playOff.size, "Expected to find 1 match")

        val final = result.playOff.first()
        Assertions.assertEquals(registrations[0].playerId, final.player1.first().id)
        Assertions.assertEquals(0, final.player2.first().id,
            "Expected to find id 0, which represents the bye player")
        Assertions.assertEquals(1, final.order)
        Assertions.assertEquals(Round.FINAL, final.round)
    }

    @Test
    fun canStorePlayOffWithPlaceholder() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(1)
        val registrations = competitionCategory.registerPlayers(players)
        val spec = PlayOffDrawSpec(
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
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(1, result.playOff.size, "Expected to find 1 match")

        val final = result.playOff.first()
        Assertions.assertEquals(1, final.player1.first().id,
            "Expected to find player id 1 which represents the placeholder player")
        Assertions.assertEquals(registrations[0].playerId, final.player2.first().id)
        Assertions.assertEquals(1, final.order)
        Assertions.assertEquals(Round.FINAL, final.round)
    }

    @Test
    fun canStoreGroupMatches() {
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val spec = GroupsDrawSpec(
            competitionCategoryId = competitionCategory.id,
            groups = listOf(
                Group(
                    name = "A",
                    registrationIds = registrationIds.take(2),
                    matches = listOf(GroupMatch(registrationIds[0], registrationIds[1]))
                ),
                Group(
                    name = "B",
                    registrationIds = registrationIds.takeLast(2),
                    matches = listOf(GroupMatch(registrationIds[2], registrationIds[3]))
                )
            ),
            matches = listOf(
                PlayOffMatch(
                    registrationOneId = Registration.Placeholder,
                    registrationTwoId = Registration.Placeholder,
                    order = 1,
                    round = Round.FINAL
                )
            )
        )

        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)
        Assertions.assertEquals(2, result.groupDraw.size, "Expected to find two groups")
        Assertions.assertEquals(1, result.playOff.size, "Expected to find 1 playoff match")

        val groupA = result.groupDraw.first { it.name == "A" }
        Assertions.assertEquals(2, groupA.players.size, "Expected to find 2 players in group A")
        Assertions.assertEquals(1, groupA.matches.size, "Expected to find 1 game in group A")

        val groupB = result.groupDraw.first { it.name == "B" }
        Assertions.assertEquals(2, groupB.players.size, "Expected to find 2 players in group B")
        Assertions.assertEquals(1, groupB.matches.size, "Expected to find 1 game in group B")

        val final = result.playOff.first()
        Assertions.assertEquals(1, final.player1.first().id,
            "Expected to find player id 1 which represents the placeholder player")
        Assertions.assertEquals(1, final.player2.first().id,
            "Expected to find player id 1 which represents the placeholder player")
        Assertions.assertEquals(1, final.order)
        Assertions.assertEquals(Round.FINAL, final.round)
    }
}