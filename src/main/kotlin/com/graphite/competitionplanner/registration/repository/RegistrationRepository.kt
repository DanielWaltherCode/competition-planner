package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesDTO
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpec
import com.graphite.competitionplanner.registration.interfaces.RegistrationSinglesSpecWithDate
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.PlayerRegistration.PLAYER_REGISTRATION
import com.graphite.competitionplanner.tables.Registration.REGISTRATION
import com.graphite.competitionplanner.tables.records.CompetitionCategoryRegistrationRecord
import com.graphite.competitionplanner.tables.records.PlayerRecord
import com.graphite.competitionplanner.tables.records.PlayerRegistrationRecord
import com.graphite.competitionplanner.tables.records.RegistrationRecord
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class RegistrationRepository(val dslContext: DSLContext) : IRegistrationRepository {

    fun addRegistrationWithId(id: Int, date: LocalDate): RegistrationRecord {
        val registration: RegistrationRecord = dslContext.newRecord(REGISTRATION)
        registration.id = id
        registration.registrationDate = date
        registration.store()
        return registration
    }

    fun deleteRegistration(registrationId: Int): Boolean {
        return dslContext.deleteFrom(REGISTRATION).where(REGISTRATION.ID.eq(registrationId)).execute() > 0
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

    fun checkIfCategoryHasRegistrations(competitionCategory: Int): Boolean {
        return dslContext.fetchExists(dslContext.selectFrom(COMPETITION_CATEGORY_REGISTRATION)
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategory)))
    }

    fun getRegistrationsInCompetition(competitionId: Int): List<PlayerRecord> {
        return dslContext.select()
            .from(COMPETITION)
            .join(COMPETITION_CATEGORY).on(
                COMPETITION_CATEGORY.COMPETITION_ID.eq(
                    Competition.COMPETITION.ID))
            .join(CATEGORY).on(
                CATEGORY.ID.eq(
                    COMPETITION_CATEGORY.CATEGORY))
            .join(COMPETITION_CATEGORY_REGISTRATION).on(
                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(
                    COMPETITION_CATEGORY.ID))
            .join(PLAYER_REGISTRATION).on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
            .where(Competition.COMPETITION.ID.eq(competitionId))
            .fetchInto(PLAYER)
    }

    fun getRegistrationIdsInCategory(competitionCategoryId: Int): List<Int> {
        return dslContext.select(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID)
            .from(COMPETITION_CATEGORY_REGISTRATION)
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .fetchInto(Int::class.java)
    }

    fun getRegisteredPlayersInCategory(competitionCategoryId: Int): List<PlayerRecord> {
        return dslContext.select()
            .from(COMPETITION_CATEGORY_REGISTRATION)
            .join(PLAYER_REGISTRATION).on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .fetchInto(PLAYER)
    }

    fun getPlayersFromRegistrationId(registrationId: Int): List<PlayerRecord> {
        return dslContext.select()
            .from(PLAYER_REGISTRATION)
            .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
            .where(PLAYER_REGISTRATION.REGISTRATION_ID.eq(registrationId))
            .fetchInto(PLAYER)
    }

    fun setSeed(registrationId: Int, competitionCategoryId: Int, seed: Int){
        dslContext.update(COMPETITION_CATEGORY_REGISTRATION)
                .set(COMPETITION_CATEGORY_REGISTRATION.SEED, seed)
                .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                        .and(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(registrationId)))
                .execute()
    }

    // Get seeds in competition category
    fun getSeeds(competitionCategoryId: Int): List<CompetitionCategoryRegistrationRecord> {
        return dslContext.select()
            .from(COMPETITION_CATEGORY_REGISTRATION)
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId).and(
                COMPETITION_CATEGORY_REGISTRATION.SEED.isNotNull))
            .fetchInto(COMPETITION_CATEGORY_REGISTRATION)
    }

    fun clearPlayingIn() = dslContext.deleteFrom(COMPETITION_CATEGORY_REGISTRATION).execute()

    override fun store(spec: RegistrationSinglesSpecWithDate): RegistrationSinglesDTO {
        val registrationRecord = addRegistration(spec.date)
        registerPlayer(registrationRecord.id, spec.playerId)
        registerInCategory(registrationRecord.id, null, spec.competitionCategoryId)

        return RegistrationSinglesDTO(registrationRecord.id, spec.playerId, spec.competitionCategoryId, spec.date)
    }

    override fun getAllPlayerIdsRegisteredTo(competitionCategoryId: Int): List<Int> {
        val records = dslContext.select()
            .from(COMPETITION_CATEGORY_REGISTRATION)
            .join(PLAYER_REGISTRATION)
            .on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .fetchInto(PLAYER)

        return records.map { it.id }
    }

    override fun getRegistrationFor(spec: RegistrationSinglesSpec): RegistrationSinglesDTO {
        val record = dslContext.select(REGISTRATION.ID, REGISTRATION.REGISTRATION_DATE, PLAYER_REGISTRATION.PLAYER_ID,
            COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID)
            .from(REGISTRATION)
            .join(PLAYER_REGISTRATION).on(REGISTRATION.ID.eq(PLAYER_REGISTRATION.REGISTRATION_ID))
            .join(COMPETITION_CATEGORY_REGISTRATION)
            .on(REGISTRATION.ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(spec.competitionCategoryId)
                .and(PLAYER_REGISTRATION.PLAYER_ID.eq(spec.playerId)))
            .fetchOne()

        return RegistrationSinglesDTO(
            record.getValue(REGISTRATION.ID),
            record.getValue(PLAYER_REGISTRATION.PLAYER_ID),
            record.getValue(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID),
            record.getValue(REGISTRATION.REGISTRATION_DATE)
        )
    }

    // Sets up base registration. Double players playing together will have same registration id
    private fun addRegistration(date: LocalDate): RegistrationRecord {
        val registration: RegistrationRecord = dslContext.newRecord(REGISTRATION)
        registration.registrationDate = date
        registration.store()
        return registration
    }

    // Links a registration to a specific category in the competition. Also includes the seed.
    private fun registerInCategory(
        registrationId: Int,
        seed: Int?,
        competitionCategory: Int
    ): CompetitionCategoryRegistrationRecord {
        val record = dslContext.newRecord(COMPETITION_CATEGORY_REGISTRATION)
        record.registrationId = registrationId
        record.seed = seed
        record.competitionCategoryId = competitionCategory
        record.store()
        return record
    }
}


