package com.graphite.competitionplanner.domain.entity

import com.graphite.competitionplanner.domain.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.lang.IllegalArgumentException
import java.time.LocalDateTime

@SpringBootTest
class TestMatch {

    private final val dataGenerator = DataGenerator()
    private final val p1 = dataGenerator.newPlayer()
    private final val p2 = dataGenerator.newPlayer()

//    @Test
//    TODO: Consider having two different data classes depending on whether a match has been sceduled or not
//    fun startTimeCannotBeAfterEndTime() {
//        Assertions.assertThrows(IllegalArgumentException::class.java) {
//            Match(
//                0,
//                LocalDateTime.now(),
//                LocalDateTime.now().minusMinutes(10),
//                CompetitionCategory(0),
//                MatchType("POOL"),
//                listOf(p1),
//                listOf(p2),
//                0,
//                "Group A"
//            )
//        }
//    }

    @Test
    fun playerCannotBelongToBothTeamsInSameMatch() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            Match(
                0,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10),
                CompetitionCategory(0),
                MatchType("POOL"),
                listOf(p2),
                listOf(p2),
                0,
                "Group A"
            )
        }
    }
}