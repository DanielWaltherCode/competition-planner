package com.graphite.competitionplanner.common.exception

class GeneralException(val exceptionType: GeneralExceptionType, val errorMessage: String)
    : Exception(errorMessage)