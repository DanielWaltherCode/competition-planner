package com.graphite.competitionplanner.schedule.service

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.repository.CategoryRepository
import com.graphite.competitionplanner.competition.domain.CreateCompetition
import com.graphite.competitionplanner.competition.interfaces.CompetitionSpec
import com.graphite.competitionplanner.competition.interfaces.LocationSpec
import com.graphite.competitionplanner.competition.repository.CompetitionRepository
import com.graphite.competitionplanner.competitioncategory.domain.AddCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.DeleteCompetitionCategory
import com.graphite.competitionplanner.competitioncategory.domain.FindCompetitionCategory
import com.graphite.competitionplanner.schedule.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.schedule.repository.ScheduleRepository
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
    @Autowired val categoryRepository: CategoryRepository,
    @Autowired val competitionRepository: CompetitionRepository,
    @Autowired val createCompetition: CreateCompetition,
    @Autowired val addCompetitionCategory: AddCompetitionCategory,
    @Autowired val deleteCompetitionCategory: DeleteCompetitionCategory,
    @Autowired val findCompetitionCategory: FindCompetitionCategory
) {

    var competitionId = 0
    var competitionCategory1 = 0
    var competitionCategory2 = 0
    lateinit var categoryStartTimes: CategoryStartTimesWithOptionsDTO

    @BeforeEach
    fun addCompetition() {
        competitionId = createCompetition.execute(
            CompetitionSpec(
                location = LocationSpec("Lund"),
                name = "Eurofinans 2021",
                welcomeText = "Välkomna till Eurofinans",
                organizingClubId = util.getClubIdOrDefault("Lugi"),
                startDate = LocalDate.now(),
                endDate = LocalDate.now().plusDays(10)
            )
        ).id

        val categories = categoryRepository.getAvailableCategories()
        val herrar1 = categories.find { it.name == "Herrar 1" }!!
        val herrar2 = categories.find { it.name == "Herrar 2" }!!

        // Categories to competition
        competitionCategory1 = addCompetitionCategory.execute(
            competitionId,
            CategorySpec(herrar1.id, herrar1.name, herrar1.type)
        ).id
        competitionCategory2 = addCompetitionCategory.execute(
            competitionId,
            CategorySpec(herrar2.id, herrar2.name, herrar2.type)
        ).id

        // Competition start times are set up automatically now so fetch the two ones just added
        categoryStartTimes = scheduleService.getCategoryStartTimesForCompetition(competitionId)
    }

    @AfterEach
    fun deleteCompetition() {
        deleteCompetitionCategory.execute(findCompetitionCategory.byId(competitionCategory1))
        deleteCompetitionCategory.execute(findCompetitionCategory.byId(competitionCategory2))
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