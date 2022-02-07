package com.graphite.competitionplanner.user.repository

import com.graphite.competitionplanner.Tables.USER_TABLE
import com.graphite.competitionplanner.user.service.UserWithEncryptedPassword
import com.graphite.competitionplanner.tables.RefreshToken.REFRESH_TOKEN
import com.graphite.competitionplanner.tables.records.RefreshTokenRecord
import com.graphite.competitionplanner.tables.records.UserTableRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(val dslContext: DSLContext) {

    fun addUser(user: UserWithEncryptedPassword): UserTableRecord {
        val record = dslContext.newRecord(USER_TABLE)
        record.email = user.username
        record.password = user.encryptedPassword
        record.clubid = user.clubId
        record.store()
        return record
    }

    fun getUserByEmail(email: String): UserTableRecord? {
        return dslContext
            .selectFrom(USER_TABLE)
            .where(USER_TABLE.EMAIL.eq(email))
            .fetchOneInto(USER_TABLE)
    }

    fun getUserById(userId: Int): UserTableRecord? {
        return dslContext
            .selectFrom(USER_TABLE)
            .where(USER_TABLE.ID.eq(userId))
            .fetchOneInto(USER_TABLE)
    }

    fun saveRefreshToken(refreshToken: String, userId: Int) {
        val record = dslContext.newRecord(REFRESH_TOKEN)
        record.refreshToken = refreshToken
        record.userId = userId
        record.store()
    }

    fun getRefreshToken(refreshToken: String): RefreshTokenRecord? {
         return dslContext
             .selectFrom(REFRESH_TOKEN)
             .where(REFRESH_TOKEN.REFRESH_TOKEN_.eq(refreshToken))
             .fetchOneInto(REFRESH_TOKEN)
    }

    fun clearTable() = dslContext.deleteFrom(USER_TABLE).execute()

    fun getRefreshTokenByUser(userId: Int): RefreshTokenRecord? {
        return dslContext
            .selectFrom(REFRESH_TOKEN)
            .where(REFRESH_TOKEN.USER_ID.eq(userId))
            .fetchOneInto(REFRESH_TOKEN)
    }

    fun updateRefreshToken(id: Int, refreshToken: String, userId: Int) {
        val record = dslContext.newRecord(REFRESH_TOKEN)
        record.id = id
        record.refreshToken = refreshToken
        record.userId = userId
        record.update()
    }
}