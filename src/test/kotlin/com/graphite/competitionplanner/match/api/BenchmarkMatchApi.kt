package com.graphite.competitionplanner.match.api

import com.graphite.competitionplanner.category.domain.DefaultCategory
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.CreateDraw
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@Disabled
class BenchmarkMatchApi(
    @Autowired val matchApi: MatchApi,
    @Autowired val createDraw: CreateDraw,
    @Autowired clubRepository: IClubRepository,
    @Autowired competitionRepository: ICompetitionRepository,
    @Autowired competitionCategoryRepository: ICompetitionCategoryRepository,
    @Autowired categoryRepository: ICategoryRepository,
    @Autowired playerRepository: IPlayerRepository,
    @Autowired registrationRepository: IRegistrationRepository,
    @Autowired matchRepository: MatchRepository,
    @Autowired resultRepository: IResultRepository,
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

    /**
     * Given: 512 players, pool size of 4, 6 matches per group, and 128 matches in first round of playoff, we have
     * in total 1023 matches in this competition.
     */
    @Test
    fun performanceTestingGetMatches() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory(
            categoryName = DefaultCategory.MEN_1.name,
            drawType = DrawType.POOL_AND_CUP
        )

        val players = (1..512).map { club.addPlayer("PlayerName") }
        players.map { competitionCategory.registerPlayer(it) }

        createDraw.execute(competitionCategory.id)

        // Act
        val results = TestHelper.Benchmark.runTimes(10) {
            matchApi.getAllMatchesInCompetition(competition.id)
        }

        println("Worst: ${results.worst.toMillis()}")
        println("Best: ${results.best.toMillis()}")
        println("Average: ${results.avg}")

        // Assert
        Assertions.assertTrue(
            results.worst.toMillis() < 1000,
            "Should not take longer than 1000 ms to fetch all matches")
        Assertions.assertTrue(
            results.avg < 900,
            "Should not take longer than 1000 ms to fetch all matches on average"
        )
    }
}