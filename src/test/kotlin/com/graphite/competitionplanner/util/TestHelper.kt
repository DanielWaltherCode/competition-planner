package com.graphite.competitionplanner.util

import org.mockito.Mockito
import java.time.Duration
import java.time.Instant

class TestHelper {
    object MockitoHelper {
        // This helper was found at https://stackoverflow.com/questions/59230041/argumentmatchers-any-must-not-be-null
        // and is a workaround to get matchers to work with method that does not take null parameters.
        fun <T> anyObject(): T {
            Mockito.any<T>()
            return uninitialized()
        }

        @Suppress("UNCHECKED_CAST")
        fun <T> uninitialized(): T = null as T
    }

    object Benchmark {
        fun realtime(body: () -> Unit): Duration {
            val start = Instant.now()
            var end: Instant
            try {
                body()
            } finally {
                end = Instant.now()
            }
            return Duration.between(start, end)
        }
    }
}