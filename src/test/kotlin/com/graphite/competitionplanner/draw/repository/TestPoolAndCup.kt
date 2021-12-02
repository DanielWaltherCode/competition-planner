package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
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
    fun canStoreGroupToPlayOfMapping() {
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
        val final = PlayOffMatch(
            registrationOneId = Registration.Real(registrations[0].id),
            registrationTwoId = Registration.Real(registrations[1].id),
            order = 1,
            round = Round.FINAL
        )
        val spec = PoolAndCupDrawSpec(
            competitionCategoryId = competitionCategory.id,
            pools = listOf(poolA),
            matches = listOf(final),
            poolToPlayoffMap = listOf(
                PoolToPlayoffSpec(
                    pool = poolA,
                    position = 1,
                    final,
                    2
                ),
                PoolToPlayoffSpec(
                    pool = poolA,
                    position = 2,
                    final,
                    1
                )
            )
        )

        // Act
        val result = repository.store(spec)

        // Assert
        Assertions.assertEquals(1, result.poolToPlayoffMapping.size)
        val poolToPlayoff = result.poolToPlayoffMapping.first()
        Assertions.assertTrue(poolToPlayoff.playOffMatchId > 0)
        Assertions.assertEquals(poolA.name, poolToPlayoff.player1.groupName)
        Assertions.assertEquals(poolA.name, poolToPlayoff.player2.groupName)
        Assertions.assertEquals(2, poolToPlayoff.player1.position, "Expected player1 to come from pool position 2")
        Assertions.assertEquals(1, poolToPlayoff.player2.position, "Expected player2 to come from pool position 1")
    }

}