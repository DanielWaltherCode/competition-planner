package com.graphite.competitionplanner.schedule

import com.graphite.competitionplanner.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.api.competition.CompetitionSpec
import com.graphite.competitionplanner.repositories.ScheduleRepository
import com.graphite.competitionplanner.repositories.competition.CategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionRepository
import com.graphite.competitionplanner.service.ScheduleService
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
    }

    @AfterEach
    fun deleteCompetition() {
        competitionCategoryService.deleteCategoryInCompetition(competitionCategory1)
        competitionCategoryService.deleteCategoryInCompetition(competitionCategory2)
        competitionRepository.deleteCompetition(competitionId)
    }

    @Test
    fun addCategoryStartTime() {
        val startTime = LocalDate.now().atTime(9, 30)
        val addedStartTime = scheduleService.addCategoryStartTime(competitionCategory1, CategoryStartTimeSpec(
            startTime
        ))
        Assertions.assertNotNull(addedStartTime)
        Assertions.assertNotNull(addedStartTime.id)
        Assertions.assertNotNull(addedStartTime.categoryDTO)
        Assertions.assertNotNull(addedStartTime.startTime)
    }

    @Test
    fun getCategoryStartTimes() {
        // Should be empty since nothing has been scheduled and there are no defaults
        val startTimesToday = scheduleService.getCategoryStartTimesByDay(competitionId, LocalDate.now())
        Assertions.assertTrue(startTimesToday.isEmpty())

        // Add a category
        scheduleService.addCategoryStartTime(competitionCategory1, CategoryStartTimeSpec(
            LocalDate.now().atTime(9, 30)
        ))

        val updatedStartTimesToday = scheduleService.getCategoryStartTimesByDay(competitionId, LocalDate.now())
        Assertions.assertNotNull(updatedStartTimesToday)
        Assertions.assertTrue(updatedStartTimesToday.size == 1)
    }

    @Test
    fun deleteCategoryStartTime() {
        // Add two categories
        scheduleService.addCategoryStartTime(competitionCategory1, CategoryStartTimeSpec(
            LocalDate.now().atTime(9, 30)
        ))
        scheduleService.addCategoryStartTime(competitionCategory2, CategoryStartTimeSpec(
            LocalDate.now().atTime(10, 30)
        ))

        val updatedStartTimesToday = scheduleService.getCategoryStartTimesByDay(competitionId, LocalDate.now())
        Assertions.assertEquals(2, updatedStartTimesToday.size)

        // Delete one and test again
        scheduleRepository.deleteCategoryStartTime(updatedStartTimesToday[0].id)
        val startTimesTodayAfterDelete = scheduleService.getCategoryStartTimesByDay(competitionId, LocalDate.now())
        Assertions.assertEquals(1, startTimesTodayAfterDelete.size)
    }
}