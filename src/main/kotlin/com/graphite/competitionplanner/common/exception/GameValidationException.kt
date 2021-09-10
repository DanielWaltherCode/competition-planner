package com.graphite.competitionplanner.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class GameValidationException(status: HttpStatus, reason: String) : ResponseStatusException(status, reason) {
}