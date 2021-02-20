package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.USER_TABLE
import com.graphite.competitionplanner.service.UserWithEncryptedPassword
import com.graphite.competitionplanner.tables.records.UserTableRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(val dslContext: DSLContext) {

    fun addUser(user: UserWithEncryptedPassword): UserTableRecord {
        val record = dslContext.newRecord(USER_TABLE)
        record.username = user.username
        record.password = user.encryptedPassword
        record.clubid = user.clubId
        record.store()
        return record
    }

    fun getUserByUsername(username: String): UserTableRecord? {
        return dslContext
            .selectFrom(USER_TABLE)
            .where(USER_TABLE.USERNAME.eq(username))
            .fetchOneInto(USER_TABLE)
    }

    fun clearTable() = dslContext.deleteFrom(USER_TABLE).execute()
}