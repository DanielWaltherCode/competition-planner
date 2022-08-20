package com.graphite.competitionplanner.common.repository

interface IRepository {
    /**
     * Run the given block of code as a transaction
     *
     * @param block Block of code to run
     */
    fun asTransaction(block: () -> Unit)
}