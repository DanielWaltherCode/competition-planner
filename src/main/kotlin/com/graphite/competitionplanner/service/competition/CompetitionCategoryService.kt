package com.graphite.competitionplanner.service.competition

import com.graphite.competitionplanner.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.api.competition.CategoryGameRulesSpec
import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategory
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.service.CompetitionCategoryDTO
import com.graphite.competitionplanner.service.ScheduleService
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class CompetitionCategoryService(
    val clubRepository: ClubRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    @Lazy val scheduleService: ScheduleService,
    val categoryService: CategoryService,
    val registrationRepository: RegistrationRepository
) {
    /**
     * Cancel competition category. This is used when players have already
     * signed up for category. Then we shouldn't delete data but just the category to
     * cancelled
     */
    fun cancelCategoryInCompetition(categoryId: Int) {
        competitionCategoryRepository.cancelCategoryInCompetition(categoryId)
    }

    /**
     * Categories can be deleted if no players are registered yet
     * Delete from competition categories
     * Competition category metadata should be deleted on cascade
     */
    fun deleteCategoryInCompetition(categoryId: Int) {
        // Check if competition registration contains this id, if so don't delete
        if (registrationRepository.checkIfCategoryHasRegistrations(categoryId)) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "A category with registered players should not be deleted"
            )
        }
        competitionCategoryRepository.deleteCategoryInCompetition(categoryId)
    }

    fun addCompetitionCategory(competitionId: Int, categoryId: Int): Int {
        val competitionCategoryId = competitionCategoryRepository.addCompetitionCategory(
            competitionId,
            categoryId
        )

        // Also set up default metadata and game rules that can later be modified
        // This default sets draw type to 3 == Pool and cup and draw strategy to 1 == normal
        categoryService.addCategoryMetadata(
            competitionCategoryId,
            CategoryMetadataSpec(
                cost = 150f,
                drawTypeId = 3,
                nrPlayersPerGroup = 4,
                nrPlayersToPlayoff = 2,
                poolDrawStrategyId = 1
            )
        )
        categoryService.addCategoryGameRules(competitionCategoryId,
        CategoryGameRulesSpec(
            nrSets = 5,
            winScore = 11,
            winMargin = 2,
            nrSetsFinal = 7,
            winScoreFinal = 11,
            winMarginFinal = 2,
            winScoreTiebreak = null,
            winMarginTieBreak = null
        )
        )

        // Add to category start time table
        // All fields are null but it's easier to display on website if categories are already in this table
        scheduleService.addCategoryStartTime(competitionCategoryId, CategoryStartTimeSpec(null, null, null))
        return competitionCategoryId
    }

    fun getByCompetitionCategoryId(competitionCategoryId: Int): CompetitionCategory {
        return competitionCategoryRepository.getById(competitionCategoryId)
    }
}