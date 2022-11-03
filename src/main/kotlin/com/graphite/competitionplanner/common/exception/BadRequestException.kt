package com.graphite.competitionplanner.common.exception

class BadRequestException(val exceptionType: BadRequestType, val errorMessage: String)
    : Exception(errorMessage)