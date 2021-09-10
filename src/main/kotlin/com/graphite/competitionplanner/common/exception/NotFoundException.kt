package com.graphite.competitionplanner.common.exception

/**
 * Repositories may throw this exception when the requested item
 * is not found.
 */
class NotFoundException(message: String) : Exception(message)