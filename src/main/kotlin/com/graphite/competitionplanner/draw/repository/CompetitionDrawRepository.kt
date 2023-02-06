package com.graphite.competitionplanner.draw.repository

import com.graphite.competitionplanner.Tables.*
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.common.repository.BaseRepository
import com.graphite.competitionplanner.competitioncategory.interfaces.CompetitionCategoryStatus
import com.graphite.competitionplanner.competitioncategory.interfaces.ICompetitionCategoryRepository
import com.graphite.competitionplanner.draw.domain.*
import com.graphite.competitionplanner.draw.interfaces.CompetitionCategoryDrawDTO
import com.graphite.competitionplanner.draw.interfaces.ICompetitionDrawRepository
import com.graphite.competitionplanner.draw.interfaces.RegistrationSeedDTO
import com.graphite.competitionplanner.match.domain.MatchType
import com.graphite.competitionplanner.registration.domain.Registration
import com.graphite.competitionplanner.registration.domain.asInt
import com.graphite.competitionplanner.tables.records.*
import org.jetbrains.annotations.NotNull
import org.jooq.DSLContext
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Repository

@Repository
class CompetitionDrawRepository(
        dslContext: DSLContext,
        val competitionCategoryRepository: ICompetitionCategoryRepository,
        @Lazy val getDraw: GetDraw
) : BaseRepository(dslContext),
    ICompetitionDrawRepository {

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
        asTransaction {
            competitionCategoryRepository.setStatus(draw.competitionCategoryId, CompetitionCategoryStatus.DRAWN)
            when (draw) {
                is CupDrawSpec -> storeCupDraw(draw)
                is PoolAndCupDrawSpec -> storePoolAndCupDraw(draw)
                is PoolDrawSpec -> storePoolDraw(draw)
                is PoolAndCupDrawWithBPlayoffSpec -> storePoolAndCupDrawWithBPlayoff(draw)
            }
        }
        return get(draw.competitionCategoryId)
    }

    override fun get(competitionCategoryId: Int): CompetitionCategoryDrawDTO {
        return getDraw.execute(competitionCategoryId)
    }


    override fun delete(competitionCategoryId: Int) {
        asTransaction {
            dslContext.deleteFrom(MATCH).where(MATCH.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)).execute()
            dslContext.deleteFrom(POOL).where(POOL.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)).execute()
        }
    }

    private fun storeCupDraw(draw: CupDrawSpec) {
        val records = draw.matches.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(records).execute()
    }

    private fun storePoolDraw(draw: PoolDrawSpec) {
        val poolRecords: List<PoolRecord> = draw.pools.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(poolRecords).execute()

        val records: List<MatchRecord> =
                draw.pools.flatMap { group -> group.matches.map { it.toRecord(draw.competitionCategoryId, group.name) } }
        dslContext.batchInsert(records).execute()
    }

    private fun storePoolAndCupDraw(draw: PoolAndCupDrawSpec) {
        val poolRecords: List<PoolRecord> = draw.pools.map { it.toRecord(draw.competitionCategoryId) }
        dslContext.batchInsert(poolRecords).execute()

        val playerOffMatchRecords: List<MatchRecord> = draw.matches.map { it.toRecord(draw.competitionCategoryId) }
        val groupMatchRecords: List<MatchRecord> =
                draw.pools.flatMap { group -> group.matches.map { it.toRecord(draw.competitionCategoryId, group.name) } }
        dslContext.batchInsert(playerOffMatchRecords + groupMatchRecords).execute()

        val poolToPlayoffMapRecords: List<PoolToPlayoffMapRecord> = createPoolToPlayoffMapRecords(draw)
        dslContext.batchInsert(poolToPlayoffMapRecords).execute()
    }

    private fun createPoolToPlayoffMapRecords(draw: PoolAndCupDrawSpec): List<PoolToPlayoffMapRecord> {
        val poolRecords =
                dslContext.selectFrom(POOL).where(POOL.COMPETITION_CATEGORY_ID.eq(draw.competitionCategoryId)).fetch()
        val firstRound = draw.matches.maxByOrNull { it.round }!!.round
        val matchesFirstRound = dslContext.selectFrom(MATCH).where(
                MATCH.COMPETITION_CATEGORY_ID.eq(draw.competitionCategoryId).and(MATCH.GROUP_OR_ROUND.eq(firstRound.name))
        ).fetch()

        val poc1: List<PoolToPlayoffMapRecord> =
                draw.matches.filter { it.round == firstRound }.filterNot { it.registrationOneId is Registration.Bye }
                        .map { playOffMatch ->
                            dslContext.newRecord(POOL_TO_PLAYOFF_MAP).apply<@NotNull PoolToPlayoffMapRecord> {
                                competitionCategoryId = draw.competitionCategoryId
                                poolId =
                                        poolRecords.first {
                                            playOffMatch.registrationOneId.toString()
                                                    .dropLast(1) == it.name // Drop group position number
                                        }.id
                                poolPosition = playOffMatch.registrationOneId.toString().last().digitToInt()
                                matchId = matchesFirstRound.first { it.matchOrderNumber == playOffMatch.order }.id
                                matchRegistrationPosition = 1 // registrationOneId
                            }
                        }
        val poc2: List<PoolToPlayoffMapRecord> =
                draw.matches.filter { it.round == firstRound }.filterNot { it.registrationTwoId is Registration.Bye }
                        .map { playOffMatch ->
                            dslContext.newRecord(POOL_TO_PLAYOFF_MAP).apply<@NotNull PoolToPlayoffMapRecord> {
                                competitionCategoryId = draw.competitionCategoryId
                                poolId =
                                        poolRecords.first {
                                            playOffMatch.registrationTwoId.toString()
                                                    .dropLast(1) == it.name // Drop group position number
                                        }.id
                                poolPosition = playOffMatch.registrationTwoId.toString().last().digitToInt()
                                matchId = matchesFirstRound.first { it.matchOrderNumber == playOffMatch.order }.id
                                matchRegistrationPosition = 2 // registrationTwoId
                            }
                        }

        return poc1 + poc2
    }

    private fun storePoolAndCupDrawWithBPlayoff(draw: PoolAndCupDrawWithBPlayoffSpec) {
1
    }

    override fun getPool(competitionCategoryId: Int, poolName: String): PoolRecord {
        return dslContext
                .selectFrom(POOL)
                .where(POOL.COMPETITION_CATEGORY_ID.eq(competitionCategoryId).and(POOL.NAME.eq(poolName)))
                .fetchOneInto(POOL)
                ?: throw NotFoundException("Pool $poolName in category $competitionCategoryId not found.")

    }

    override fun isPoolFinished(poolId: Int): Boolean {
        return dslContext.fetchExists(
                dslContext.selectFrom(POOL_RESULT).where(POOL_RESULT.POOL_ID.eq(poolId))
        )
    }

    override fun getPoolResult(poolId: Int): List<PoolResultRecord> {
        return dslContext.selectFrom(POOL_RESULT).where(POOL_RESULT.POOL_ID.eq(poolId)).fetchInto(POOL_RESULT)
    }

    override fun deletePools(competitionCategoryId: Int) {
        dslContext.deleteFrom(POOL).where(POOL.COMPETITION_CATEGORY_ID.eq(competitionCategoryId)).execute()
    }

    override fun clearPoolTable() {
        dslContext.deleteFrom(POOL).execute()
    }

    override fun deleteSeeding(competitionCategoryId: Int) {
        dslContext.update(COMPETITION_CATEGORY_REGISTRATION)
                .setNull(COMPETITION_CATEGORY_REGISTRATION.SEED)
                .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
                .execute()
    }

    override fun getSeeding(competitionCategoryId: Int): List<RegistrationSeedDTO> {
        val records = dslContext.select()
                .from(COMPETITION_CATEGORY_REGISTRATION)
                .where(COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(competitionCategoryId))
                .orderBy(COMPETITION_CATEGORY_REGISTRATION.SEED.asc().nullsLast())
                .fetchInto(COMPETITION_CATEGORY_REGISTRATION)
        return records.map { RegistrationSeedDTO(Registration.Real(it.registrationId), it.competitionCategoryId, it.seed) }
    }

    override fun storeSeeding(registrationSeeds: List<RegistrationSeedDTO>) {
        dslContext.batched {
            for (dto in registrationSeeds) {
                dslContext.update(COMPETITION_CATEGORY_REGISTRATION)
                        .set(COMPETITION_CATEGORY_REGISTRATION.SEED, dto.seed)
                        .where(
                                COMPETITION_CATEGORY_REGISTRATION.COMPETITION_CATEGORY_ID.eq(dto.competitionCategoryId)
                                        .and(COMPETITION_CATEGORY_REGISTRATION.REGISTRATION_ID.eq(dto.registration.id))
                        ).execute()
            }
        }
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
        record.firstRegistrationId = this.registrationOneId.asInt()
        record.secondRegistrationId = this.registrationTwoId.asInt()
        record.matchOrderNumber = this.order
        record.groupOrRound = this.round.name
        record.winner = this.winner?.asInt()
        return record
    }
}