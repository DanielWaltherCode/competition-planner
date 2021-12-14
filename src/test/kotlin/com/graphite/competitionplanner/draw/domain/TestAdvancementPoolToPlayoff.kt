package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.draw.interfaces.Round
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.registration.domain.GetRegistrationsInCompetitionCategory
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest

/**
 * A base class for tests related to mapping of pools to playoff. Contains configurations of mocks and helper functions
 * for asserting that the pools are mapped to the correct playoff matches.
 */
@SpringBootTest
class TestAdvancementPoolToPlayoff {

    private val mockedGetRegistrationInCompetitionCategory =
        Mockito.mock(GetRegistrationsInCompetitionCategory::class.java)
    protected final val mockedFindCompetitionCategory: FindCompetitionCategory = Mockito.mock(FindCompetitionCategory::class.java)
    protected final val mockedRegistrationRepository: IRegistrationRepository = Mockito.mock(IRegistrationRepository::class.java)
    private val mockedSeedRepository = Mockito.mock(ISeedRepository::class.java)
    protected final val mockedCompetitionDrawRepository: ICompetitionDrawRepository = Mockito.mock(ICompetitionDrawRepository::class.java)

    protected final val dataGenerator = DataGenerator()

    @Captor
    lateinit var classCaptor: ArgumentCaptor<CompetitionCategoryDrawSpec>

    protected val createDraw = CreateDraw(
        mockedGetRegistrationInCompetitionCategory,
        mockedFindCompetitionCategory,
        CreateSeed(),
        mockedRegistrationRepository,
        mockedSeedRepository,
        mockedCompetitionDrawRepository
    )

    /**
     * Assert that the given match up exist.
     */
    protected fun List<Pair<String, String>>.assertMatchUpExist(expected: Pair<String, String>) {
        Assertions.assertTrue(this.contains(expected), "Expected to find match up ${expected.first} vs ${expected.second}. All match ups: $this")
    }

    /**
     * Assert that all the expected names exist in the given round
     */
    protected fun List<PlayOffMatch>.assertNamesInRoundEqual(round: Round, expectedNames: List<String>) {
        val matches = this.inRound(round)
        val actualPlaceholderNames = matches.flatMap {
            listOf(it.registrationOneId.toString(), it.registrationTwoId.toString())
        }.sorted()
        Assertions.assertEquals(expectedNames.sorted(), actualPlaceholderNames)
    }

    /**
     * Return all the matches in the given round
     */
    protected fun List<PlayOffMatch>.inRound(round: Round): List<PlayOffMatch> {
        return this.filter { it.round == round }
    }

    /**
     * Return the match with a placeholder with the given name
     */
    protected fun List<PlayOffMatch>.findMatchWith(name: String): PlayOffMatch {
        return this.first { it.registrationOneId.toString() == name || it.registrationTwoId.toString() == name }
    }
}