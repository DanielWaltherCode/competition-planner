package com.graphite.competitionplanner.domain.mapping

import com.graphite.competitionplanner.domain.entity.Match
import com.graphite.competitionplanner.domain.entity.MatchType
import com.graphite.competitionplanner.domain.entity.Player
import com.graphite.competitionplanner.schedule.domain.interfaces.MatchDTO
import com.graphite.competitionplanner.util.DataGenerator
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class TestMappingMatchAndDTO {

    val dataGenerator = DataGenerator()

    @Test
    fun testConvertDtoToEntity() {
        val p1 = dataGenerator.newPlayerDTO(
            id = 111,
            firstName = "Åke",
            lastName = "Isaksson",
            clubId = 1,
            dateOfBirth = LocalDate.of(2001, 3, 12)
        )

        val p2 = dataGenerator.newPlayerDTO(
            id = 222,
            firstName = "Lars",
            lastName = "Nilsson",
            clubId = 2,
            dateOfBirth = LocalDate.of(2001, 3, 12)
        )

        val dto = MatchDTO(
            1, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), 1, "POOL",
            listOf(p1.id), listOf(p2.id), 1, "GROUP A"
        )

        val match = Match(dto)

        Assertions.assertEquals(dto.id, match.id)
        Assertions.assertEquals(dto.startTime, match.startTime)
        Assertions.assertEquals(dto.endTime, match.endTime)
        Assertions.assertEquals(dto.competitionCategoryId, match.competitionCategoryId)
        Assertions.assertEquals(dto.matchType, match.type.value)
        Assertions.assertEquals(dto.firstPlayer.first(), match.firstTeamPlayerIds.first())
        Assertions.assertEquals(dto.secondPlayer.first(), match.secondTeamPlayerIds.first())
        Assertions.assertEquals(dto.matchOrderNumber, match.orderNumber)
        Assertions.assertEquals(dto.groupOrRound, match.groupOrRound)
    }

    @Test
    fun testConvertEntityToDto() {
        val club = dataGenerator.newClub()
        val p1 = Player(
            33, "Test", "Testsson",
            LocalDate.of(1992, 10, 18)
        )
        p1.club = club

        val p2 = Player(
            44, "Isak", "Testsson",
            LocalDate.of(1992, 10, 18)
        )
        p2.club = club

        val match = Match(
            33, 33, LocalDateTime.now(), LocalDateTime.now().plusMinutes(15), MatchType("PLAYOFF"),
            listOf(p1.id), listOf(p2.id), 11, "Round of 64"
        )
        val dto = MatchDTO(match)

        Assertions.assertEquals(match.id, dto.id)
        Assertions.assertEquals(match.startTime, dto.startTime)
        Assertions.assertEquals(match.endTime, dto.endTime)
        Assertions.assertEquals(match.competitionCategoryId, dto.competitionCategoryId)
        Assertions.assertEquals(match.type.value, dto.matchType)
        Assertions.assertEquals(match.firstTeamPlayerIds.first(), dto.firstPlayer.first())
        Assertions.assertEquals(match.secondTeamPlayerIds.first(), dto.secondPlayer.first())
        Assertions.assertEquals(match.orderNumber, dto.matchOrderNumber)
        Assertions.assertEquals(match.groupOrRound, dto.groupOrRound)
    }
}