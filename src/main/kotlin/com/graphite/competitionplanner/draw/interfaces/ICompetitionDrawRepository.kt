package com.graphite.competitionplanner.draw.interfaces

import com.graphite.competitionplanner.draw.domain.CompetitionCategoryDrawSpec
import com.graphite.competitionplanner.tables.records.PoolRecord
import com.graphite.competitionplanner.tables.records.PoolResultRecord

interface ICompetitionDrawRepository {

    /**
     * Store the draw of a specific competition category
     *
     * @param draw The specification of the draw
     * @return The newly stored draw
     */
    fun store(draw: CompetitionCategoryDrawSpec): CompetitionCategoryDrawDTO

    /**
     * Returns the matches from a resulting draw of a specific competition category
     *
     * @param competitionCategoryId Id of the competition category
     * @return The matches for a competition category
     */
    fun get(competitionCategoryId: Int): CompetitionCategoryDrawDTO

    /**
     *  Delete the draw for the given competition category. This will delete any matches and groups, as well as mark
     *  the competition category as not drawn.
     *
     *  @param competitionCategoryId Id of the competition category
     */
    fun delete(competitionCategoryId: Int)

    /**
     * Retrieve a pool
     */
    fun getPool(competitionCategoryId: Int, poolName: String): PoolRecord

    /**
     * Checks if pool is done (i.e. if a PoolResultRecord exists)
     */
    fun isPoolFinished(poolId: Int): Boolean

    /**
     * Retrieve list of pool results for a given group
     */
    fun getPoolResult(poolId: Int): List<PoolResultRecord>

    /**
     * Delete pools for a given competition category
     */
    fun deletePools(competitionCategoryId: Int)

    /**
     * Should only be used to clear test data
     */
    fun clearPoolTable()
}