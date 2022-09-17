package com.graphite.competitionplanner.registration.domain

import com.graphite.competitionplanner.registration.interfaces.IRegistrationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Unregister(
    @Autowired val repository: IRegistrationRepository
) {
    fun execute(registrationId: Int) {
        repository.remove(registrationId)
    }
}