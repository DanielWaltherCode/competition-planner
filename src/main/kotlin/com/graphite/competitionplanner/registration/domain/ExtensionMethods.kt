package com.graphite.competitionplanner.registration.domain

fun Registration.asInt(): Int {
    return when (this) {
        is Registration.Real -> this.id
        is Registration.Bye -> 0
        is Registration.Placeholder -> -1
    }
}

fun Int.isReal(): Boolean {
    return this > 0
}

fun Int.asRegistration(): Registration {
    return if (this > 0) {
        return Registration.Real(this)
    } else if (this == 0) {
        Registration.Bye
    } else if (this == -1) {
        Registration.Placeholder()
    } else {
        throw IllegalArgumentException("The value $this provided does not correspond to a Registration")
    }
}