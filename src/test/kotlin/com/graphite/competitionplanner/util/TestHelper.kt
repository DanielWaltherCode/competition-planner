package com.graphite.competitionplanner.util

import org.mockito.ArgumentCaptor
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

        /**
         * Returns ArgumentCaptor.capture() as nullable type to avoid java.lang.IllegalStateException
         * when null is returned.
         */
        fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()
    }

    object Benchmark {

        fun realtime(body: () -> Unit): Duration {
            val start = Instant.now()
            val end: Instant
            try {
                body()
            } finally {
                end = Instant.now()
            }
            return Duration.between(start, end)
        }

        /**
         * Run the given code the given number of times and calculate some basic performance metrics
         *
         * @param body Code to run
         * @param times Number of times to run the code
         * @return Statistics of the performance test
         */
        fun runTimes(times: Int, body: () -> Unit) : Results {

            val durations = mutableListOf<Duration>()
            repeat((1..times).count()) {
                val duration = realtime {
                    realtime(body)
                }
                durations.add(duration)
            }

            val worst = durations.maxOf { it }
            val best = durations.minOf { it }
            val avg = durations.sumOf { it.toMillis() } / durations.size.toDouble()
            return Results(worst, best, avg)
        }

        data class Results(
            val worst: Duration,
            val best: Duration,

            /**
             * Average execution time in milliseconds
             */
            val avg: Double
        )
    }
}