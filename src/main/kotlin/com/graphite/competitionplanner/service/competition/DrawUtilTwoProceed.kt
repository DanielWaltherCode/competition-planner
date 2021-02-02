package com.graphite.competitionplanner.service.competition

import org.springframework.stereotype.Component

/**
 * Class for handling group/playoff draws
 * where two people proceed from each group proceed to playoff
 */
@Component
class DrawUtilTwoProceed(val drawUtil: DrawUtil) {

    fun playoffDrawWhereTwoProceed(groups: List<String> ): List<MatchUp> {
        val BYE = "BYE"
        val matchUps = mutableListOf<MatchUp>()

        if (groups.size == 2) {
            matchUps.add(MatchUp("A1", "B2"))
            matchUps.add(MatchUp("A2", "B1"))
        }
        // Up to 8 players in playoff
        else if(groups.size == 3) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("B2", "C2"))
            matchUps.add(MatchUp("A2", "C1"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 4) {
            matchUps.add(MatchUp("A1", "C2"))
            matchUps.add(MatchUp("D1", "B2"))
            matchUps.add(MatchUp("A2", "C1"))
            matchUps.add(MatchUp("D2", "B1"))
        }
        // Up to 16 players in playoff
        else if(groups.size == 5) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("B2", "E2"))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("C2", BYE))
            matchUps.add(MatchUp(BYE, "E1"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("A2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 6) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("B2", "E2"))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("F2", "E1"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("A2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 7) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("B2", "E2"))
            matchUps.add(MatchUp("D1", "G2"))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("F2", "E1"))
            matchUps.add(MatchUp("A2", "C1"))
            matchUps.add(MatchUp("D2", "G1"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 8) {
            matchUps.add(MatchUp("A1", "E2"))
            matchUps.add(MatchUp("H1", "B2"))
            matchUps.add(MatchUp("D1", "G2"))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("F2", "E1"))
            matchUps.add(MatchUp("A2", "C1"))
            matchUps.add(MatchUp("D2", "G1"))
            matchUps.add(MatchUp("H2", "B1"))
        }
        else if(groups.size == 9) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("I2", "E2"))
            matchUps.add(MatchUp("H1", BYE))
            matchUps.add(MatchUp("G2", BYE))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("C2", BYE))
            matchUps.add(MatchUp("B2", BYE))
            matchUps.add(MatchUp("F1", BYE))
            matchUps.add(MatchUp(BYE, "E1"))
            matchUps.add(MatchUp(BYE, "F2"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp(BYE, "A2"))
            matchUps.add(MatchUp(BYE, "G1"))
            matchUps.add(MatchUp(BYE, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 10) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("I2", "E2"))
            matchUps.add(MatchUp("H1", BYE))
            matchUps.add(MatchUp("G2", BYE))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", BYE))
            matchUps.add(MatchUp("F1", BYE))
            matchUps.add(MatchUp(BYE, "E1"))
            matchUps.add(MatchUp(BYE, "F2"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("J2", "A2"))
            matchUps.add(MatchUp(BYE, "G1"))
            matchUps.add(MatchUp(BYE, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 11) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("I2", "E2"))
            matchUps.add(MatchUp("H1", BYE))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", BYE))
            matchUps.add(MatchUp("F1", BYE))
            matchUps.add(MatchUp(BYE, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("J2", "A2"))
            matchUps.add(MatchUp(BYE, "G1"))
            matchUps.add(MatchUp(BYE, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 12) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", BYE))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", BYE))
            matchUps.add(MatchUp(BYE, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("J2", "A2"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp(BYE, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 12) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", BYE))
            matchUps.add(MatchUp(BYE, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 13) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", BYE))
            matchUps.add(MatchUp(BYE, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 14) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", BYE))
            matchUps.add(MatchUp("N1", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("N2", "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(BYE, "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 15) {
            matchUps.add(MatchUp("A1", BYE))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("O1", "K2"))
            matchUps.add(MatchUp("D1", "G2"))
            matchUps.add(MatchUp("N1", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("N2", "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp("O2", "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(BYE, "B1"))
        }
        else if(groups.size == 16) {
            matchUps.add(MatchUp("A1", "P2"))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("O1", "K2"))
            matchUps.add(MatchUp("D1", "G2"))
            matchUps.add(MatchUp("N1", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("N2", "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp("O2", "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "P1"))
            matchUps.add(MatchUp("D2", "B1"))
        }
        return matchUps
    }
}