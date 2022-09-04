package com.graphite.competitionplanner.common.repository

import com.graphite.competitionplanner.common.exception.BadRequestException
import org.jooq.DSLContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Provides a base implementation of the IRepository interface as well as a logger object.
 */
abstract class BaseRepository(
    protected val dslContext: DSLContext
) : IRepository {

    protected val logger: Logger = LoggerFactory.getLogger(javaClass)

    override fun asTransaction(block: () -> Unit) {
        try {
            dslContext.transaction { _ ->
                run(block)
            }
        } catch (exception: RuntimeException) {
            if (exception.cause is BadRequestException) {
                // Client error. Return BadRequest instead of generic RuntimeException
                throw exception.cause as BadRequestException
            }
            logger.error("Failed to commit transaction", exception)
            throw RuntimeException("Something went wrong")
        }
    }
}