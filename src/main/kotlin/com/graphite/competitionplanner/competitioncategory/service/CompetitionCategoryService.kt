package com.graphite.competitionplanner.competitioncategory.service

import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.competitioncategory.domain.*
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryUpdateSpec
import com.graphite.competitionplanner.competitioncategory.repository.CompetitionCategoryRepository
import com.graphite.competitionplanner.registration.repository.RegistrationRepository
import com.graphite.competitionplanner.schedule.api.CategoryStartTimeSpec
import com.graphite.competitionplanner.schedule.service.ScheduleService
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CompetitionCategoryService(
    val competitionCategoryRepository: CompetitionCategoryRepository,
    @Lazy val scheduleService: ScheduleService,
    val registrationRepository: RegistrationRepository,
    val addCompetitionCategory: AddCompetitionCategory,
    val getCompetitionCategory: GetCompetitionCategory,
    val getCompetitionCategories: GetCompetitionCategories,
    val updateCompetitionCategory: UpdateCompetitionCategory,
    val deleteCompetitionCategory: DeleteCompetitionCategory,
    val cancelCompetitionCategory: CancelCompetitionCategory
) {
    /**
     * Cancel competition category. This is used when players have already
     * signed up for category. Then we shouldn't delete data but just the category to
     * cancelled
     */
    fun cancelCategoryInCompetition(competitionCategoryId: Int) {
        cancelCompetitionCategory.execute(competitionCategoryId)
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
        deleteCompetitionCategory.execute(categoryId)
    }

    fun getByCompetitionCategoryId(competitionCategoryId: Int): CompetitionCategoryDTO {
        return getCompetitionCategory.execute(competitionCategoryId)
    }

    fun addCompetitionCategory(
        competitionId: Int,
        spec: CategorySpec
    ): CompetitionCategoryDTO {
        val competitionCategory = addCompetitionCategory.execute(competitionId, spec)
        scheduleService.addCategoryStartTime(competitionCategory.id, CategoryStartTimeSpec(null, null, null))
        return competitionCategory
    }

    fun getCompetitionCategoriesFor(competitionId: Int): List<CompetitionCategoryDTO> {
        return getCompetitionCategories.execute(competitionId)
    }

    fun updateCompetitionCategory(competitionCategoryId: Int, spec: CompetitionCategoryUpdateSpec) {
        updateCompetitionCategory.execute(competitionCategoryId, spec)
    }
}