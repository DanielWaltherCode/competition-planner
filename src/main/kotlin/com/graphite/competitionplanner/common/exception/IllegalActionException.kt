package com.graphite.competitionplanner.common.exception

/**
 * Thrown when a given action is not applicable to the current state of the competition
 */
class IllegalActionException(message: String) : Exception(message)