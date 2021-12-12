package com.graphite.competitionplanner.draw.interfaces

/**
 * This is thrown when one tries to make a draw but there are too few registrations in the given category.
 */
class NotEnoughRegistrationsException(
    message: String = "Not enough registration in category to make a draw."): Exception(message)