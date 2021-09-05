package com.graphite.competitionplanner.service.competition

import com.graphite.competitionplanner.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.api.competition.CategoryGameRulesSpec
import com.graphite.competitionplanner.api.competition.CategoryMetadataSpec
import com.graphite.competitionplanner.domain.dto.CategoryDTO
import com.graphite.competitionplanner.domain.usecase.competition.AddCompetitionCategory
import com.graphite.competitionplanner.repositories.ClubRepository
import com.graphite.competitionplanner.repositories.RegistrationRepository
import com.graphite.competitionplanner.repositories.competition.CompetitionCategory
import com.graphite.competitionplanner.repositories.competition.CompetitionCategoryRepository
import com.graphite.competitionplanner.service.CategoryService
import com.graphite.competitionplanner.service.CompetitionCategoryDTO
import com.graphite.competitionplanner.service.ScheduleService
import com.graphite.competitionplanner.service.draw.DrawStrategy
import com.graphite.competitionplanner.service.draw.DrawType
import com.graphite.competitionplanner.service.draw.Round
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CompetitionCategoryService(
    val clubRepository: ClubRepository,
    val competitionCategoryRepository: CompetitionCategoryRepository,
    @Lazy val scheduleService: ScheduleService,
    val categoryService: CategoryService,
    val registrationRepository: RegistrationRepository,
    val addCompetitionCategory: AddCompetitionCategory
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

    fun addCompetitionCategory(competitionId: Int, categoryId: Int): CompetitionCategoryDTO {
        val competitionCategoryId = competitionCategoryRepository.addCompetitionCategory(
            competitionId,
            categoryId
        )

        setUpDefaultCategoryData(competitionCategoryId)
        val addedCategory = competitionCategoryRepository.getById(competitionCategoryId)
        return CompetitionCategoryDTO(addedCategory.categoryId, addedCategory.categoryName)
    }

    fun setUpDefaultCategoryData(competitionCategoryId: Int) {
        // Also set up default metadata and game rules that can later be modified
        categoryService.addCategoryMetadata(
            competitionCategoryId,
            CategoryMetadataSpec(
                cost = 150f,
                drawType = DrawType.POOL_AND_CUP,
                nrPlayersPerGroup = 4,
                nrPlayersToPlayoff = 2,
                poolDrawStrategy = DrawStrategy.NORMAL
            )
        )
        categoryService.addCategoryGameRules(
            competitionCategoryId,
            CategoryGameRulesSpec(
                nrSets = 5,
                winScore = 11,
                winMargin = 2,
                differentNumberOfGamesFromRound = Round.UNKNOWN,
                nrSetsFinal = 7,
                winScoreFinal = 11,
                winMarginFinal = 2,
                tiebreakInFinalGame = false,
                winScoreTiebreak = 7,
                winMarginTiebreak = 2
            )
        )

        // Add to category start time table
        // All fields are null but it's easier to display on website if categories are already in this table
        scheduleService.addCategoryStartTime(competitionCategoryId, CategoryStartTimeSpec(null, null, null))
    }

    fun getByCompetitionCategoryId(competitionCategoryId: Int): CompetitionCategory {
        return competitionCategoryRepository.getById(competitionCategoryId)
    }

    fun addCompetitionCategory(
        competitionId: Int,
        category: CategoryDTO
    ): com.graphite.competitionplanner.domain.dto.CompetitionCategoryDTO {
        return addCompetitionCategory.execute(competitionId, category)
    }
}