package com.graphite.competitionplanner.registration.domain

sealed class Registration {
    class Real(val id: Int) : Registration() {
        init {
            require(id > 0) { "A real registration must have an id higher than 0" }
        }

        override fun toString(): String {
            return id.toString()
        }
    }

    class Placeholder(var name: String = "Placeholder") : Registration() {
        override fun toString(): String {
            return name
        }
    }

    object Bye : Registration() {
        override fun toString(): String {
            return "BYE"
        }
    }
}