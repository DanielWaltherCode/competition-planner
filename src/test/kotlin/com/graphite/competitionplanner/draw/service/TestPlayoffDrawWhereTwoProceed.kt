package com.graphite.competitionplanner.draw.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPlayoffDrawWhereTwoProceed(
    @Autowired val drawUtil: DrawUtilTwoProceed
) {

    val BYE = "BYE"

    @Test
    fun test2Groups() {
        val groups = listOf("A", "B")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)

        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("B2", matchUps[0].player2)
        Assertions.assertEquals("A2", matchUps[1].player1)
        Assertions.assertEquals("B1", matchUps[1].player2)

    }

    @Test
    fun test3Groups() {
        val groups = listOf("A", "B", "C")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)

        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals(BYE, matchUps[0].player2)
        Assertions.assertEquals("B2", matchUps[1].player1)
        Assertions.assertEquals("C2", matchUps[1].player2)
        Assertions.assertEquals("A2", matchUps[2].player1)
        Assertions.assertEquals("C1", matchUps[2].player2)
        Assertions.assertEquals(BYE, matchUps[3].player1)
        Assertions.assertEquals("B1", matchUps[3].player2)
    }

    @Test
    fun test4Groups() {
        val groups = listOf("A", "B", "C", "D")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)

        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("C2", matchUps[0].player2)
        Assertions.assertEquals("D1", matchUps[1].player1)
        Assertions.assertEquals("B2", matchUps[1].player2)
        Assertions.assertEquals("A2", matchUps[2].player1)
        Assertions.assertEquals("C1", matchUps[2].player2)
        Assertions.assertEquals("D2", matchUps[3].player1)
        Assertions.assertEquals("B1", matchUps[3].player2)
    }

    @Test
    fun test5Groups() {
        val groups = listOf("A", "B", "C", "D", "E")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)

        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals(BYE, matchUps[0].player2)
        Assertions.assertEquals("B2", matchUps[1].player1)
        Assertions.assertEquals("E2", matchUps[1].player2)
        Assertions.assertEquals("D1", matchUps[2].player1)
        Assertions.assertEquals(BYE, matchUps[2].player2)
        Assertions.assertEquals("C2", matchUps[3].player1)
        Assertions.assertEquals(BYE, matchUps[3].player2)
        Assertions.assertEquals(BYE, matchUps[4].player1)
        Assertions.assertEquals("E1", matchUps[4].player2)
        Assertions.assertEquals(BYE, matchUps[5].player1)
        Assertions.assertEquals("C1", matchUps[5].player2)
        Assertions.assertEquals("A2", matchUps[6].player1)
        Assertions.assertEquals("D2", matchUps[6].player2)
        Assertions.assertEquals(BYE, matchUps[7].player1)
        Assertions.assertEquals("B1", matchUps[7].player2)
    }

    @Test
    fun test6Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)

        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals(BYE, matchUps[0].player2)
        Assertions.assertEquals("B2", matchUps[1].player1)
        Assertions.assertEquals("E2", matchUps[1].player2)
        Assertions.assertEquals("D1", matchUps[2].player1)
        Assertions.assertEquals(BYE, matchUps[2].player2)
        Assertions.assertEquals("F1", matchUps[3].player1)
        Assertions.assertEquals("C2", matchUps[3].player2)
        Assertions.assertEquals("F2", matchUps[4].player1)
        Assertions.assertEquals("E1", matchUps[4].player2)
        Assertions.assertEquals(BYE, matchUps[5].player1)
        Assertions.assertEquals("C1", matchUps[5].player2)
        Assertions.assertEquals("A2", matchUps[6].player1)
        Assertions.assertEquals("D2", matchUps[6].player2)
        Assertions.assertEquals(BYE, matchUps[7].player1)
        Assertions.assertEquals("B1", matchUps[7].player2)
    }

    @Test
    fun test7Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    @Test
    fun test8Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    @Test
    fun test9Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)

        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals(BYE, matchUps[0].player2)
        Assertions.assertEquals("I2", matchUps[1].player1)
        Assertions.assertEquals("E2", matchUps[1].player2)
        Assertions.assertEquals("H1", matchUps[2].player1)
        Assertions.assertEquals(BYE, matchUps[2].player2)
        Assertions.assertEquals("G2", matchUps[3].player1)
        Assertions.assertEquals(BYE, matchUps[3].player2)
        Assertions.assertEquals("D1", matchUps[4].player1)
        Assertions.assertEquals(BYE, matchUps[4].player2)
        Assertions.assertEquals("C2", matchUps[5].player1)
        Assertions.assertEquals(BYE, matchUps[5].player2)
        Assertions.assertEquals("B2", matchUps[6].player1)
        Assertions.assertEquals(BYE, matchUps[6].player2)
        Assertions.assertEquals("F1", matchUps[7].player1)
        Assertions.assertEquals(BYE, matchUps[7].player2)
        Assertions.assertEquals(BYE, matchUps[8].player1)
        Assertions.assertEquals("E1", matchUps[8].player2)
        Assertions.assertEquals(BYE, matchUps[9].player1)
        Assertions.assertEquals("F2", matchUps[9].player2)
        Assertions.assertEquals(BYE, matchUps[10].player1)
        Assertions.assertEquals("C1", matchUps[10].player2)
        Assertions.assertEquals(BYE, matchUps[11].player1)
        Assertions.assertEquals("A2", matchUps[11].player2)
        Assertions.assertEquals(BYE, matchUps[12].player1)
        Assertions.assertEquals("G1", matchUps[12].player2)
        Assertions.assertEquals(BYE, matchUps[13].player1)
        Assertions.assertEquals("I1", matchUps[13].player2)
        Assertions.assertEquals("H2", matchUps[14].player1)
        Assertions.assertEquals("D2", matchUps[14].player2)
        Assertions.assertEquals(BYE, matchUps[15].player1)
        Assertions.assertEquals("B1", matchUps[15].player2)
    }

    @Test
    fun test10Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    @Test
    fun test11Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    @Test
    fun test12Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    @Test
    fun test13Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    @Test
    fun test14Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    @Test
    fun test15Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)

        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals(BYE, matchUps[0].player2)
        Assertions.assertEquals("L1", matchUps[1].player1)
        Assertions.assertEquals("E2", matchUps[1].player2)
        Assertions.assertEquals("H1", matchUps[2].player1)
        Assertions.assertEquals("M2", matchUps[2].player2)
        Assertions.assertEquals("O1", matchUps[3].player1)
        Assertions.assertEquals("K2", matchUps[3].player2)
        Assertions.assertEquals("D1", matchUps[4].player1)
        Assertions.assertEquals("G2", matchUps[4].player2)
        Assertions.assertEquals("N1", matchUps[5].player1)
        Assertions.assertEquals("B2", matchUps[5].player2)
        Assertions.assertEquals("J1", matchUps[6].player1)
        Assertions.assertEquals("I2", matchUps[6].player2)
        Assertions.assertEquals("F1", matchUps[7].player1)
        Assertions.assertEquals("C2", matchUps[7].player2)
        Assertions.assertEquals("N2", matchUps[8].player1)
        Assertions.assertEquals("E1", matchUps[8].player2)
        Assertions.assertEquals("F2", matchUps[9].player1)
        Assertions.assertEquals("K1", matchUps[9].player2)
        Assertions.assertEquals("O2", matchUps[10].player1)
        Assertions.assertEquals("C1", matchUps[10].player2)
        Assertions.assertEquals("J2", matchUps[11].player1)
        Assertions.assertEquals("M1", matchUps[11].player2)
        Assertions.assertEquals("L2", matchUps[12].player1)
        Assertions.assertEquals("G1", matchUps[12].player2)
        Assertions.assertEquals("A2", matchUps[13].player1)
        Assertions.assertEquals("I1", matchUps[13].player2)
        Assertions.assertEquals("H2", matchUps[14].player1)
        Assertions.assertEquals("D2", matchUps[14].player2)
        Assertions.assertEquals(BYE, matchUps[15].player1)
        Assertions.assertEquals("B1", matchUps[15].player2)
    }

    @Test
    fun test16Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "P")
        val matchUps = drawUtil.playoffDrawWhereTwoProceed(groups)
        testDrawSidesConsistency(matchUps)
    }

    // Ensure no duplicates on each draw side
    // Ensure same groups and nrByes are present on both sides
    private fun testDrawSidesConsistency(matchUps: List<MatchUp>) {
        val firstHalf = matchUps.subList(0, matchUps.size / 2)
        val secondHalf = matchUps.subList(matchUps.size / 2, matchUps.size)

        checkForDrawHalfDuplicates(firstHalf)
        checkForDrawHalfDuplicates(secondHalf)
        testSidesAreBalanced(firstHalf, secondHalf)
    }

    private fun checkForDrawHalfDuplicates(matchUps: List<MatchUp>) {
        val players = mutableListOf<String>()
        for (matchUp in matchUps) {
            if (matchUp.player1 != "BYE") {
                players.add(matchUp.player1)
            }
            if (matchUp.player2 != "BYE") {
                players.add(matchUp.player2)
            }
        }
        // Collect groups names (A, C, etc) and ensure that each group name
        // appears only once on this side of draw
        val groups = players.map { it[0] }
        val groupsAsSet = groups.toSet()
        Assertions.assertEquals(groups.size, groupsAsSet.size)
    }

    // Sides should contain same groups and same number of Byes
    private fun testSidesAreBalanced(firstHalf: List<MatchUp>, secondHalf: List<MatchUp>) {
        val playersFirstHalf = mutableListOf<String>()
        val playersSecondHalf = mutableListOf<String>()
        for (matchUp in firstHalf) {
            playersFirstHalf.add(matchUp.player1)
            playersFirstHalf.add(matchUp.player2)
        }
        for (matchUp in secondHalf) {
            playersSecondHalf.add(matchUp.player1)
            playersSecondHalf.add(matchUp.player2)
        }
        testOnesAndTwosAddUp(playersFirstHalf, playersSecondHalf)
        val firstHalfGroups = playersFirstHalf.map { it[0] }.sorted()
        val secondHalfGroups = playersSecondHalf.map { it[0] }.sorted()

        Assertions.assertEquals(firstHalfGroups, secondHalfGroups)
    }

    private fun testOnesAndTwosAddUp(
        playersFirstHalf: List<String>,
        playersSecondHalf: List<String>
    ) {
        val firstHalfPositions = playersFirstHalf.filter { it != BYE }.map { it[1] }
        val secondHalfPositions = playersSecondHalf.filter { it != BYE }.map { it[1] }

        val ones = mutableListOf<Char>()
        val twos = mutableListOf<Char>()

        for (position in firstHalfPositions) {
            if (position == '1') {
                ones.add(position)
            } else if (position == '2') {
                twos.add(position)
            } else {
                throw Exception("Something is seriously wrong with playoff draw!")
            }
        }
        for (position in secondHalfPositions) {
            if (position == '1') {
                ones.add(position)
            } else if (position == '2') {
                twos.add(position)
            } else {
                throw Exception("Something is seriously wrong with playoff draw!")
            }
        }
        Assertions.assertEquals(ones.size, twos.size)

    }
}