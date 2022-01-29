package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
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
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawPoolSizes {

    private val mockedGetRegistrationInCompetitionCategory = mock(GetRegistrationsInCompetitionCategory::class.java)
    private val mockedFindCompetitionCategory = mock(FindCompetitionCategory::class.java)
    private val mockedRegistrationRepository = mock(IRegistrationRepository::class.java)
    private val mockedSeedRepository = mock(ISeedRepository::class.java)
    private val mockedCompetitionDrawRepository = mock(ICompetitionDrawRepository::class.java)
    private val mockedCompetitionCategoryRepository = mock(ICompetitionCategoryRepository::class.java)

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
    fun tooFewRegistrations() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_ONLY,
                playersPerGroup = 4
            )
        )
        val registrationRanks = (1..1).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act & Assert
        Assertions.assertThrows(NotEnoughRegistrationsException::class.java) {
            createDraw.execute(competitionCategory.id) // Need at least two registrations to make a valid pool
        }
    }

    @Test
    fun competitionWithTenPlayersAndGroupsOfFour() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4
            )
        )
        val registrationRanks = (1..10).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(3, result.pools.size)
        Assertions.assertEquals(2, result.pools.filter { it.registrationIds.size == 3 }.size,
            "Expected to find 2 groups with 3 registrations in each")
        Assertions.assertEquals(1, result.pools.filter { it.registrationIds.size == 4 }.size,
            "Expected to find 1 group with 4 registrations in each")
    }

    @Test
    fun competitionWithNinePlayersAndGroupsOfFive() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 5
            )
        )
        val registrationRanks = (1..9).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(2, result.pools.size)
        Assertions.assertEquals(1, result.pools.filter { it.registrationIds.size == 4 }.size,
            "Expected to find 1 group with 4 registrations in each")
        Assertions.assertEquals(1, result.pools.filter { it.registrationIds.size == 5 }.size,
            "Expected to find 1 group with 5 registrations in each")
    }

    @Test
    fun competitionWithFifteenPlayersAndGroupsOfThree() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..15).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(5, result.pools.size)
        Assertions.assertEquals(5, result.pools.filter { it.registrationIds.size == 3 }.size,
            "Expected to find 5 groups with 3 registrations in each")
    }

    @Test
    fun competitionWithSixteenPlayersAndGroupsOfThree() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..16).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(6, result.pools.size)
        Assertions.assertEquals(4, result.pools.filter { it.registrationIds.size == 3 }.size,
            "Expected to find 4 groups with 3 registrations in each")

        Assertions.assertEquals(2, result.pools.filter { it.registrationIds.size == 2 }.size,
            "Expected to find 2 groups with 2 registrations in each")

    }

    @Test
    fun competitionWithThreePlayersAndGroupsOfThree() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..3).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(1, result.pools.size)
        Assertions.assertEquals(1, result.pools.filter { it.registrationIds.size == 3 }.size,
            "Expected to find 1 groups with 3 registrations in each")
    }

    @Test
    fun competitionWithThreePlayersAndGroupsOfFive() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 5
            )
        )
        val registrationRanks = (1..3).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(1, result.pools.size)
        Assertions.assertEquals(1, result.pools.filter { it.registrationIds.size == 3 }.size,
            "Expected to find 1 groups with 3 registrations in each")
    }

    @Test
    fun competitionWithSeventyThreePlayersAndGroupsOfFour() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.POOL_AND_CUP,
                playersPerGroup = 4
            )
        )
        val registrationRanks = (1..73).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        `when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        `when`(mockedRegistrationRepository.getRegistrationRanking(competitionCategory)).thenReturn(registrationRanks)

        // Act
        createDraw.execute(competitionCategory.id)

        // Record the spec sent to the repository for validation
        Mockito.verify(mockedCompetitionDrawRepository).store(TestHelper.MockitoHelper.capture(classCaptor))
        val result = classCaptor.value as PoolAndCupDrawSpec

        // Assert
        Assertions.assertEquals(19, result.pools.size)
        Assertions.assertEquals(16, result.pools.filter { it.registrationIds.size == 4 }.size,
            "Expected to find 19 groups with 3 registrations in each")
        Assertions.assertEquals(3, result.pools.filter { it.registrationIds.size == 3 }.size,
            "Expected to find 2 groups with 2 registrations in each")
    }
}