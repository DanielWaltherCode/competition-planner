package com.graphite.competitionplanner.match.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.match.domain.PlayoffMatch
import com.graphite.competitionplanner.match.domain.PoolMatch
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestMatchRepository @Autowired constructor(
    clubRepository: IClubRepository,
    competitionRepository: ICompetitionRepository,
    competitionCategoryRepository: ICompetitionCategoryRepository,
    categoryRepository: ICategoryRepository,
    playerRepository: IPlayerRepository,
    registrationRepository: IRegistrationRepository,
    matchRepository: MatchRepository,
    resultRepository: IResultRepository
) : BaseRepositoryTest(
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository,
    matchRepository,
    resultRepository
) {

    lateinit var competitionCategory: CompetitionCategoryDTO
    lateinit var reg1: RegistrationSinglesDTO
    lateinit var reg2: RegistrationSinglesDTO

    @BeforeEach
    fun setupDummyCompetitionCategory() {
        val club = newClub()
        val competition = club.addCompetition()
        competitionCategory = competition.addCompetitionCategory()
        val player1 = club.addPlayer("PlayerOne")
        val player2 = club.addPlayer("PlayerTwo")
        reg1 = competitionCategory.registerPlayer(player1)
        reg2 = competitionCategory.registerPlayer(player2)
    }

    @Test
    fun storePoolMatch() {
        // Setup
        val spec = dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = reg1.id,
            secondRegistrationId = reg2.id,
            matchType = MatchType.GROUP,
            groupOrRound = "A"
        )

        // Act
        val match = matchRepository.store(spec) as PoolMatch

        // Assert
        Assertions.assertTrue(match.id > 0)
        Assertions.assertEquals(spec.groupOrRound, match.name)
        Assertions.assertEquals(spec.startTime, match.startTime)
        Assertions.assertEquals(spec.endTime, match.endTime)
        Assertions.assertEquals(spec.competitionCategoryId, match.competitionCategoryId)
        Assertions.assertTrue(match.result.isEmpty(), "There should not be a result on a newly created match ${match.result}")
        Assertions.assertFalse(match.wasWalkOver, "Default value of wasWalkOver should be false")
        Assertions.assertTrue(match.winner == null, "There should not be a winner on a newly created match")
    }

    @Test
    fun storePlayoffMatch() {
        // Setup
        val spec = dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = reg1.id,
            secondRegistrationId = reg2.id,
            matchType = MatchType.PLAYOFF,
            groupOrRound = Round.FINAL.name,
            matchOrderNumber = 2
        )

        // Act
        val match = matchRepository.store(spec) as PlayoffMatch

        // Assert
        Assertions.assertTrue(match.id > 0)
        Assertions.assertEquals(spec.matchOrderNumber, match.orderNumber)
        Assertions.assertEquals(Round.valueOf(spec.groupOrRound), match.round)
        Assertions.assertEquals(spec.startTime, match.startTime)
        Assertions.assertEquals(spec.endTime, match.endTime)
        Assertions.assertEquals(spec.competitionCategoryId, match.competitionCategoryId)
        Assertions.assertTrue(match.result.isEmpty(), "There should not be a result on a newly created match ${match.result}")
        Assertions.assertFalse(match.wasWalkOver, "Default value of wasWalkOver should be false")
        Assertions.assertTrue(match.winner == null, "There should not be a winner on a newly created match")
    }

}