package com.graphite.competitionplanner.draw

import com.graphite.competitionplanner.service.competition.CompetitionDrawUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestDrawsWhereOneProceeds(@Autowired val competitionDrawUtil: CompetitionDrawUtil) {

    @Test
    fun testGroupOf3() {
        val groups = listOf("A", "B", "C")

        val playoffPlan = competitionDrawUtil.playoffForGroupsWhereOneProceeds(groups)
        val matchUps = playoffPlan.matchUps
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("BYE", matchUps[0].player2)
        Assertions.assertEquals("C1", matchUps[1].player1)
        Assertions.assertEquals("B1", matchUps[1].player2)
    }

    @Test
    fun testGroupOf4() {
        val groups = listOf("A", "B", "C", "D")

        val playoffPlan = competitionDrawUtil.playoffForGroupsWhereOneProceeds(groups)
        val matchUps = playoffPlan.matchUps
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("D1", matchUps[0].player2)
        Assertions.assertEquals("C1", matchUps[1].player1)
        Assertions.assertEquals("B1", matchUps[1].player2)
    }

    @Test
    fun testGroupOf5() {
        val groups = listOf("A", "B", "C", "D", "E")

        val playoffPlan = competitionDrawUtil.playoffForGroupsWhereOneProceeds(groups)
        val matchUps = playoffPlan.matchUps
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("BYE", matchUps[0].player2)

        Assertions.assertEquals("E1", matchUps[1].player1)
        Assertions.assertEquals("D1", matchUps[1].player2)

        Assertions.assertEquals("C1", matchUps[2].player1)
        Assertions.assertEquals("BYE", matchUps[2].player2)

        Assertions.assertEquals("BYE", matchUps[3].player1)
        Assertions.assertEquals("B1", matchUps[3].player2)
    }

    @Test
    fun testGroupOf7() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G")

        val playoffPlan = competitionDrawUtil.playoffForGroupsWhereOneProceeds(groups)
        val matchUps = playoffPlan.matchUps
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("BYE", matchUps[0].player2)

        Assertions.assertEquals("E1", matchUps[1].player1)
        Assertions.assertEquals("D1", matchUps[1].player2)

        Assertions.assertEquals("C1", matchUps[2].player1)
        Assertions.assertEquals("F1", matchUps[2].player2)

        Assertions.assertEquals("G1", matchUps[3].player1)
        Assertions.assertEquals("B1", matchUps[3].player2)
    }

    @Test
    fun testGroupOf8() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H")

        val playoffPlan = competitionDrawUtil.playoffForGroupsWhereOneProceeds(groups)
        val matchUps = playoffPlan.matchUps
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("H1", matchUps[0].player2)

        Assertions.assertEquals("E1", matchUps[1].player1)
        Assertions.assertEquals("D1", matchUps[1].player2)

        Assertions.assertEquals("C1", matchUps[2].player1)
        Assertions.assertEquals("F1", matchUps[2].player2)

        Assertions.assertEquals("G1", matchUps[3].player1)
        Assertions.assertEquals("B1", matchUps[3].player2)
    }

    @Test
    fun testGroupOf9() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I")

        val playoffPlan = competitionDrawUtil.playoffForGroupsWhereOneProceeds(groups)
        val matchUps = playoffPlan.matchUps
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("BYE", matchUps[0].player2)

        Assertions.assertEquals("BYE", matchUps[1].player1)
        Assertions.assertEquals("E1", matchUps[1].player2)

        Assertions.assertEquals("D1", matchUps[2].player1)
        Assertions.assertEquals("BYE", matchUps[2].player2)

        Assertions.assertEquals("H1", matchUps[3].player1)
        Assertions.assertEquals("I1", matchUps[3].player2)

        Assertions.assertEquals("G1", matchUps[4].player1)
        Assertions.assertEquals("BYE", matchUps[4].player2)

        Assertions.assertEquals("C1", matchUps[5].player1)
        Assertions.assertEquals("BYE", matchUps[5].player2)

        Assertions.assertEquals("F1", matchUps[6].player1)
        Assertions.assertEquals("BYE", matchUps[6].player2)

        Assertions.assertEquals("BYE", matchUps[7].player1)
        Assertions.assertEquals("B1", matchUps[7].player2)
    }

    @Test
    fun testGroupOf13() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M")

        val playoffPlan = competitionDrawUtil.playoffForGroupsWhereOneProceeds(groups)
        val matchUps = playoffPlan.matchUps
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("BYE", matchUps[0].player2)

        Assertions.assertEquals("L1", matchUps[1].player1)
        Assertions.assertEquals("E1", matchUps[1].player2)

        Assertions.assertEquals("D1", matchUps[2].player1)
        Assertions.assertEquals("M1", matchUps[2].player2)

        Assertions.assertEquals("H1", matchUps[3].player1)
        Assertions.assertEquals("I1", matchUps[3].player2)

        Assertions.assertEquals("G1", matchUps[4].player1)
        Assertions.assertEquals("J1", matchUps[4].player2)

        Assertions.assertEquals("C1", matchUps[5].player1)
        Assertions.assertEquals("BYE", matchUps[5].player2)

        Assertions.assertEquals("F1", matchUps[6].player1)
        Assertions.assertEquals("K1", matchUps[6].player2)

        Assertions.assertEquals("BYE", matchUps[7].player1)
        Assertions.assertEquals("B1", matchUps[7].player2)
    }
}