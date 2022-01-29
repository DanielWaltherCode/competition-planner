package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.draw.interfaces.NotEnoughRegistrationsException
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawCupOnly {

    private val mockedGetRegistrationInCompetitionCategory =
        Mockito.mock(GetRegistrationsInCompetitionCategory::class.java)
    private val mockedFindCompetitionCategory = Mockito.mock(FindCompetitionCategory::class.java)
    private val mockedRegistrationRepository = Mockito.mock(IRegistrationRepository::class.java)
    private val mockedSeedRepository = Mockito.mock(ISeedRepository::class.java)
    private val mockedCompetitionDrawRepository = Mockito.mock(ICompetitionDrawRepository::class.java)
    private val mockedCompetitionCategoryRepository = Mockito.mock(ICompetitionCategoryRepository::class.java)

    private val createDraw = CreateDraw(
        mockedGetRegistrationInCompetitionCategory,
        mockedFindCompetitionCategory,
        CreateSeed(),
        mockedRegistrationRepository,
        mockedSeedRepository,
        mockedCompetitionDrawRepository,
        mockedCompetitionCategoryRepository
    )

    private val dataGenerator = DataGenerator()

    @Captor
    lateinit var classCaptor: ArgumentCaptor<CompetitionCategoryDrawSpec>

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
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as CupDrawSpec

        // Assert
        val matches = result.matches
        Assertions.assertEquals(7, matches.size, "In a binary tree with 4 leaves, there should be in total 7 nodes.")

        val bestToWorst = registrationRanks.sortedBy { -it.rank }.map { Registration.Real(it.registrationId) }
        val bestRegistration = bestToWorst[0]
        matches.assertThereIsMatchWith(bestRegistration, Registration.Bye)

        val secondBestRegistration = bestToWorst[1]
        val worstRegistration = bestToWorst.last()
        matches.assertThereIsMatchWith(secondBestRegistration, worstRegistration)

        val thirdBestRegistration = bestToWorst[2]
        val thirdWorstRegistration = bestToWorst[bestToWorst.size - 3]
        matches.assertThereIsMatchWith(thirdBestRegistration, thirdWorstRegistration)

        val fourthBestRegistration = bestToWorst[3]
        val secondWorstRegistration = bestToWorst[bestToWorst.size - 2]
        matches.assertThereIsMatchWith(fourthBestRegistration, secondWorstRegistration)
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
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as CupDrawSpec

        // Assert
        val matches = result.matches
        Assertions.assertEquals(15, matches.size, "In a binary tree with 8 leaves, there should be in total 15 nodes.")

        val byePlayerID = Registration.Bye
        val matchesWithByeInFirstRound =
            matches.filter { it.round == Round.ROUND_OF_16 }.filter { it.contains(byePlayerID) }
        Assertions.assertEquals(7, matchesWithByeInFirstRound.size, "Expected to find 7 BYE players in first round")

        val bestToWorst = registrationRanks.sortedBy { -it.rank }.map { Registration.Real(it.registrationId) }
        val bestRegistration = bestToWorst[0]
        matches.assertThereIsMatchWith(bestRegistration, byePlayerID)

        val secondBestRegistration = bestToWorst[1]
        matches.assertThereIsMatchWith(secondBestRegistration, byePlayerID)

        val thirdBestRegistration = bestToWorst[2]
        matches.assertThereIsMatchWith(thirdBestRegistration, byePlayerID)

        val fourthBestRegistration = bestToWorst[3]
        matches.assertThereIsMatchWith(fourthBestRegistration, byePlayerID)
    }

    @Test
    fun onePlayer() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..1).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act & Assert
        Assertions.assertThrows(NotEnoughRegistrationsException::class.java) {
            createDraw.execute(competitionCategory.id)
        }
    }

    @Test
    fun twoPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(2, Round.FINAL)
    }

    @Test
    fun fourPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(4, Round.SEMI_FINAL)
    }

    @Test
    fun eightPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(8, Round.QUARTER_FINAL)
    }

    @Test
    fun sixteenPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(16, Round.ROUND_OF_16)
    }

    @Test
    fun thirtyTwoPlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(32, Round.ROUND_OF_32)
    }

    @Test
    fun fiveHundredAndTwelvePlayers() {
        assertThatCorrectNumberOfMatchesAreGenerated(512, Round.UNKNOWN)
    }

    fun assertThatCorrectNumberOfMatchesAreGenerated(
        numberOfPlayers: Int,
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
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as CupDrawSpec

        // Assert
        val expectedNumberOfMatches =
            numberOfPlayers - 1 // Based on the formula for total number of nodes in a binary tree. Which is 2 * numberOfLeafNodes - 1
        val matches = result.matches
        Assertions.assertEquals(expectedNumberOfMatches, matches.size, "Wrong number of matches generated")
        Assertions.assertEquals(expectedRound, result.startingRound)
    }

    /**
     * Raises assertion error if there are no match in the list of matches that contains both registrations
     */
    fun List<PlayOffMatch>.assertThereIsMatchWith(registrationOne: Registration, registrationTwo: Registration) {
        val match = this.find { it.contains(registrationOne) }!!
        Assertions.assertTrue(match.contains(registrationTwo),
            "Could not find a match containing registration ids $registrationOne and $registrationTwo")
    }

    fun PlayOffMatch.contains(registration: Registration): Boolean {
        when (registration) {
            is Registration.Real -> {
                return if (this.registrationOneId is Registration.Real && this.registrationTwoId is Registration.Real) {
                    (this.registrationOneId as Registration.Real).id == registration.id || (this.registrationTwoId as Registration.Real).id == registration.id
                } else if (this.registrationOneId is Registration.Real) {
                    (this.registrationOneId as Registration.Real).id == registration.id
                } else if (this.registrationTwoId is Registration.Real) {
                    (this.registrationTwoId as Registration.Real).id == registration.id
                } else {
                    false
                }
            }
            is Registration.Bye -> {
                return this.registrationOneId is Registration.Bye || this.registrationTwoId is Registration.Bye
            }
            is Registration.Placeholder -> {
                return this.registrationOneId is Registration.Placeholder || this.registrationTwoId is Registration.Placeholder
            }
        }
    }
}