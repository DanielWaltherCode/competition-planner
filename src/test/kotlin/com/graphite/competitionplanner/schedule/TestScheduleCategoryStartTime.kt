package com.graphite.competitionplanner.schedule

import com.graphite.competitionplanner.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.api.competition.CompetitionSpec
import com.graphite.competitionplanner.repositories.ScheduleRepository
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import com.graphite.competitionplanner.service.CategoryStartTimesWithOptionsDTO
import com.graphite.competitionplanner.service.ScheduleService
import com.graphite.competitionplanner.service.StartInterval
import com.graphite.competitionplanner.service.competition.CompetitionCategoryService
import com.graphite.competitionplanner.service.competition.CompetitionService
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.time.LocalDateTime

@SpringBootTest
class TestScheduleCategoryStartTime(
    @Autowired val util: Util,
    @Autowired val scheduleService: ScheduleService,
    @Autowired val scheduleRepository: ScheduleRepository,
    @Autowired val competitionCategoryService: CompetitionCategoryService,
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val competitionService: CompetitionService,
    @Autowired val competitionRepository: CompetitionRepository
) {

    var competitionId = 0
    var competitionCategory1 = 0
    var competitionCategory2 = 0
    lateinit var categoryStartTimes: CategoryStartTimesWithOptionsDTO

    @BeforeEach
    fun addCompetition() {
        competitionId = competitionService.addCompetition(
            CompetitionSpec(
                location = "Lund",
                name = "Eurofinans 2021",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        ).id

        // Categories to competition
        competitionCategory1 = competitionCategoryService.addCompetitionCategory(
            competitionId,
            categoryRepository.getByName("Herrar 1").id
        )
        competitionCategory2 = competitionCategoryService.addCompetitionCategory(
            competitionId,
            categoryRepository.getByName("Herrar 2").id
        )

        // Competition start times are set up automatically now so fetch the two ones just added
        categoryStartTimes = scheduleService.getCategoryStartTimesForCompetition(competitionId)
    }

    @AfterEach
    fun deleteCompetition() {
        competitionCategoryService.deleteCategoryInCompetition(competitionCategory1)
        competitionCategoryService.deleteCategoryInCompetition(competitionCategory2)
        competitionRepository.deleteCompetition(competitionId)
    }


    @Test
    fun getCategoryStartTimes() {
        // Should be empty since nothing has been scheduled and there are no defaults
        val startTimesToday = scheduleService.getCategoryStartTimesByDay(competitionId, LocalDate.now())
        Assertions.assertTrue(startTimesToday.isEmpty())

        val startTimeOne = categoryStartTimes.categoryStartTimeList[0]

        // Update one category to take place today
        scheduleService.updateCategoryStartTime(startTimeOne.id, competitionCategory1, CategoryStartTimeSpec(
            LocalDate.now(), null, null
        ))

        val updatedStartTimesToday = scheduleService.getCategoryStartTimesByDay(competitionId, LocalDate.now())
        Assertions.assertNotNull(updatedStartTimesToday)
        Assertions.assertTrue(updatedStartTimesToday.size == 1)
    }

    @Test
    fun getCategoryStartTimesInCompetition() {
        val startTimeOne = categoryStartTimes.categoryStartTimeList[0]
        val startTimeTwo = categoryStartTimes.categoryStartTimeList[1]

        // Add categories
        scheduleService.updateCategoryStartTime(startTimeOne.id, competitionCategory1, CategoryStartTimeSpec(
            LocalDate.now(), StartInterval.EARLY_MORNING, null
        ))
        scheduleService.updateCategoryStartTime(startTimeTwo.id, competitionCategory2, CategoryStartTimeSpec(
            LocalDate.now().plusDays(1),null, null
        ))

        val categoryStartTimes = scheduleService.getCategoryStartTimesForCompetition(competitionId)
        Assertions.assertEquals(2, categoryStartTimes.categoryStartTimeList.size)

        val startTime1 = categoryStartTimes.categoryStartTimeList[0]
        val startTime2 = categoryStartTimes.categoryStartTimeList[1]

        Assertions.assertEquals(LocalDate.now(), startTime1.playingDay)
        Assertions.assertEquals(StartInterval.EARLY_MORNING, startTime1.startInterval)
        Assertions.assertEquals(null, startTime1.exactStartTime)

        Assertions.assertEquals(LocalDate.now().plusDays(1), startTime2.playingDay)
        Assertions.assertEquals(StartInterval.NOT_SELECTED, startTime2.startInterval)
        Assertions.assertEquals(null, startTime2.exactStartTime)
    }

    @Test
    fun deleteCategoryStartTime() {

        val updatedStartTimesToday = scheduleService.getCategoryStartTimesForCompetition(competitionId)
        Assertions.assertEquals(2, updatedStartTimesToday.categoryStartTimeList.size)

        // Delete one and test again
        val startTime1 = categoryStartTimes.categoryStartTimeList[0]
        scheduleRepository.deleteCategoryStartTime(startTime1.id)
        val startTimesTodayAfterDelete = scheduleService.getCategoryStartTimesForCompetition(competitionId)
        Assertions.assertEquals(1, startTimesTodayAfterDelete.categoryStartTimeList.size)
    }
}