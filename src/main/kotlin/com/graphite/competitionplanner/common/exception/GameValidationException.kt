package com.graphite.competitionplanner.common.exception

class GameValidationException(reason: Reason) : Exception(reason.toString()) {
    enum class Reason {
        TOO_FEW_SETS_REPORTED,
        TOO_FEW_POINTS_IN_SET,
        COULD_NOT_DECIDE_WINNER,
        TOO_MANY_SETS_REPORTED,
        NOT_ENOUGH_WIN_MARGIN
    }
}
