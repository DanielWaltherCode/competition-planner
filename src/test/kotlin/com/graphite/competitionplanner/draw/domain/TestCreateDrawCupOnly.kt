package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawCupOnly {

    private val mockedGetRegistrationInCompetitionCategory =
        Mockito.mock(GetRegistrationsInCompetitionCategory::class.java)
    private val mockedFindCompetitionCategory = Mockito.mock(FindCompetitionCategory::class.java)
    private val mockedRegistrationRepository = Mockito.mock(IRegistrationRepository::class.java)
    private val mockedSeedRepository = Mockito.mock(ISeedRepository::class.java)

    private val createDraw = CreateDraw(
        mockedGetRegistrationInCompetitionCategory,
        mockedFindCompetitionCategory,
        CreateSeed(),
        mockedRegistrationRepository,
        mockedSeedRepository
    )

    private val dataGenerator = DataGenerator()

    @Test
    fun sevenPlayersOneBye() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..7).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        val result = createDraw.execute(competitionCategory.id) as CompetitionCategoryPlayoffDrawDTO

        // Assert
        val matches = result.matches
        Assertions.assertEquals(4, matches.size)

        val bestToWorst = registrationRanks.sortedBy { -it.rank }
        val bestRegistration = bestToWorst[0]
        val byePlayerID = 0
        matches.assertThereIsMatchWith(bestRegistration.id, byePlayerID)

        val secondBestRegistration = bestToWorst[1]
        val worstRegistration = bestToWorst.last()
        matches.assertThereIsMatchWith(secondBestRegistration.id, worstRegistration.id)

        val thirdBestRegistration = bestToWorst[2]
        val secondWorstRegistration = bestToWorst[bestToWorst.size - 2]
        matches.assertThereIsMatchWith(thirdBestRegistration.id, secondWorstRegistration.id)

        val fourthBestRegistration = bestToWorst[3]
        val thirdWorstRegistration = bestToWorst[bestToWorst.size - 3]
        matches.assertThereIsMatchWith(fourthBestRegistration.id, thirdWorstRegistration.id)
    }

    @Test
    fun ninePlayersSevenByes() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..9).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        val result = createDraw.execute(competitionCategory.id) as CompetitionCategoryPlayoffDrawDTO

        // Assert
        val matches = result.matches
        Assertions.assertEquals(8, matches.size)

        val byePlayerID = 0
        val matchesWithBye = matches.filter { it.contains(byePlayerID) }
        Assertions.assertEquals(7, matchesWithBye.size, "Expected to find BYE player in 7 matches")

        val bestToWorst = registrationRanks.sortedBy { -it.rank }
        val bestRegistration = bestToWorst[0]
        matches.assertThereIsMatchWith(bestRegistration.id, byePlayerID)

        val secondBestRegistration = bestToWorst[1]
        matches.assertThereIsMatchWith(secondBestRegistration.id, byePlayerID)

        val thirdBestRegistration = bestToWorst[2]
        matches.assertThereIsMatchWith(thirdBestRegistration.id, byePlayerID)

        val fourthBestRegistration = bestToWorst[3]
        matches.assertThereIsMatchWith(fourthBestRegistration.id, byePlayerID)
    }

    @Test
    fun twoPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(2, 1, Round.FINAL)
    }

    @Test
    fun fourPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(4, 2, Round.SEMI_FINAL)
    }

    @Test
    fun eightPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(8, 4, Round.QUARTER_FINAL)
    }

    @Test
    fun sixteenPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(16, 8, Round.ROUND_OF_16)
    }

    @Test
    fun thirtyTwoPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(32, 16, Round.ROUND_OF_32)
    }

    @Test
    fun fiveHundredAndTwelvePlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(512, 256, Round.UNKNOWN)
    }

    fun assertThatCorrectNumberOfMatchesAreGenerated(
        numberOfPlayers: Int,
        expectedNumberOfGames: Int,
        expectedRound: Round
    ) {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..numberOfPlayers).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        val result = createDraw.execute(competitionCategory.id) as CompetitionCategoryPlayoffDrawDTO

        // Assert
        val matches = result.matches
        Assertions.assertEquals(expectedNumberOfGames, matches.size, "Wrong number of matches generated")
        Assertions.assertEquals(expectedRound, result.round)
    }

    /**
     * Raises assertion error if there are no match in the list of matches that contains both registrations
     */
    fun List<Match>.assertThereIsMatchWith(registrationIdOne: Int, registrationIdTwo: Int) {
        val match = this.find { it.contains(registrationIdOne) }!!
        Assertions.assertTrue(match.contains(registrationIdTwo),
            "Could not find a match containing registration ids $registrationIdOne and $registrationIdTwo")
    }

    fun Match.contains(registrationId: Int): Boolean {
        return this.registrationOneId == registrationId || this.registrationTwoId == registrationId
    }
}