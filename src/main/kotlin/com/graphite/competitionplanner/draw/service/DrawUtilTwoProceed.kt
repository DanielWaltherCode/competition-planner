package com.graphite.competitionplanner.draw.service

import org.springframework.stereotype.Component

/**
 * Class for handling group/playoff draws
 * where two people proceed from each group proceed to playoff
 */
@Component
class DrawUtilTwoProceed {

    fun playoffDrawWhereTwoProceed(groups: List<String> ): List<MatchUp> {
        val bye = "BYE"
        val matchUps = mutableListOf<MatchUp>()

        if (groups.size == 2) {
            matchUps.add(MatchUp("A1", "B2"))
            matchUps.add(MatchUp("A2", "B1"))
        }
        // Up to 8 players in playoff
        else if(groups.size == 3) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("B2", "C2"))
            matchUps.add(MatchUp("A2", "C1"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 4) {
            matchUps.add(MatchUp("A1", "C2"))
            matchUps.add(MatchUp("D1", "B2"))
            matchUps.add(MatchUp("A2", "C1"))
            matchUps.add(MatchUp("D2", "B1"))
        }
        // Up to 16 players in playoff
        else if(groups.size == 5) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("B2", "E2"))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("C2", bye))
            matchUps.add(MatchUp(bye, "E1"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("A2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 6) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("B2", "E2"))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("F2", "E1"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("A2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 7) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("B2", "E2"))
            matchUps.add(MatchUp("D1", "G2"))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("F2", "E1"))
            matchUps.add(MatchUp("A2", "C1"))
            matchUps.add(MatchUp("D2", "G1"))
            matchUps.add(MatchUp(bye, "B1"))
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
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("I2", "E2"))
            matchUps.add(MatchUp("H1", bye))
            matchUps.add(MatchUp("G2", bye))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("C2", bye))
            matchUps.add(MatchUp("B2", bye))
            matchUps.add(MatchUp("F1", bye))
            matchUps.add(MatchUp(bye, "E1"))
            matchUps.add(MatchUp(bye, "F2"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp(bye, "A2"))
            matchUps.add(MatchUp(bye, "G1"))
            matchUps.add(MatchUp(bye, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 10) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("I2", "E2"))
            matchUps.add(MatchUp("H1", bye))
            matchUps.add(MatchUp("G2", bye))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", bye))
            matchUps.add(MatchUp("F1", bye))
            matchUps.add(MatchUp(bye, "E1"))
            matchUps.add(MatchUp(bye, "F2"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("J2", "A2"))
            matchUps.add(MatchUp(bye, "G1"))
            matchUps.add(MatchUp(bye, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 11) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("I2", "E2"))
            matchUps.add(MatchUp("H1", bye))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", bye))
            matchUps.add(MatchUp("F1", bye))
            matchUps.add(MatchUp(bye, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("J2", "A2"))
            matchUps.add(MatchUp(bye, "G1"))
            matchUps.add(MatchUp(bye, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 12) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", bye))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", bye))
            matchUps.add(MatchUp(bye, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("J2", "A2"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp(bye, "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 12) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", bye))
            matchUps.add(MatchUp(bye, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 13) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("C2", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", bye))
            matchUps.add(MatchUp(bye, "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 14) {
            matchUps.add(MatchUp("A1", bye))
            matchUps.add(MatchUp("L1", "E2"))
            matchUps.add(MatchUp("H1", "M2"))
            matchUps.add(MatchUp("G2", "K2"))
            matchUps.add(MatchUp("D1", bye))
            matchUps.add(MatchUp("N1", "B2"))
            matchUps.add(MatchUp("J1", "I2"))
            matchUps.add(MatchUp("F1", "C2"))
            matchUps.add(MatchUp("N2", "E1"))
            matchUps.add(MatchUp("F2", "K1"))
            matchUps.add(MatchUp(bye, "C1"))
            matchUps.add(MatchUp("J2", "M1"))
            matchUps.add(MatchUp("L2", "G1"))
            matchUps.add(MatchUp("A2", "I1"))
            matchUps.add(MatchUp("H2", "D2"))
            matchUps.add(MatchUp(bye, "B1"))
        }
        else if(groups.size == 15) {
            matchUps.add(MatchUp("A1", bye))
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
            matchUps.add(MatchUp(bye, "B1"))
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