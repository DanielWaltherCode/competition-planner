package com.graphite.competitionplanner.result.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.service.MatchSpec
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.result.service.ResultDTO
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestResultRepository @Autowired constructor(
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

    @Test
    fun gettingResultsFromMatches() {
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()
        val player1 = club.addPlayer("PlayerOne")
        val player2 = club.addPlayer("PlayerTwo")
        val reg1 = competitionCategory.registerPlayer(player1)
        val reg2 = competitionCategory.registerPlayer(player2)

        val match1 = matchRepository.addMatch(
            MatchSpec(
                null,
                null,
                competitionCategory.id,
                MatchType.GROUP,
                reg1.id,
                reg2.id,
                1,
                "A"
            )
        )
        val match2 = matchRepository.addMatch(
            MatchSpec(
                null,
                null,
                competitionCategory.id,
                MatchType.GROUP,
                reg1.id,
                reg2.id,
                2,
                "A"
            )
        )
        val match3 = matchRepository.addMatch(
            MatchSpec(
                null,
                null,
                competitionCategory.id,
                MatchType.GROUP,
                reg1.id,
                reg2.id,
                3,
                "A"
            )
        )
        val match4 = matchRepository.addMatch(
            MatchSpec(
                null,
                null,
                competitionCategory.id,
                MatchType.GROUP,
                reg1.id,
                reg2.id,
                3,
                "A"
            )
        )

        val resultMatch1 = ResultDTO(
            listOf(
                resultRepository.storeResult(match1.id, dataGenerator.newGameSpec(1)),
                resultRepository.storeResult(match1.id, dataGenerator.newGameSpec(2)),
                resultRepository.storeResult(match1.id, dataGenerator.newGameSpec(3))
            )
        )

        val resultMatch2 = ResultDTO(
            listOf(
                resultRepository.storeResult(match2.id, dataGenerator.newGameSpec(1)),
                resultRepository.storeResult(match2.id, dataGenerator.newGameSpec(2)),
                resultRepository.storeResult(match2.id, dataGenerator.newGameSpec(3))
            )
        )

        val resultMatch3 = ResultDTO(emptyList())


        // Act
        val results = resultRepository.getResults(listOf(match2.id, match1.id, match3.id))
        val results2 = resultRepository.getResults(listOf(match1.id, match2.id, match3.id))
        val ids = results.map { it.first }
        Assertions.assertEquals(
            listOf(match1.id, match2.id, match3.id),
            ids,
            "The result was not sorted in ascending order of match ids"
        )

        // Assert
        Assertions.assertEquals(results, results2, "Order of match IDs in input should not matter")

        val actualResultsMatch1 = results.first { p -> p.first == match1.id }.second
        Assertions.assertEquals(resultMatch1, actualResultsMatch1)

        val actualResultsMatch2 = results.first { p -> p.first == match2.id }.second
        Assertions.assertEquals(resultMatch2, actualResultsMatch2)

        val actualResultsMatch3 = results.first { p -> p.first == match3.id }.second
        Assertions.assertEquals(resultMatch3, actualResultsMatch3)

        val actualResultsMatch4 = results.filter { p -> p.first == match4.id }
        Assertions.assertEquals(0, actualResultsMatch4.size, "We got results from a match that we did not ask for.")
    }
}