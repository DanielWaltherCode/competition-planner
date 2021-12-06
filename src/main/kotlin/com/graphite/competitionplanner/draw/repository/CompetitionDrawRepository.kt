package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.club.interfaces.ClubDTO
import com.graphite.competitionplanner.competitioncategory.entity.Round
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.*
import com.graphite.competitionplanner.draw.service.MatchType
import com.graphite.competitionplanner.draw.service.PoolDrawHelper
import com.graphite.competitionplanner.player.interfaces.PlayerWithClubDTO
import com.graphite.competitionplanner.tables.records.MatchRecord
import com.graphite.competitionplanner.tables.records.PoolDrawRecord
import com.graphite.competitionplanner.tables.records.PoolRecord
import com.graphite.competitionplanner.tables.records.PoolToPlayoffMapRecord
import org.jetbrains.annotations.NotNull
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.Record13
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

    fun clearTable() {
        dslContext.deleteFrom(POOL_DRAW).execute()
        dslContext.deleteFrom(POOL_TO_PLAYOFF_MAP).execute()
        dslContext.deleteFrom(POOL).execute()
    }


    override fun store(draw: CompetitionCategoryDrawSpec): CompetitionCategoryDrawDTO {
        return when (draw) {
            is CupDrawSpec -> storeCupDraw(draw)
            is PoolAndCupDrawSpec -> storePoolAndCupDraw(draw)
            is PoolDrawSpec -> storePoolDraw(draw)
        }
    }

    override fun get(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        val records = getDataRows(competitionCategoryId)
        val playOffMatches = constructPlayOffMatches(records.filter { it.groupOrRound.isRound() })
        val updatedPlayoffMatches = updatePlaceholderNamesInFirstRound(competitionCategoryId, playOffMatches)
        val groupDraws = constructGroupDraws(records.filter { !it.groupOrRound.isRound() })

        return CompetitionCategoryDrawDTO(
            competitionCategoryId,
            updatedPlayoffMatches,
            groupDraws
        )
    }

    /**
     * Update the name of all placeholder matches in the first round with the name of the group position.
     */
    private fun updatePlaceholderNamesInFirstRound(
        competitionCategoryId: Int,
        playOffMatches: List<PlayOffMatchDTO>
    ): List<PlayOffMatchDTO> {
        val poolToPlayOffMap = constructPoolToPlayoffMap(competitionCategoryId)
        val potentialMatchUpdateIds = poolToPlayOffMap.map { it.playoffPosition.matchId }
        val matchesThatCanBeUpdated = playOffMatches.filter { potentialMatchUpdateIds.contains(it.id) }
        val matchesThatCannotBeUpdated = playOffMatches.filterNot { potentialMatchUpdateIds.contains(it.id) }

        val placeholder = Registration.Placeholder()
        val updatedMatches = mutableListOf<PlayOffMatchDTO>()
        for (match in matchesThatCanBeUpdated) {
            var player1 = match.player1
            var player2 = match.player2
            if (match.player1.any { it.firstName == placeholder.toString() }) {
                val player1Map = poolToPlayOffMap.first { it.playoffPosition.matchId == match.id && it.playoffPosition.position == 1}
                player1 = match.player1.map { updateNameOfPlaceholder(it, player1Map) }
            }
            if (match.player2.any { it.firstName == placeholder.toString() }) {
                val player2Map = poolToPlayOffMap.first { it.playoffPosition.matchId == match.id && it.playoffPosition.position == 2}
                player2 = match.player2.map { updateNameOfPlaceholder(it, player2Map) }
            }

            updatedMatches.add(
                PlayOffMatchDTO(
                    match.id,
                    player1,
                    player2,
                    match.order,
                    match.round,
                    match.winner
                )
            )

        }

        return matchesThatCannotBeUpdated + updatedMatches
    }

    private fun updateNameOfPlaceholder(player: PlayerWithClubDTO, groupToPlayoff: GroupToPlayoff): PlayerWithClubDTO {
        return PlayerWithClubDTO(
            player.id,
            "${groupToPlayoff.groupPosition.groupName}${groupToPlayoff.groupPosition.position}",
            player.lastName,
            player.club,
            player.dateOfBirth
        )
    }

    private fun constructPoolToPlayoffMap(competitionCategoryId: Int): List<GroupToPlayoff> {
        val records = dslContext.select()
            .from(POOL_TO_PLAYOFF_MAP)
            .join(POOL).on(POOL_TO_PLAYOFF_MAP.POOL_ID.eq(POOL.ID))
            .where(POOL_TO_PLAYOFF_MAP.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
            .orderBy(POOL_TO_PLAYOFF_MAP.MATCH_ID).fetch().toList()

        return processPoolToPlayoffMapRecords(records)
    }

    private fun processPoolToPlayoffMapRecords(records: List<Record>): List<GroupToPlayoff> {
        return records.map {
            GroupToPlayoff(
                PlayoffPosition(
                    it.get(POOL_TO_PLAYOFF_MAP.MATCH_ID),
                    it.get(POOL_TO_PLAYOFF_MAP.MATCH_REGISTRATION_POSITION),
                ),
                GroupPosition(
                    it.getValue(POOL.NAME),
                    it.getValue(POOL_TO_PLAYOFF_MAP.POOL_POSITION)
                )
            )
        }
    }

    override fun delete(competitionCategoryId: Int) {
        dslContext.deleteFrom(MATCH).where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)).execute()
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
            val winner = rows.filter { it.registrationNumber == rows.first().winner }.map { it.toPlayerWithClubDto() }
            listOf(
                PlayOffMatchDTO(
                    matchId,
                    player1,
                    player2,
                    order,
                    round,
                    winner
                )
            ) + constructPlayOffMatches(records.drop(rows.count()))
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

    private fun constructGroupDraws(records: List<MatchPlayerRecord>): List<GroupDrawDTO> {
        val groupNames = records.distinctBy { it.groupOrRound }.map { it.groupOrRound }
        val groupDraws = mutableListOf<GroupDrawDTO>()
        for (name in groupNames) {
            groupDraws.add(constructGroupDraw(name, records.filter { it.groupOrRound == name }))
        }
        return groupDraws
    }

    private fun constructGroupDraw(name: String, records: List<MatchPlayerRecord>): GroupDrawDTO {
        return GroupDrawDTO(
            name,
            records.distinctBy { it.playerId }.map { it.toPlayerWithClubDto() },
            constructGroupMatches(records)
        )
    }

    private fun constructGroupMatches(records: List<MatchPlayerRecord>): List<GroupMatchDTO> {
        return if (records.isEmpty()) {
            return emptyList()
        } else {
            val matchId = records.first().matchId
            val rows = records.filter { it.matchId == matchId }
            val player1 =
                rows.filter { it.registrationOrder == RegistrationOrder.First }.map { it.toPlayerWithClubDto() }
            val player2 =
                rows.filter { it.registrationOrder == RegistrationOrder.Second }.map { it.toPlayerWithClubDto() }
            val winner = rows.filter { it.registrationNumber == rows.first().winner }.map { it.toPlayerWithClubDto() }
            listOf(GroupMatchDTO(matchId, player1, player2, winner)) + constructGroupMatches(records.drop(rows.count()))
        }
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
                m.WINNER,
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
                        m.WINNER,
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
        RecordMapper<Record13<Int, String, Int, Int, String, Int, Int?, String, String, LocalDate, Int, String, String>, MatchPlayerRecord> {
        override fun map(p0: Record13<Int, String, Int, Int, String, Int, Int?, String, String, LocalDate, Int, String, String>?): MatchPlayerRecord {
            return MatchPlayerRecord(
                p0?.get("match_id", MATCH.ID.type)!!,
                p0.getValue(MATCH.GROUP_OR_ROUND)!!,
                p0.getValue(MATCH.MATCH_ORDER_NUMBER)!!,
                p0.getValue("registration_id", REGISTRATION.ID.type),
                RegistrationOrder.valueOf(p0.getValue("registration_order", String::class.java)),
                p0.getValue(MATCH.WINNER),
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
        val winner: Int?, // Null if winner has not been decided yet
        val playerId: Int,
        val firstName: String,
        val lastName: String,
        val dateOfBirth: LocalDate,
        val clubId: Int,
        val clubName: String,
        val clubAddress: String
    )

    enum class RegistrationOrder { First, Second }

    private fun storeCupDraw(draw: CupDrawSpec): CompetitionCategoryDrawDTO {
        val records = draw.matches.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(records).execute()
        return get(draw.competitionCategoryId)
    }

    private fun storePoolDraw(draw: PoolDrawSpec): CompetitionCategoryDrawDTO {
        val poolRecords = draw.pools.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(poolRecords).execute()

        val records =
            draw.pools.flatMap { group -> group.matches.map { it.toRecord(draw.competitionCategoryId, group.name) } }
        dslContext.batchInsert(records).execute()
        return get(draw.competitionCategoryId)
    }

    private fun storePoolAndCupDraw(draw: PoolAndCupDrawSpec): CompetitionCategoryDrawDTO {
        val poolRecords = draw.pools.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(poolRecords).execute()

        val playerOffMatchRecords = draw.matches.map { it.toRecord(draw.competitionCategoryId) }
        val groupMatchRecords =
            draw.pools.flatMap { group -> group.matches.map { it.toRecord(draw.competitionCategoryId, group.name) } }
        dslContext.batchInsert(playerOffMatchRecords + groupMatchRecords).execute()

        val poolToPlayoffMapRecords = createPoolToPlayoffMapRecords(draw)
        dslContext.batchInsert(poolToPlayoffMapRecords).execute()

        return get(draw.competitionCategoryId)
    }

    private fun createPoolToPlayoffMapRecords(draw: PoolAndCupDrawSpec): List<PoolToPlayoffMapRecord> {
        val poolRecords =
            dslContext.selectFrom(POOL).where(POOL.COMPETITION_CATEGORY_ID.eq(draw.competitionCategoryId)).fetch()
        val firstRound = draw.matches.maxByOrNull { it.round }!!.round
        val matchesFirstRound = dslContext.selectFrom(MATCH).where(
            MATCH.COMPETITION_CATEGORY_ID.eq(draw.competitionCategoryId).and(MATCH.GROUP_OR_ROUND.eq(firstRound.name))
        ).fetch()

        val poc1 = draw.matches.filter { it.round == firstRound }.filterNot { it.registrationOneId is Registration.Bye }
            .map { playOffMatch ->
                dslContext.newRecord(POOL_TO_PLAYOFF_MAP).apply<@NotNull PoolToPlayoffMapRecord> {
                    competitionCategoryId = draw.competitionCategoryId
                    poolId =
                        poolRecords.first {
                            playOffMatch.registrationOneId.toString().first().toString() == it.name
                        }.id
                    poolPosition = playOffMatch.registrationOneId.toString().last().digitToInt()
                    matchId = matchesFirstRound.first { it.matchOrderNumber == playOffMatch.order }.id
                    matchRegistrationPosition = 1 // registrationOneId
                }
            }
        val poc2 = draw.matches.filter { it.round == firstRound }.filterNot { it.registrationTwoId is Registration.Bye }
            .map { playOffMatch ->
                dslContext.newRecord(POOL_TO_PLAYOFF_MAP).apply<@NotNull PoolToPlayoffMapRecord> {
                    competitionCategoryId = draw.competitionCategoryId
                    poolId =
                        poolRecords.first {
                            playOffMatch.registrationTwoId.toString().first().toString() == it.name
                        }.id
                    poolPosition = playOffMatch.registrationTwoId.toString().last().digitToInt()
                    matchId = matchesFirstRound.first { it.matchOrderNumber == playOffMatch.order }.id
                    matchRegistrationPosition = 2 // registrationTwoId
                }
            }

        return poc1 + poc2
    }

    private fun Pool.toRecord(competitionCategoryId: Int): PoolRecord {
        val record = dslContext.newRecord(POOL)
        record.competitionCategoryId = competitionCategoryId
        record.name = this.name
        return record
    }

    private fun PoolMatch.toRecord(competitionCategoryId: Int, groupName: String): MatchRecord {
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