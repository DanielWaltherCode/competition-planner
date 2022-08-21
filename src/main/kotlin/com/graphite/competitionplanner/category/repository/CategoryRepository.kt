package com.graphite.competitionplanner.category.repository

import com.graphite.competitionplanner.Tables
import com.graphite.competitionplanner.Tables.CATEGORY
import com.graphite.competitionplanner.category.api.CustomCategorySpec
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.category.interfaces.CategoryType
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.tables.records.CategoryRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * The categories possible for players to compete in. Should be a complete, non-updatable list!
 */
@Repository
class CategoryRepository(val dslContext: DSLContext) : ICategoryRepository {

    override fun getAvailableCategories(competitionId: Int): List<CategoryDTO> {
        return dslContext
                .selectFrom(CATEGORY)
                .where(CATEGORY.COMPETITION_ID.isNull.or(CATEGORY.COMPETITION_ID.eq(competitionId)))
                .fetch()
                .map { it.toDto() }
    }

    override fun addCategory(category: String, type: CategoryType) {
        dslContext.insertInto(CATEGORY).columns(CATEGORY.CATEGORY_NAME, CATEGORY.CATEGORY_TYPE)
            .values(category, type.name).execute()
    }

    override fun addCustomCategory(competitionId: Int, customCategorySpec: CustomCategorySpec): CategoryDTO {
       val addedCategoryRecord = dslContext.insertInto(CATEGORY)
                .columns(CATEGORY.CATEGORY_NAME, CATEGORY.CATEGORY_TYPE, CATEGORY.COMPETITION_ID)
                .values(customCategorySpec.name, customCategorySpec.type.name, competitionId)
                .returningResult(CATEGORY.ID, CATEGORY.CATEGORY_NAME, CATEGORY.CATEGORY_TYPE)
                .fetchOne()

        return CategoryDTO(addedCategoryRecord!!.getValue(CATEGORY.ID),
                addedCategoryRecord.getValue(CATEGORY.CATEGORY_NAME),
                CategoryType.valueOf(addedCategoryRecord.getValue(CATEGORY.CATEGORY_TYPE)))
    }

    override fun deleteCategory(categoryId: Int) {
        dslContext
                .deleteFrom(CATEGORY)
                .where(CATEGORY.ID.eq(categoryId))
                .execute()
    }

    override fun deleteCategoriesInCompetition(competitionId: Int) {
        dslContext
                .deleteFrom(CATEGORY)
                .where(CATEGORY.COMPETITION_ID.eq(competitionId))
                .execute()
    }

    override fun updateCategory(categorySpec: CategorySpec) {
        dslContext
                .update(CATEGORY)
                .set(CATEGORY.CATEGORY_NAME, categorySpec.name)
                .set(CATEGORY.CATEGORY_TYPE, categorySpec.type.name)
                .where(CATEGORY.ID.eq(categorySpec.id))
                .execute()
    }

    internal fun clearTable() = dslContext.deleteFrom(CATEGORY).execute()

    private fun CategoryRecord.toDto(): CategoryDTO {
        return CategoryDTO(this.id, this.categoryName, CategoryType.valueOf(this.categoryType))
    }
}