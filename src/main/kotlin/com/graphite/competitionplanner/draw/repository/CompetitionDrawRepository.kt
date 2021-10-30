package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.domain.entity.Round
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.GroupDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.PlayOffMatchDTO
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.draw.service.PoolDrawHelper
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.tables.records.MatchRecord
import com.graphite.competitionplanner.tables.records.PoolDrawRecord
import org.jooq.DSLContext
import org.jooq.Record12
import org.jooq.RecordMapper
import org.jooq.impl.DSL.inline
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
class CompetitionDrawRepository(val dslContext: DSLContext) : ICompetitionDrawRepository {

    fun addPoolDraw(poolDrawDto: PoolDrawHelper) {
        val pooldrawRecord = dslContext.newRecord(POOL_DRAW)
        pooldrawRecord.registrationId = poolDrawDto.registrationId
        pooldrawRecord.competitionCategoryId = poolDrawDto.competitionCategoryId
        pooldrawRecord.groupName = poolDrawDto.groupName
        pooldrawRecord.playerNumber = poolDrawDto.playerNumber
        pooldrawRecord.store()
    }

    fun getPoolDraw(competitionCategoryId: Int, groupName: String): List<PoolDrawRecord> {
        return dslContext
            .selectFrom(POOL_DRAW)
            .where(POOL_DRAW.COMPETITION_CATEGORY_ID.eq(competitionCategoryId).and(POOL_DRAW.GROUP_NAME.eq(groupName)))
            .fetchInto(POOL_DRAW)
    }

