package com.graphite.competitionplanner.repositories

import com.graphite.competitionplanner.Tables.PLAYING_IN
import com.graphite.competitionplanner.tables.PlayerRegistration.PLAYER_REGISTRATION
import com.graphite.competitionplanner.tables.Registration.REGISTRATION
import com.graphite.competitionplanner.tables.records.PlayerRegistrationRecord
import com.graphite.competitionplanner.tables.records.PlayingInRecord
import com.graphite.competitionplanner.tables.records.RegistrationRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class RegistrationRepository(val dslContext: DSLContext) {

    // Sets up base registration. Double players playing together will have same registration id
    fun addRegistration(date: LocalDate): RegistrationRecord {
        val registration: RegistrationRecord = dslContext.newRecord(REGISTRATION)
        registration.registrationDate = date
        registration.store()
        return registration
    }

    fun clearRegistration() = dslContext.deleteFrom(REGISTRATION).execute()


    // Links player to registration
    fun registerPlayer(registrationId: Int, playerId: Int): PlayerRegistrationRecord {
        val record: PlayerRegistrationRecord = dslContext.newRecord(PLAYER_REGISTRATION)
        record.registrationId = registrationId
        record.playerId = playerId
        record.store()
        return record
    }

    fun clearPlayerRegistration() = dslContext.deleteFrom(PLAYER_REGISTRATION).execute()

    // Links a registration to a specific category in the competition. Also includes the seed.
    fun registerPlayingIn(registrationId: Int, seed: Int?, competitionPlayingCategory: Int): PlayingInRecord {
        val record: PlayingInRecord = dslContext.newRecord(PLAYING_IN)
        record.registrationId = registrationId;
        record.seed = seed;
        record.competitionPlayingCategoryId = competitionPlayingCategory
        record.store()
        return record
    }

    fun clearPlayingIn() = dslContext.deleteFrom(PLAYING_IN).execute()
}