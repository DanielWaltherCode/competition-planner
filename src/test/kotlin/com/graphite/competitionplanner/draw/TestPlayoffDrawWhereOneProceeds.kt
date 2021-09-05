package com.graphite.competitionplanner.draw

import com.graphite.competitionplanner.service.draw.DrawUtil
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestPlayoffDrawWhereOneProceeds(@Autowired val drawUtil: DrawUtil) {

    @Test
    fun test3Groups() {
        val groups = listOf("A", "B", "C")

        val matchUps = drawUtil.playoffForGroupsWhereOneProceeds(groups)
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("BYE", matchUps[0].player2)
        Assertions.assertEquals("C1", matchUps[1].player1)
        Assertions.assertEquals("B1", matchUps[1].player2)
    }

    @Test
    fun test4Groups() {
        val groups = listOf("A", "B", "C", "D")

        val matchUps = drawUtil.playoffForGroupsWhereOneProceeds(groups)
        Assertions.assertEquals("A1", matchUps[0].player1)
        Assertions.assertEquals("D1", matchUps[0].player2)
        Assertions.assertEquals("C1", matchUps[1].player1)
        Assertions.assertEquals("B1", matchUps[1].player2)
    }

    @Test
    fun test5Groups() {
        val groups = listOf("A", "B", "C", "D", "E")

        val matchUps = drawUtil.playoffForGroupsWhereOneProceeds(groups)
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
    fun test7Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G")

        val matchUps = drawUtil.playoffForGroupsWhereOneProceeds(groups)
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
    fun test8Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H")

        val matchUps = drawUtil.playoffForGroupsWhereOneProceeds(groups)
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
    fun test9Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I")

        val matchUps = drawUtil.playoffForGroupsWhereOneProceeds(groups)
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
    fun test13Groups() {
        val groups = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M")

        val matchUps = drawUtil.playoffForGroupsWhereOneProceeds(groups)
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