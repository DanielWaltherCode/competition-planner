package com.graphite.competitionplanner.common.api

import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.common.exception.GameValidationException
import com.graphite.competitionplanner.competitioncategory.domain.CannotDeleteCompetitionCategoryException
import com.graphite.competitionplanner.draw.interfaces.NotEnoughRegistrationsException
import com.graphite.competitionplanner.registration.interfaces.PlayerAlreadyRegisteredException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.WebRequest
import java.time.LocalDateTime


@ControllerAdvice
class ExceptionAdvisor {

    @ResponseBody
    @ExceptionHandler(NotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun entityNotFoundHandler(exception: NotFoundException, request: WebRequest): ResponseEntity<Any> {
        val body = mutableMapOf<String, Any>()
        body["timestamp"] = LocalDateTime.now()
        body["message"] = "${exception.message}"
        return ResponseEntity(body, HttpStatus.NOT_FOUND)
    }

    @ResponseBody
    @ExceptionHandler(
        IllegalArgumentException::class,
        GameValidationException::class,
        CannotDeleteCompetitionCategoryException::class,
        NotEnoughRegistrationsException::class,
        PlayerAlreadyRegisteredException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun entityValidationError(exception: IllegalArgumentException, request: WebRequest): ResponseEntity<Any> {
        val body = mutableMapOf<String, Any>()
        body["timestamp"] = LocalDateTime.now()
        body["message"] = "${exception.message}"
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

}
