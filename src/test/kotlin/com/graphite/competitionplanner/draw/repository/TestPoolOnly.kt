package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.Pool
import com.graphite.competitionplanner.draw.domain.PoolDrawSpec
import com.graphite.competitionplanner.draw.domain.PoolMatch
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.player.repository.PlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class TestPoolOnly(
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
    fun canStoreSinglesPoolOnly() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val spec = PoolDrawSpec(
            competitionCategoryId = competitionCategory.id,
            pools = listOf(
                Pool(
                    name = "A",
                    registrationIds = registrationIds.take(2),
                    matches = listOf(PoolMatch(registrationIds[0], registrationIds[1]))
                ),
                Pool(
                    name = "B",
                    registrationIds = registrationIds.takeLast(2),
                    matches = listOf(PoolMatch(registrationIds[2], registrationIds[3]))
                )
            )
        )

        // Act
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)

        val groups = result.groups

        Assertions.assertEquals(2, groups.size, "Expected to find 2 groups.")

        Assertions.assertNotNull(groups.find { it.name == "A" }, "Expected to find a group with name A")
        Assertions.assertNotNull(groups.find { it.name == "B" }, "Expected to find a group with name B")

        AssertionHelper.assertGroupDrawDto(spec.pools.first { it.name == "A" }, club, players.take(2), groups.first {it.name == "A"})
        AssertionHelper.assertGroupDrawDto(spec.pools.first { it.name == "B" }, club, players.takeLast(2), groups.first {it.name == "B"})

        val status = competitionCategoryRepository.get(result.competitionCategoryId).status
        Assertions.assertEquals(CompetitionCategoryStatus.DRAWN.name, status)
    }

    @Test
    fun canStoreDoublesPoolOnly() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrationOne = competitionCategory.registerForDoubles(players.take(2))
        val registrationTwo = competitionCategory.registerForDoubles(players.takeLast(2))
        val registrationIds = listOf(Registration.Real(registrationOne.id), Registration.Real(registrationTwo.id))
        val spec = PoolDrawSpec(
            competitionCategoryId = competitionCategory.id,
            pools = listOf(
                Pool(
                    name = "Z",
                    registrationIds = registrationIds,
                    matches = listOf(
                        PoolMatch(registrationIds[0], registrationIds[1]),
                        PoolMatch(registrationIds[0], registrationIds[1]),
                        PoolMatch(registrationIds[0], registrationIds[1])
                    )
                )
            )
        )

        // Act
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(competitionCategory.id, result.competitionCategoryId)

        val groups = result.groups

        Assertions.assertEquals(1, groups.size, "Expected to find 1 groups.")
        Assertions.assertNotNull(groups.find { it.name == "Z" })
        AssertionHelper.assertGroupDrawDto(spec.pools.first { it.name == "Z" }, club, players, groups.first {it.name == "Z"})

        val status = competitionCategoryRepository.get(result.competitionCategoryId).status
        Assertions.assertEquals(CompetitionCategoryStatus.DRAWN.name, status)
    }

    @Test
    fun canDeletePoolOnlyDraw() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val spec = PoolDrawSpec(
            competitionCategoryId = competitionCategory.id,
            pools = listOf(
                Pool(
                    name = "A",
                    registrationIds = registrationIds.take(2),
                    matches = listOf(PoolMatch(registrationIds[0], registrationIds[1]))
                ),
                Pool(
                    name = "B",
                    registrationIds = registrationIds.takeLast(2),
                    matches = listOf(PoolMatch(registrationIds[2], registrationIds[3]))
                )
            )
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

    @Test
    fun getAndStoreShouldReturnTheSame() {
        // Setup
        val club = clubRepository.store(dataGenerator.newClubSpec())
        val competition = club.addCompetition()
        val competitionCategory = competition.createCategory()
        val players = club.addPlayers(4)
        val registrations = competitionCategory.registerPlayers(players)
        val registrationIds = registrations.map { Registration.Real(it.id) }
        val spec = PoolDrawSpec(
            competitionCategoryId = competitionCategory.id,
            pools = listOf(
                Pool(
                    name = "A",
                    registrationIds = registrationIds.take(2),
                    matches = listOf(PoolMatch(registrationIds[0], registrationIds[1]))
                ),
                Pool(
                    name = "B",
                    registrationIds = registrationIds.takeLast(2),
                    matches = listOf(PoolMatch(registrationIds[2], registrationIds[3]))
                ),
                Pool(
                    name = "E",
                    registrationIds = registrationIds,
                    matches = listOf(
                        PoolMatch(registrationIds[0], registrationIds[1]),
                        PoolMatch(registrationIds[0], registrationIds[2]),
                        PoolMatch(registrationIds[0], registrationIds[3]))
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