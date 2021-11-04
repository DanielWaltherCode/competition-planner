package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.domain.entity.Round
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
class TestCreateDrawCupOnlyPlaceholderMatches {

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
    fun shouldGenerateTheCorrectNumberOfMatchesPerRound() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..128).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PlayOffDrawSpec

        // Assert
        result.matches.assertCorrectNumberOfMatchesInRound(Round.FINAL, 1)
        result.matches.assertCorrectNumberOfMatchesInRound(Round.SEMI_FINAL, 2)
        result.matches.assertCorrectNumberOfMatchesInRound(Round.QUARTER_FINAL, 4)
        result.matches.assertCorrectNumberOfMatchesInRound(Round.ROUND_OF_16, 8)
        result.matches.assertCorrectNumberOfMatchesInRound(Round.ROUND_OF_32, 16)
        result.matches.assertCorrectNumberOfMatchesInRound(Round.ROUND_OF_64, 32)
        result.matches.assertCorrectNumberOfMatchesInRound(Round.ROUND_OF_128, 64)
    }

    fun List<PlayOffMatch>.assertCorrectNumberOfMatchesInRound(round: Round, expected: Int) {
        Assertions.assertEquals(this.filter { it.round == round }.size, expected)
    }

    @Test
    fun shouldAssignCorrectMatchOrderToPlaceHolderMatches() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..128).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PlayOffDrawSpec

        // Assert
        result.matches.assertMatchOrdersExist(Round.FINAL, (1..1).toList())
        result.matches.assertMatchOrdersExist(Round.SEMI_FINAL, (1..2).toList())
        result.matches.assertMatchOrdersExist(Round.QUARTER_FINAL, (1..4).toList())
        result.matches.assertMatchOrdersExist(Round.ROUND_OF_16, (1..8).toList())
        result.matches.assertMatchOrdersExist(Round.ROUND_OF_32, (1..16).toList())
        result.matches.assertMatchOrdersExist(Round.ROUND_OF_64, (1..32).toList())
    }

    fun List<PlayOffMatch>.assertMatchOrdersExist(round: Round, orders: List<Int>) {
        val matchesOrdersInRound = this.filter { it.round == round }.map { it.order }
        Assertions.assertEquals(orders.size, matchesOrdersInRound.size)
        Assertions.assertEquals(orders.size, orders.intersect(matchesOrdersInRound).size)
    }
}