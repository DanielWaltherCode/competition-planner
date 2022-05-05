package com.graphite.competitionplanner.schedule.repository

import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.club.interfaces.IClubRepository
import com.graphite.competitionplanner.competition.interfaces.ICompetitionRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.match.repository.MatchRepository
import com.graphite.competitionplanner.player.interfaces.IPlayerRepository
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.result.interfaces.IResultRepository
import com.graphite.competitionplanner.schedule.domain.GenerateTimeTable
import com.graphite.competitionplanner.schedule.domain.GetTimeTable
import com.graphite.competitionplanner.schedule.domain.TimeTableSlotSpec
import com.graphite.competitionplanner.schedule.interfaces.IScheduleRepository
import com.graphite.competitionplanner.util.BaseRepositoryTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration

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
    fun testStoreTimeTable() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val numberOfTables = 5
        val estimatedMatchTime = 25.toDuration(TimeUnit.MINUTES)
        val location = "Arena A"
        val dates = listOf(LocalDate.of(2022, 6, 1), LocalDate.of(2022, 6, 2))
        val generateTimeTable = GenerateTimeTable(repository)
        val getTimeTable = GetTimeTable(repository)

        // Act
        generateTimeTable.execute(competition.id, numberOfTables, estimatedMatchTime, location, dates)

        val timeTable = getTimeTable.execute(competition.id)

        Assertions.assertTrue(timeTable.isNotEmpty())
    }

    @Test
    fun testAddMatchToTimeTableSlot() {
        // Setup
        val club = newClub()
        val competition = club.addCompetition()
        val competitionCategory = competition.addCompetitionCategory()
        val player1 = club.addPlayer("Sven")
        val player2 = club.addPlayer("Hans")
        val registration1 = competitionCategory.registerPlayer(player1)
        val registration2 = competitionCategory.registerPlayer(player2)
        val match1 = matchRepository.addMatch(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = registration1.id,
            secondRegistrationId = registration2.id,
        ))
        val match2 = matchRepository.addMatch(dataGenerator.newMatchSpec(
            competitionCategoryId = competitionCategory.id,
            firstRegistrationId = registration1.id,
            secondRegistrationId = registration2.id,
        ))
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

        // Act
        val afterFirst = repository.addMatchToTimeTableSlot(timeTableSlots.first().id, match1.id)
        val afterSecond = repository.addMatchToTimeTableSlot(timeTableSlots.first().id, match2.id)

        // Assert
        Assertions.assertEquals(1, afterFirst.size, "Not the expected number of items")
        Assertions.assertEquals(2, afterSecond.size, "Not the expected number of items")
    }
}