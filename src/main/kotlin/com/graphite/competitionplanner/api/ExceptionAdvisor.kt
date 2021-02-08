package com.graphite.competitionplanner.api

import com.graphite.competitionplanner.service.PlayerNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime


@ControllerAdvice
class PlayerNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(PlayerNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun playerNotFoundHandler(exception: PlayerNotFoundException, request: WebRequest): ResponseEntity<Any> {
        val body = mutableMapOf<String, Any>()
        body["timestamp"] = LocalDateTime.now()
        body["message"] = "Player with id ${exception.playerId} not found."
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

}
