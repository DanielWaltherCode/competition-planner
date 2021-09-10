package com.graphite.competitionplanner.util

import org.mockito.Mockito

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
}