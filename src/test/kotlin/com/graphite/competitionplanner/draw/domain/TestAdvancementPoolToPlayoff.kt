package com.graphite.competitionplanner.draw.domain

import com.graphite.competitionplanner.draw.interfaces.Round
import org.junit.jupiter.api.Assertions
import org.springframework.boot.test.context.SpringBootTest

/**
 * A base class for tests related to mapping of pools to playoff. Contains configurations of mocks and helper functions
 * for asserting that the pools are mapped to the correct playoff matches.
 */
@SpringBootTest
class TestAdvancementPoolToPlayoff: TestBaseCreateDraw() {

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