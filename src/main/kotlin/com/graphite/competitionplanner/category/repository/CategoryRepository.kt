package com.graphite.competitionplanner.category.repository

import com.graphite.competitionplanner.Tables
import com.graphite.competitionplanner.category.domain.interfaces.ICategoryRepository
import com.graphite.competitionplanner.category.domain.interfaces.CategoryDTO
import com.graphite.competitionplanner.tables.records.CategoryRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

/**
 * The categories possible for players to compete in. Should be a complete, non-updatable list!
 */
@Repository
class CategoryRepository(val dslContext: DSLContext) : ICategoryRepository {

    fun addCategory(category: String, type: String) {
        dslContext.insertInto(Tables.CATEGORY).columns(Tables.CATEGORY.CATEGORY_NAME, Tables.CATEGORY.CATEGORY_TYPE)
            .values(category, type).execute()
    }

    fun addCategoryWithId(id: Int, category: String, type: String) {
        dslContext.insertInto(Tables.CATEGORY)
            .columns(Tables.CATEGORY.ID, Tables.CATEGORY.CATEGORY_NAME, Tables.CATEGORY.CATEGORY_TYPE)
            .values(id, category, type).execute()
    }

    fun getByName(name: String): CategoryRecord {
        return dslContext.selectFrom(Tables.CATEGORY).where(Tables.CATEGORY.CATEGORY_NAME.eq(name)).fetchOne()
    }

    fun getById(id: Int): CategoryRecord {
        return dslContext.selectFrom(Tables.CATEGORY).where(Tables.CATEGORY.ID.eq(id)).fetchOne()
    }

    fun getCategories(): List<CategoryRecord> {
        return dslContext.selectFrom(Tables.CATEGORY).fetch()
    }

    fun clearTable() = dslContext.deleteFrom(Tables.CATEGORY).execute()


    override fun getAvailableCategories(): List<CategoryDTO> {
        return dslContext.selectFrom(Tables.CATEGORY).fetch().map { it.toDto() }
    }

    private fun CategoryRecord.toDto(): CategoryDTO {
        return CategoryDTO(this.id, this.categoryName, this.categoryType)
    }
}