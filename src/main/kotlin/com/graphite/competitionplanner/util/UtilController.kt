package com.graphite.competitionplanner.util

import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/util")
@Profile("local")
class UtilController(val setupTestData: SetupTestData) {

    @PutMapping("/set-up-test-data")
    fun setUpTestData() {
        setupTestData.resetTestData()
    }
}