package com.graphite.competitionplanner.category.repository

import com.graphite.competitionplanner.Tables
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.ICategoryRepository
import com.graphite.competitionplanner.tables.records.CategoryRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * The categories possible for players to compete in. Should be a complete, non-updatable list!
 */
@Repository
class CategoryRepository(val dslContext: DSLContext) : ICategoryRepository {

    override fun getAvailableCategories(): List<CategoryDTO> {
        return dslContext.selectFrom(Tables.CATEGORY).fetch().map { it.toDto() }
    }

    internal fun addCategory(category: String, type: String) {
        dslContext.insertInto(Tables.CATEGORY).columns(Tables.CATEGORY.CATEGORY_NAME, Tables.CATEGORY.CATEGORY_TYPE)
            .values(category, type).execute()
    }

    internal fun addCategoryWithId(id: Int, category: String, type: String) {
        dslContext.insertInto(Tables.CATEGORY)
            .columns(Tables.CATEGORY.ID, Tables.CATEGORY.CATEGORY_NAME, Tables.CATEGORY.CATEGORY_TYPE)
            .values(id, category, type).execute()
    }

    internal fun clearTable() = dslContext.deleteFrom(Tables.CATEGORY).execute()

    private fun CategoryRecord.toDto(): CategoryDTO {
        return CategoryDTO(this.id, this.categoryName, this.categoryType)
    }
}