package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.interfaces.DrawType
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestCreateDrawMatchOrder {

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
    fun oneMatch() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..2).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        val result = createDraw.execute(competitionCategory.id) as PlayOffDrawSpec

        // Assert
        val match = result.matches.first()

        Assertions.assertEquals(1, match.order)
    }

    @Test
    fun twoMatches() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..4).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        val result = createDraw.execute(competitionCategory.id) as PlayOffDrawSpec

        // Assert
        val bestToWorst = registrationRanks.sortedBy { -it.rank }.map { Registration.Real(it.registrationId) }
        val matchWithRankOne = result.matches.first { it.contains(bestToWorst[0]) }
        val matchWithRankTwo = result.matches.first { it.contains(bestToWorst[1]) }

        Assertions.assertEquals(1, matchWithRankOne.order)
        Assertions.assertEquals(2, matchWithRankTwo.order)
    }

    @Test
    fun fourMatches() {
        val competitionCategory = dataGenerator.newCompetitionCategoryDTO(
            id = 33,
            settings = dataGenerator.newGeneralSettingsSpec(
                drawType = DrawType.CUP_ONLY,
                playersPerGroup = 3
            )
        )
        val registrationRanks = (1..8).toList().map {
            dataGenerator.newRegistrationRankDTO(competitionCategoryId = competitionCategory.id, rank = it)
        }
        Mockito.`when`(mockedFindCompetitionCategory.byId(competitionCategory.id)).thenReturn(competitionCategory)
        Mockito.`when`(mockedRegistrationRepository.getRegistrationRank(competitionCategory))
            .thenReturn(registrationRanks)

        // Act
        val result = createDraw.execute(competitionCategory.id) as PlayOffDrawSpec

        // Assert
        val bestToWorst = registrationRanks.sortedBy { -it.rank }.map { Registration.Real(it.registrationId) }
        val matchWithRankOne = result.matches.first { it.contains(bestToWorst[0]) }
        val matchWithRankTwo = result.matches.first { it.contains(bestToWorst[1]) }
        val matchWithRankThree = result.matches.first { it.contains(bestToWorst[2]) }
        val matchWithRankFour = result.matches.first { it.contains(bestToWorst[3]) }

        Assertions.assertEquals(1, matchWithRankOne.order)
        Assertions.assertTrue(matchWithRankFour.order <= 2, "Order was ${matchWithRankFour.order}")

        Assertions.assertTrue(matchWithRankTwo.order > 2, "Order was ${matchWithRankTwo.order}")
        Assertions.assertTrue(matchWithRankThree.order > 2, "Order was ${matchWithRankThree.order}")
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