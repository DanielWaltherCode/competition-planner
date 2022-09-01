package com.graphite.competitionplanner.common.api

import com.graphite.competitionplanner.common.exception.BadRequestException
import com.graphite.competitionplanner.common.exception.NotFoundException
import com.graphite.competitionplanner.common.exception.GameValidationException
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
        IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun entityValidationError(exception: IllegalArgumentException, request: WebRequest): ResponseEntity<Any> {
        val body = mutableMapOf<String, Any>()
        body["timestamp"] = LocalDateTime.now()
        body["message"] = "${exception.message}"
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ResponseBody
    @ExceptionHandler(BadRequestException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun badRequestWithErrorType(exception: BadRequestException, request: WebRequest): ResponseEntity<Any> {
        val body = mutableMapOf<String, Any>()
        body["timestamp"] = LocalDateTime.now()
        body["errorType"] = exception.exceptionType.name
        body["message"] = exception.errorMessage
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }

    @ResponseBody
    @ExceptionHandler(GameValidationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun gameValidationError(exception: GameValidationException, request: WebRequest): ResponseEntity<Any> {
        val body = mutableMapOf<String, Any>()
        body["timestamp"] = LocalDateTime.now()
        body["message"] = "${exception.message}"
        return ResponseEntity(body, HttpStatus.BAD_REQUEST)
    }
}
