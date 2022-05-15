package com.graphite.competitionplanner.schedule.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotSpec
import com.graphite.competitionplanner.schedule.interfaces.TimeTableSlotToMatch
import com.graphite.competitionplanner.schedule.interfaces.UpdateMatchToTimeTableSlotSpec
import com.graphite.competitionplanner.tables.records.MatchRecord
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest
class TestTimeTable @Autowired constructor(
    val repository: IScheduleRepository,
    clubRepository: IClubRepository,
    competitionRepository: ICompetitionRepository,
    competitionCategoryRepository: ICompetitionCategoryRepository,
    categoryRepository: ICategoryRepository,
    playerRepository: IPlayerRepository,
    registrationRepository: IRegistrationRepository,
    matchRepository: MatchRepository,
    resultRepository: IResultRepository
) : BaseRepositoryTest(
    clubRepository,
    competitionRepository,
    competitionCategoryRepository,
    categoryRepository,
    playerRepository,
    registrationRepository,
    matchRepository,
    resultRepository
) {

    @Test
    fun testGetTimeTable() {
        // Setup
        val (match1, match2, slot1, slot2, competition) = setupTestData()
        val updateSpec1 = listOf(
            UpdateMatchToTimeTableSlotSpec(match1.id, slot1.id),
            UpdateMatchToTimeTableSlotSpec(match2.id, slot1.id)
        )
        repository.updateMatchesTimeTablesSlots(updateSpec1)

        // Act
        val timeTable = repository.getTimeTable(competition.id)

        // Assert
        Assertions.assertEquals(3, timeTable.size, "Not the expected number of items")

        val zipWithNext = timeTable.zipWithNext()
        for ((s1, s2) in zipWithNext) {
            Assertions.assertTrue(s1.id <= s2.id, "The returned list is not sorted by TimeTableSlot id in ascending order")
        }
    }

    @Test
    fun testGetTimeTableSlotRecordsSortedByTimeAndTableNumber() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val locationMalmo = "Malmö Arena"

        var now = LocalDateTime.now()
        now = LocalDateTime.of(now.year, now.month, now.dayOfMonth, now.hour, now.minute, now.second)
        val slot1 = TimeTableSlotSpec(now, 1, locationMalmo)
        val slot2 = TimeTableSlotSpec(now.plusMinutes(10), 1, locationMalmo)
        val slot3 = TimeTableSlotSpec(now.plusMinutes(10), 2, locationMalmo)
        val slot4 = TimeTableSlotSpec(now.plusMinutes(10), 3, locationMalmo)
        val slot5 = TimeTableSlotSpec(now.plusMinutes(15), 1, locationMalmo)
        val slot6 = TimeTableSlotSpec(now.plusMinutes(15), 2, locationMalmo)

        repository.storeTimeTable(competition.id, listOf(slot6, slot5, slot1, slot2, slot3, slot4))

        // Act
        val result = repository.getTimeTableSlotRecords(competition.id, now.minusMinutes(1), listOf(1, 2, 3), locationMalmo)

        // Assert
        Assertions.assertTrue(slot1.startTime == result[0].startTime && slot1.tableNumber == result[0].tableNumber)
        Assertions.assertTrue(slot2.startTime == result[1].startTime && slot2.tableNumber == result[1].tableNumber)
        Assertions.assertTrue(slot3.startTime == result[2].startTime && slot3.tableNumber == result[2].tableNumber)
        Assertions.assertTrue(slot4.startTime == result[3].startTime && slot4.tableNumber == result[3].tableNumber)
        Assertions.assertTrue(slot5.startTime == result[4].startTime && slot5.tableNumber == result[4].tableNumber)
        Assertions.assertTrue(slot6.startTime == result[5].startTime && slot6.tableNumber == result[5].tableNumber)
    }

    @Test
    fun testGetTimeTableSlotRecords() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val locationMalmo = "Malmö Arena"
        val locationLund = "Lund Arena"

        val slot1 = TimeTableSlotSpec(LocalDateTime.now(), 1, locationMalmo)
        val slot2 = TimeTableSlotSpec(LocalDateTime.now().plusMinutes(30), 1, locationMalmo)
        val slot3 = TimeTableSlotSpec(LocalDateTime.now().plusMinutes(30), 2, locationMalmo)
        val slot4 = TimeTableSlotSpec(LocalDateTime.now(), 3, locationLund)

        repository.storeTimeTable(competition.id, listOf(slot1, slot2, slot3, slot4))

        // Act & Assert
        val result = repository.getTimeTableSlotRecords(-1, LocalDateTime.now().minusMinutes(10), listOf(1), locationMalmo)
        Assertions.assertEquals(0, result.size, "The competition id does not exist. Expected empty result.")

        assertCorrectRecordsReturned(3, competition.id, LocalDateTime.now().minusMinutes(10), listOf(1, 2), locationMalmo)
        assertCorrectRecordsReturned(2, competition.id, LocalDateTime.now().plusMinutes(10), listOf(1, 2), locationMalmo)
        assertCorrectRecordsReturned(2, competition.id, LocalDateTime.now().minusMinutes(10), listOf(1), locationMalmo)
        assertCorrectRecordsReturned(1, competition.id, LocalDateTime.now().minusMinutes(10), listOf(3), locationLund)
    }

    private fun assertCorrectRecordsReturned(
        expectedCount: Int,
        competitionId: Int,
        starTime: LocalDateTime,
        tableNumbers: List<Int>,
        location: String
    ) {
        val result = repository.getTimeTableSlotRecords(
            competitionId,
            starTime,
            tableNumbers,
            location
        )
        Assertions.assertEquals(expectedCount, result.size, "Wrong number of TimeTableSlots returned.")
        Assertions.assertTrue(
            result.all { it.location == location },
            "At least one TimeTableSlot is from the wrong location"
        )
        Assertions.assertTrue(
            result.all { tableNumbers.contains(it.tableNumber) },
            "At least one TimeTableSlot has the wrong table number"
        )
        Assertions.assertTrue(
            result.all { it.startTime >= starTime },
            "At least one TimeTableSlot starts before the given start time."
        )
    }

    @Test
    fun testAddMatchToTimeTableSlot() {
        // Setup
        val (match1, match2, slot1, _) = setupTestData()

        // Act
        val afterFirst = repository.addMatchToTimeTableSlot(slot1.id, match1.id)
        val afterSecond = repository.addMatchToTimeTableSlot(slot1.id, match2.id)

        // Assert
        Assertions.assertEquals(1, afterFirst.size, "Not the expected number of items")
        Assertions.assertEquals(2, afterSecond.size, "Not the expected number of items")
    }

    @Test
    fun testUpdateMatchesTimeTableSlots() {
        // Setup
        val (match1, match2, slot1, slot2, _) = setupTestData()

        val updateSpec1 = listOf(
            UpdateMatchToTimeTableSlotSpec(match1.id, slot1.id),
            UpdateMatchToTimeTableSlotSpec(match2.id, slot1.id)
        )

        val updateSpec2 = listOf(
            UpdateMatchToTimeTableSlotSpec(match1.id, slot2.id),
            UpdateMatchToTimeTableSlotSpec(match2.id, slot2.id)
        )

        // Act
        repository.updateMatchesTimeTablesSlots(updateSpec1)
        val match1AfterFirstUpdate = matchRepository.getMatch(match1.id)
        val match2AfterFirstUpdate = matchRepository.getMatch(match2.id)

        repository.updateMatchesTimeTablesSlots(updateSpec2)
        val match1AfterSecondUpdate = matchRepository.getMatch(match1.id)
        val match2AfterSecondUpdate = matchRepository.getMatch(match2.id)

        // Assert
        Assertions.assertEquals(slot1.id, match1AfterFirstUpdate.matchScheduleId)
        Assertions.assertEquals(slot1.id, match2AfterFirstUpdate.matchScheduleId)

        Assertions.assertEquals(slot2.id, match1AfterSecondUpdate.matchScheduleId)
        Assertions.assertEquals(slot2.id, match2AfterSecondUpdate.matchScheduleId)

        Assertions.assertEquals(match1.matchType, match1AfterSecondUpdate.matchType, "Wrong values updated!")
        Assertions.assertEquals(match2.matchType, match2AfterSecondUpdate.matchType, "Wrong values updated!")
    }

    data class TestData(
        val match1: MatchRecord,
        val match2: MatchRecord,
        val slot1: TimeTableSlotToMatch,
        val slot2: TimeTableSlotToMatch,
        val competitionId: CompetitionDTO
    )

    private fun setupTestData(): TestData {
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()
        val player1 = club.addPlayer("Sven")
        val player2 = club.addPlayer("Hans")
        val registration1 = competitionCategory.registerPlayer(player1)
        val registration2 = competitionCategory.registerPlayer(player2)
        val match1 = matchRepository.addMatch(
            dataGenerator.newMatchSpec(
                competitionCategoryId = competitionCategory.id,
                firstRegistrationId = registration1.id,
                secondRegistrationId = registration2.id,
            )
        )
        val match2 = matchRepository.addMatch(
            dataGenerator.newMatchSpec(
                competitionCategoryId = competitionCategory.id,
                firstRegistrationId = registration1.id,
                secondRegistrationId = registration2.id,
            )
        )
        val slot1 = TimeTableSlotSpec(
            LocalDateTime.now(),
            1,
            "Arena"
        )
        val slot2 = TimeTableSlotSpec(
            LocalDateTime.now(),
            2,
            "Arena"
        )
        repository.storeTimeTable(competition.id, listOf(slot1, slot2))
        val timeTableSlots = repository.getTimeTable(competition.id)

        return TestData(match1, match2, timeTableSlots.first(), timeTableSlots.last(), competition)
    }
}