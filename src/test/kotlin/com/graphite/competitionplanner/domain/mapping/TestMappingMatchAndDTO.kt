package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.domain.dto.ClubDTO
import com.graphite.competitionplanner.domain.dto.MatchDTO
import com.graphite.competitionplanner.domain.dto.PlayerDTO
import com.graphite.competitionplanner.domain.entity.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class TestMappingMatchAndDTO {

    @Test
    fun testConvertDtoToEntity() {
        val p1 = PlayerDTO(
            111, "Åke", "Isaksson",
            ClubDTO(2, "Lyseskil", "Bakgården 1"),
            LocalDate.of(2001, 3, 12)
        )

        val p2 = PlayerDTO(
            222, "Lars", "Nilsson",
            ClubDTO(2, "Lyseskil", "Bakgården 1"),
            LocalDate.of(2001, 3, 12)
        )

        val dto = MatchDTO(
            1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), 1, "POOL",
            listOf(p1), listOf(p2), 1, "GROUP A"
        )

        val match = Match(dto)

        Assertions.assertEquals(dto.id, match.id)
        Assertions.assertEquals(dto.startTime, match.startTime)
        Assertions.assertEquals(dto.endTime, match.endTime)
        Assertions.assertEquals(dto.competitionCategoryId, match.competitionCategory.id)
        Assertions.assertEquals(dto.matchType, match.type.value)
        Assertions.assertEquals(dto.firstPlayer.first().id, match.firstPlayer.first().id)
        Assertions.assertEquals(dto.secondPlayer.first().id, match.secondPlayer.first().id)
        Assertions.assertEquals(dto.matchOrderNumber, match.orderNumber)
        Assertions.assertEquals(dto.groupOrRound, match.groupOrRound)
    }

    @Test
    fun testConvertEntityToDto() {
        val p1 = Player(
            33, "Test", "Testsson",
            Club(3, "ClubA", "Address 1"),
            LocalDate.of(1992, 10, 18)
        )

        val p2 = Player(
            44, "Isak", "Testsson",
            Club(3, "ClubA", "Address 1"),
            LocalDate.of(1992, 10, 18)
        )

        val match = Match(
            33, CompetitionCategory(33), LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), MatchType("PLAYOFF"),
            listOf(p1), listOf(p2), 11, "Round of 64"
        )

        val dto = MatchDTO(match)

        Assertions.assertEquals(match.id, dto.id)
        Assertions.assertEquals(match.startTime, dto.startTime)
        Assertions.assertEquals(match.endTime, dto.endTime)
        Assertions.assertEquals(match.competitionCategory.id, dto.competitionCategoryId)
        Assertions.assertEquals(match.type.value, dto.matchType)
        Assertions.assertEquals(match.firstPlayer.first().id, dto.firstPlayer.first().id)
        Assertions.assertEquals(match.secondPlayer.first().id, dto.secondPlayer.first().id)
        Assertions.assertEquals(match.orderNumber, dto.matchOrderNumber)
        Assertions.assertEquals(match.groupOrRound, dto.groupOrRound)
    }
}