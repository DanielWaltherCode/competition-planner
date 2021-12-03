package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import com.graphite.competitionplanner.util.TestHelper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPoolToPlayoffMapping {

    private val mockedGetRegistrationInCompetitionCategory =
        Mockito.mock(GetRegistrationsInCompetitionCategory::class.java)
    private val mockedFindCompetitionCategory = Mockito.mock(FindCompetitionCategory::class.java)
    private val mockedRegistrationRepository = Mockito.mock(IRegistrationRepository::class.java)
    private val mockedSeedRepository = Mockito.mock(ISeedRepository::class.java)
    private val mockedCompetitionDrawRepository = Mockito.mock(ICompetitionDrawRepository::class.java)

    private val createDraw = CreateDraw(
        mockedGetRegistrationInCompetitionCategory,
        mockedFindCompetitionCategory,
        CreateSeed(),
        mockedRegistrationRepository,
        mockedSeedRepository,
        mockedCompetitionDrawRepository
    )

    private val dataGenerator = DataGenerator()

    @Captor
    lateinit var classCaptor: ArgumentCaptor<CompetitionCategoryDrawSpec>

    @Test
    fun whenFourPlayersAdvanceToPlayoff() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 35,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = (1..8).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        val finalExpectedName = listOf("Placeholder", "Placeholder")
        result.matches.assertNamesInRoundEqual(Round.FINAL, finalExpectedName)

        val semiFinalExpectedNames = listOf("A1", "A2", "B1", "B2").sorted()
        result.matches.assertNamesInRoundEqual(Round.SEMI_FINAL, semiFinalExpectedNames)
    }

    @Test
    fun whenSixPlayersAdvanceToPlayoff() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 35,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4,
                playersToPlayOff = 2
            )
        )
        val registrationRanks = (1..12).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        val finalExpectedName = listOf("Placeholder", "Placeholder")
        result.matches.assertNamesInRoundEqual(Round.FINAL, finalExpectedName)

        val semiFinalExpectedNames = listOf("Placeholder", "Placeholder", "Placeholder", "Placeholder")
        result.matches.assertNamesInRoundEqual(Round.SEMI_FINAL, semiFinalExpectedNames)

        val quarterFinalExpectedNames = listOf("A1", "A2", "B1", "B2", "C1", "C2", "BYE", "BYE").sorted()
        result.matches.assertNamesInRoundEqual(Round.QUARTER_FINAL, quarterFinalExpectedNames)

        val matchUps = result.matches.map { Pair(it.registrationOneId.toString(), it.registrationTwoId.toString()) }
        Assertions.assertTrue(matchUps.contains(Pair("A1", "BYE")), "Expected to find match up A1 vs BYE in first round")
        Assertions.assertTrue(matchUps.contains(Pair("B1", "BYE")), "Expected to find match up B1 vs BYE in first round")
    }

    private fun List<PlayOffMatch>.assertNamesInRoundEqual(round: Round, expectedNames: List<String>) {
        val matches = this.filter { it.round == round }
        val actualPlaceholderNames = matches.flatMap {
            listOf(it.registrationOneId.toString(), it.registrationTwoId.toString())
        }.sorted()
        Assertions.assertEquals(expectedNames, actualPlaceholderNames)
    }
}