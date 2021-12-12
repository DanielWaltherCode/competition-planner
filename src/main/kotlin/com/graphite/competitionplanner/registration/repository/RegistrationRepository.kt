package com.graphite.competitionplanner.registration.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.category.interfaces.CategoryDTO
import com.graphite.competitionplanner.category.interfaces.CategorySpec
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryDTO
import com.graphite.competitionplanner.draw.interfaces.ISeedRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.player.interfaces.PlayerDTO
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.registration.interfaces.*
import com.graphite.competitionplanner.tables.Competition
import com.graphite.competitionplanner.tables.PlayerRegistration.PLAYER_REGISTRATION
import com.graphite.competitionplanner.tables.Registration.REGISTRATION
import com.graphite.competitionplanner.tables.records.*
import org.jooq.DSLContext
import org.jooq.Record4
import org.jooq.TableField
import org.jooq.impl.DSL.partitionBy
import org.jooq.impl.DSL.sum
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class RegistrationRepository(val dslContext: DSLContext) : IRegistrationRepository, ISeedRepository {

    fun addRegistrationWithId(id: Int, date: LocalDate): RegistrationRecord {
        val registration: RegistrationRecord = dslContext.newRecord(REGISTRATION)
        registration.id = id
        registration.registrationDate = date
        registration.store()
        return registration
    }

    internal fun clearRegistration() = dslContext.deleteFrom(REGISTRATION).execute()

    // Links player to registration
    fun registerPlayer(registrationId: Int, playerId: Int): PlayerRegistrationRecord {
        val record: PlayerRegistrationRecord = dslContext.newRecord(PLAYER_REGISTRATION)
        record.registrationId = registrationId
        record.playerId = playerId
        record.store()
        return record
    }


    fun clearPlayerRegistration() = dslContext.deleteFrom(PLAYER_REGISTRATION).execute()

    fun getRegistreredPlayersInCompetition(competitionId: Int): List<PlayerRecord> {
        return dslContext.selectDistinct(PLAYER.asterisk())
            .from(COMPETITION)
            .join(COMPETITION_CATEGORY).on(
                COMPETITION_CATEGORY.COMPETITION_ID.eq(
                    Competition.COMPETITION.ID
                )
            )
            .join(CATEGORY).on(
                CATEGORY.ID.eq(
                    COMPETITION_CATEGORY.CATEGORY
                )
            )
            .join(COMPETITION_CATEGORY_REGISTRATION).on(
                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(
                    COMPETITION_CATEGORY.ID
                )
            )
            .join(PLAYER_REGISTRATION)
            .on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
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

    fun setSeed(registrationId: Int, competitionCategoryId: Int, seed: Int) {
        dslContext.update(COMPETITION_CATEGORY_REGISTRATION)
            .set(COMPETITION_CATEGORY_REGISTRATION.SEED, seed)
            .where(
                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                    .and(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(registrationId))
            )
            .execute()
    }

    // Get seeds in competition category
    fun getSeeds(competitionCategoryId: Int): List<CompetitionCategoryRegistrationRecord> {
        return dslContext.select()
            .from(COMPETITION_CATEGORY_REGISTRATION)
            .where(
                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId).and(
                    COMPETITION_CATEGORY_REGISTRATION.SEED.isNotNull
                )
            )
            .fetchInto(COMPETITION_CATEGORY_REGISTRATION)
    }

    fun clearPlayingIn() = dslContext.deleteFrom(COMPETITION_CATEGORY_REGISTRATION).execute()

    override fun storeSingles(spec: RegistrationSinglesSpecWithDate): RegistrationSinglesDTO {
        val registrationRecord = addRegistration(spec.date)
        registerPlayer(registrationRecord.id, spec.playerId)
        registerInCategory(registrationRecord.id, spec.competitionCategoryId)

        return RegistrationSinglesDTO(registrationRecord.id, spec.playerId, spec.competitionCategoryId, spec.date)
    }

    override fun storeDoubles(spec: RegistrationDoublesSpecWithDate): RegistrationDoublesDTO {
        val registrationRecord = addRegistration(spec.date)
        registerPlayer(registrationRecord.id, spec.playerOneId)
        registerPlayer(registrationRecord.id, spec.playerTwoId)
        registerInCategory(registrationRecord.id, spec.competitionCategoryId)

        return RegistrationDoublesDTO(
            registrationRecord.id,
            spec.playerOneId,
            spec.playerTwoId,
            spec.competitionCategoryId,
            spec.date
        )
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

    override fun getAllRegisteredPlayersInCompetition(competitionId: Int): List<PlayerWithClubDTO> {
        val records = dslContext.select(
            PLAYER.ID,
            PLAYER.FIRST_NAME,
            PLAYER.LAST_NAME,
            PLAYER.DATE_OF_BIRTH,
            CLUB.ID,
            CLUB.NAME,
            CLUB.ADDRESS
        )
            .from(COMPETITION)
            .join(COMPETITION_CATEGORY).on(COMPETITION_CATEGORY.COMPETITION_ID.eq(COMPETITION.ID))
            .join(COMPETITION_CATEGORY_REGISTRATION).on(
                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(
                    COMPETITION_CATEGORY.ID
                )
            )
            .join(PLAYER_REGISTRATION)
            .on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
            .join(CLUB).on(CLUB.ID.eq(PLAYER.CLUB_ID))
            .where(COMPETITION.ID.eq(competitionId))
            .fetch()

        return records.map {
            PlayerWithClubDTO(
                it.getValue(PLAYER.ID),
                it.getValue(PLAYER.FIRST_NAME),
                it.getValue(PLAYER.LAST_NAME),
                ClubDTO(
                    it.getValue(CLUB.ID),
                    it.getValue(CLUB.NAME),
                    it.getValue(CLUB.ADDRESS)
                ),
                it.getValue(PLAYER.DATE_OF_BIRTH)
            )
        }
    }

    override fun getCategoriesAndPlayersInCompetition(competitionId: Int): List<Pair<CategoryDTO, PlayerWithClubDTO>> {
        val records = dslContext.select(
            PLAYER.ID,
            PLAYER.FIRST_NAME,
            PLAYER.LAST_NAME,
            PLAYER.DATE_OF_BIRTH,
            CLUB.ID,
            CLUB.NAME,
            CLUB.ADDRESS,
            CATEGORY.ID,
            CATEGORY.CATEGORY_NAME,
            CATEGORY.CATEGORY_TYPE
        )
            .from(COMPETITION)
            .join(COMPETITION_CATEGORY).on(COMPETITION_CATEGORY.COMPETITION_ID.eq(COMPETITION.ID))
            .join(COMPETITION_CATEGORY_REGISTRATION).on(
                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(
                    COMPETITION_CATEGORY.ID
                )
            )
            .join(CATEGORY).on(CATEGORY.ID.eq(COMPETITION_CATEGORY.CATEGORY))
            .join(PLAYER_REGISTRATION)
            .on(PLAYER_REGISTRATION.REGISTRATION_ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .join(PLAYER).on(PLAYER.ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
            .join(CLUB).on(CLUB.ID.eq(PLAYER.CLUB_ID))
            .where(COMPETITION.ID.eq(competitionId))
            .fetch()

        return records.map {
            Pair(
                CategoryDTO(
                    it.getValue(CATEGORY.ID),
                    it.getValue(CATEGORY.CATEGORY_NAME),
                    it.getValue(CATEGORY.CATEGORY_TYPE)
                ),
                PlayerWithClubDTO(
                    it.getValue(PLAYER.ID),
                    it.getValue(PLAYER.FIRST_NAME),
                    it.getValue(PLAYER.LAST_NAME),
                    ClubDTO(
                        it.getValue(CLUB.ID),
                        it.getValue(CLUB.NAME),
                        it.getValue(CLUB.ADDRESS)
                    ),
                    it.getValue(PLAYER.DATE_OF_BIRTH)
                )
            )
        }
    }

    override fun getRegistrationFor(spec: RegistrationSinglesSpec): RegistrationSinglesDTO {
        val record = getRegistration(spec.playerId, spec.competitionCategoryId)

        return RegistrationSinglesDTO(
            record.getValue(REGISTRATION.ID),
            record.getValue(PLAYER_REGISTRATION.PLAYER_ID),
            record.getValue(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID),
            record.getValue(REGISTRATION.REGISTRATION_DATE)
        )
    }

    override fun getRegistrationFor(spec: RegistrationDoublesSpec): RegistrationDoublesDTO {
        val registrationOne = getRegistration(spec.playerOneId, spec.competitionCategoryId)
        val registrationTwo = getRegistration(spec.playerTwoId, spec.competitionCategoryId)
        return RegistrationDoublesDTO(
            registrationOne.getValue(REGISTRATION.ID),
            registrationOne.getValue(PLAYER_REGISTRATION.PLAYER_ID),
            registrationTwo.getValue(PLAYER_REGISTRATION.PLAYER_ID),
            registrationOne.getValue(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID),
            registrationOne.getValue(REGISTRATION.REGISTRATION_DATE)
        )
    }

    override fun getRegistrationsIn(competitionCategoryId: Int): List<RegistrationDTO> {
        val records = dslContext.select()
            .from(REGISTRATION)
            .join(COMPETITION_CATEGORY_REGISTRATION)
            .on(REGISTRATION.ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .fetchInto(REGISTRATION)
        return records.map { RegistrationDTO(it.id, it.registrationDate) }
    }

    override fun getPlayersFrom(registrationId: Int): List<PlayerDTO> {
        val records = dslContext.select().from(REGISTRATION).join(PLAYER_REGISTRATION).on(
            REGISTRATION.ID.eq(
                PLAYER_REGISTRATION.REGISTRATION_ID
            )
        ).join(PLAYER).on(PLAYER_REGISTRATION.PLAYER_ID.eq(PLAYER.ID)).where(
            REGISTRATION.ID.eq(registrationId)
        ).fetchInto(
            PLAYER
        )

        return records.map { PlayerDTO(it.id, it.firstName, it.lastName, it.clubId, it.dateOfBirth) }
    }

    @Throws(NotFoundException::class)
    override fun remove(registrationId: Int) {
        val success = dslContext.deleteFrom(REGISTRATION).where(REGISTRATION.ID.eq(registrationId)).execute() > 0
        if (!success) {
            throw NotFoundException("Could not delete. The registration with id $registrationId was not found.")
        }
    }

    override fun getRegistrationRanking(competitionCategory: CompetitionCategoryDTO): List<RegistrationRankingDTO> {
        val rankFieldName = "rank"
        val records = dslContext.select(
            REGISTRATION.ID,
            COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID,
            sum(getRankField(competitionCategory.category)).over(partitionBy(REGISTRATION.ID)).`as`(rankFieldName)
        )
            .distinctOn(REGISTRATION.ID)
            .from(REGISTRATION)
            .join(COMPETITION_CATEGORY_REGISTRATION)
            .on(REGISTRATION.ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .join(PLAYER_REGISTRATION).on(REGISTRATION.ID.eq(PLAYER_REGISTRATION.REGISTRATION_ID))
            .join(PLAYER_RANKING).on(PLAYER_RANKING.PLAYER_ID.eq(PLAYER_REGISTRATION.PLAYER_ID))
            .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategory.id))
            .fetch()

        return records.map {
            RegistrationRankingDTO(
                it.getValue(REGISTRATION.ID),
                it.getValue(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID),
                it.getValue(rankFieldName).toString().toInt()
            )
        }
    }

    private fun getRankField(category: CategorySpec): TableField<PlayerRankingRecord, Int>? {
        return if (category.type == "DOUBLES") {
            PLAYER_RANKING.RANK_DOUBLE
        } else {
            PLAYER_RANKING.RANK_SINGLE
        }
    }

    /**
     * Will return one row as RegistrationId, RegistrationDate, PlayerId, CompetitionCategoryId
     */
    private fun getRegistration(playerId: Int, competitionCategoryId: Int): Record4<Int, LocalDate, Int, Int> {
        return dslContext.select(
            REGISTRATION.ID, REGISTRATION.REGISTRATION_DATE, PLAYER_REGISTRATION.PLAYER_ID,
            COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID
        )
            .from(REGISTRATION)
            .join(PLAYER_REGISTRATION).on(REGISTRATION.ID.eq(PLAYER_REGISTRATION.REGISTRATION_ID))
            .join(COMPETITION_CATEGORY_REGISTRATION)
            .on(REGISTRATION.ID.eq(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID))
            .where(
                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)
                    .and(PLAYER_REGISTRATION.PLAYER_ID.eq(playerId))
            )
            .fetchOne() ?: throw NotFoundException("Combination of $playerId and $competitionCategoryId not found.")
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
        competitionCategory: Int
    ): CompetitionCategoryRegistrationRecord {
        val record = dslContext.newRecord(COMPETITION_CATEGORY_REGISTRATION)
        record.registrationId = registrationId
        record.seed = null
        record.competitionCategoryId = competitionCategory
        record.store()
        return record
    }

    override fun setSeeds(registrationSeeds: List<RegistrationSeedDTO>) {
        for (registration in registrationSeeds) {
            dslContext.update(COMPETITION_CATEGORY_REGISTRATION)
                .set(COMPETITION_CATEGORY_REGISTRATION.SEED, registration.seed)
                .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(registration.competitionCategoryId)
                    .and(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(registration.registrationId)))
                .execute()
        }
    }
}


