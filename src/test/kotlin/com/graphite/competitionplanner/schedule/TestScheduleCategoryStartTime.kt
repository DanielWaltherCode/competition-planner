package com.graphite.competitionplanner.schedule

import com.graphite.competitionplanner.schedule.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.competition.api.CompetitionSpec
import com.graphite.competitionplanner.category.domain.interfaces.CategoryDTO
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.schedule.service.CategoryStartTimesWithOptionsDTO
import com.graphite.competitionplanner.schedule.service.ScheduleService
import com.graphite.competitionplanner.schedule.service.StartInterval
import com.graphite.competitionplanner.competitioncategory.service.CompetitionCategoryService
import com.graphite.competitionplanner.competition.service.CompetitionService
import com.graphite.competitionplanner.util.Util
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate

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

        val categories = categoryRepository.getCategories()
        val herrar1 = categories.find { it.categoryName == "Herrar 1" }!!
        val herrar2 = categories.find { it.categoryName == "Herrar 2" }!!

        // Categories to competition
        competitionCategory1 = competitionCategoryService.addCompetitionCategory(
            competitionId,
            CategoryDTO(herrar1.id, herrar1.categoryName, herrar1.categoryType)
        ).id
        competitionCategory2 = competitionCategoryService.addCompetitionCategory(
            competitionId,
            CategoryDTO(herrar2.id, herrar2.categoryName, herrar2.categoryType)
        ).id

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
        scheduleService.updateCategoryStartTime(
            startTimeOne.id, competitionCategory1, CategoryStartTimeSpec(
                LocalDate.now(), null, null
            )
        )

        val updatedStartTimesToday = scheduleService.getCategoryStartTimesByDay(competitionId, LocalDate.now())
        Assertions.assertNotNull(updatedStartTimesToday)
        Assertions.assertTrue(updatedStartTimesToday.size == 1)
    }

    @Test
    fun getCategoryStartTimesInCompetition() {
        val startTimeOne = categoryStartTimes.categoryStartTimeList[0]
        val startTimeTwo = categoryStartTimes.categoryStartTimeList[1]

        // Add categories
        scheduleService.updateCategoryStartTime(
            startTimeOne.id, competitionCategory1, CategoryStartTimeSpec(
                LocalDate.now(), StartInterval.EARLY_MORNING, null
            )
        )
        scheduleService.updateCategoryStartTime(
            startTimeTwo.id, competitionCategory2, CategoryStartTimeSpec(
                LocalDate.now().plusDays(1), null, null
            )
        )

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