    fun deleteGroupsInCategory(competitionCategoryId: Int) {
        dslContext.deleteFrom(POOL_DRAW).where(POOL_DRAW.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)).execute()
    }

    fun clearTable() = dslContext.deleteFrom(POOL_DRAW).execute()

    override fun store(draw: CompetitionCategoryDrawSpec) {
        when (draw) {
            is PlayOffDrawSpec -> storePlayoff(draw)
            is GroupsDrawSpec -> storeGroup(draw)
        }
    }

    override fun get(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val records = getDataRows(competitionCategoryId)
        val playOffMatches = constructPlayOffMatches(records.filter { it.groupOrRound.isRound() })
        val groupMatches = constructGroupDraws(records.filter { !it.groupOrRound.isRound() })

        return CompetitionCategoryDrawDTO(
            competitionCategoryId,
            playOffMatches,
            groupMatches
        )
    }

    private fun String.isRound(): Boolean {
        return Round.values().any { it.name == this }
    }

    private fun constructPlayOffMatches(records: List<MatchPlayerRecord>): List<PlayOffMatchDTO> {
        return if (records.isEmpty()) {
            emptyList()
        } else {
            val matchId = records.first().matchId
            val rows = records.filter { it.matchId == matchId }
            val player1 =
                rows.filter { it.registrationOrder == RegistrationOrder.First }.map { it.toPlayerWithClubDto() }
            val player2 =
                rows.filter { it.registrationOrder == RegistrationOrder.Second }.map { it.toPlayerWithClubDto() }
            val order = rows.first().matchOrder
            val round = Round.valueOf(rows.first().groupOrRound)
            listOf(PlayOffMatchDTO(player1,
                player2,
                order,
                round)) + constructPlayOffMatches(records.drop(rows.count()))
        }
    }

    private fun MatchPlayerRecord.toPlayerWithClubDto(): PlayerWithClubDTO {
        return PlayerWithClubDTO(
            this.playerId,
            this.firstName,
            this.lastName,
            ClubDTO(
                this.clubId,
                this.clubName,
                this.clubAddress
            ),
            this.dateOfBirth
        )
    }

    private fun constructGroupDraws(dataRows: List<MatchPlayerRecord>): List<GroupDrawDTO> {
        // TODO: Implement
        return emptyList()
    }

    private fun getDataRows(competitionCategoryId: Int): List<MatchPlayerRecord> {
        val r1 = REGISTRATION.`as`("r1")
        val pr1 = PLAYER_REGISTRATION.`as`("pr1")
        val p1 = PLAYER.`as`("p1")
        val c1 = CLUB.`as`("c1")

        val r2 = REGISTRATION.`as`("r2")
        val pr2 = PLAYER_REGISTRATION.`as`("pr2")
        val p2 = PLAYER.`as`("p2")
        val c2 = CLUB.`as`("c2")

        val m = MATCH.`as`("m")

        return dslContext
            .select(
                m.ID.`as`("match_id"),
                m.GROUP_OR_ROUND,
                m.MATCH_ORDER_NUMBER,
                m.FIRST_REGISTRATION_ID.`as`("registration_id"),
                inline(RegistrationOrder.First.name).`as`("registration_order"), // Keeping track from which select
                p1.ID.`as`("player_id"),
                p1.FIRST_NAME,
                p1.LAST_NAME,
                p1.DATE_OF_BIRTH,
                c1.ID.`as`("club_id"),
                c1.NAME,
                c1.ADDRESS
            )
            .from(m)
            .join(r1).on(r1.ID.eq(m.FIRST_REGISTRATION_ID))
            .join(pr1).on(pr1.REGISTRATION_ID.eq(r1.ID))
            .join(p1).on(p1.ID.eq(pr1.PLAYER_ID))
            .join(c1).on(c1.ID.eq(p1.CLUB_ID))
            .where(m.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .union(
                dslContext
                    .select(
                        m.ID.`as`("match_id"),
                        m.GROUP_OR_ROUND,
                        m.MATCH_ORDER_NUMBER,
                        m.SECOND_REGISTRATION_ID.`as`("registration_id"),
                        inline(RegistrationOrder.Second.name).`as`("registration_order"), // Keeping track from which select
                        p2.ID.`as`("player_id"),
                        p2.FIRST_NAME,
                        p2.LAST_NAME,
                        p2.DATE_OF_BIRTH,
                        c2.ID.`as`("club_id"),
                        c2.NAME,
                        c2.ADDRESS
                    )
                    .from(m)
                    .join(r2).on(r2.ID.eq(m.SECOND_REGISTRATION_ID))
                    .join(pr2).on(pr2.REGISTRATION_ID.eq(r2.ID))
                    .join(p2).on(p2.ID.eq(pr2.PLAYER_ID))
                    .join(c2).on(c2.ID.eq(p2.CLUB_ID))
                    .where(m.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            ).orderBy(m.ID.`as`("match_id").desc())
            .fetch(MatchPlayerRecordMapper())
    }

    class MatchPlayerRecordMapper :
        RecordMapper<Record12<Int, String, Int, Int, String, Int, String, String, LocalDate, Int, String, String>, MatchPlayerRecord> {
        override fun map(p0: Record12<Int, String, Int, Int, String, Int, String, String, LocalDate, Int, String, String>?): MatchPlayerRecord {
            return MatchPlayerRecord(
                p0?.get("match_id", MATCH.ID.type)!!,
                p0.getValue(MATCH.GROUP_OR_ROUND)!!,
                p0.getValue(MATCH.MATCH_ORDER_NUMBER)!!,
                p0.getValue("registration_id", REGISTRATION.ID.type),
                RegistrationOrder.valueOf(p0.getValue("registration_order", String::class.java)),
                p0.getValue("player_id", PLAYER.ID.type)!!,
                p0.getValue(PLAYER.FIRST_NAME)!!,
                p0.getValue(PLAYER.LAST_NAME)!!,
                p0.getValue(PLAYER.DATE_OF_BIRTH),
                p0.getValue("club_id", CLUB.ID.type)!!,
                p0.getValue(CLUB.NAME)!!,
                p0.getValue(CLUB.ADDRESS)!!
            )
        }
    }

    class MatchPlayerRecord(
        val matchId: Int,
        val groupOrRound: String,
        val matchOrder: Int,
        val registrationNumber: Int,
        val registrationOrder: RegistrationOrder, // Indicates whether this row came from first or second select i.e. first_registration or second_registration
        val playerId: Int,
        val firstName: String,
        val lastName: String,
        val dateOfBirth: LocalDate,
        val clubId: Int,
        val clubName: String,
        val clubAddress: String
    )

    enum class RegistrationOrder { First, Second }

    private fun storePlayoff(draw: PlayOffDrawSpec) {
        val records = draw.matches.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(records).execute()
    }

    private fun storeGroup(draw: GroupsDrawSpec) {
        // TODO: Store the group ?
        val playerOffMatchRecords = draw.matches.map { it.toRecord(draw.competitionCategoryId) }
        val groupMatchRecords =
            draw.groups.flatMap { group -> group.matches.map { it.toRecord(draw.competitionCategoryId, group.name) } }
        dslContext.batchInsert(playerOffMatchRecords + groupMatchRecords).execute()
    }

    private fun GroupMatch.toRecord(competitionCategoryId: Int, groupName: String): MatchRecord {
        val record = dslContext.newRecord(MATCH)
        record.startTime = null
        record.endTime = null
        record.competitionCategoryId = competitionCategoryId
        record.matchType = MatchType.GROUP.name
        record.firstRegistrationId = this.registrationOneId.asInt()
        record.secondRegistrationId = this.registrationTwoId.asInt()
        record.matchOrderNumber = 0
        record.groupOrRound = groupName
        return record
    }

    private fun PlayOffMatch.toRecord(competitionCategoryId: Int): MatchRecord {
        val record = dslContext.newRecord(MATCH)
        record.startTime = null
        record.endTime = null
        record.competitionCategoryId = competitionCategoryId
        record.matchType = MatchType.PLAYOFF.name
        record.firstRegistrationId = this.registrationOneId.asInt()// matchSpec.firstRegistrationId
        record.secondRegistrationId = this.registrationTwoId.asInt()
        record.matchOrderNumber = this.order
        record.groupOrRound = this.round.name
        return record
    }

    private fun Registration.asInt(): Int {
        return when (this) {
            is Registration.Real -> this.id
            is Registration.Bye -> 0 // TODO: This feels a bit fragile. These IDs are set in SetupTestData
            is Registration.Placeholder -> 1 // TODO: Fetch PlaceHolder ID once
        }
    }

}