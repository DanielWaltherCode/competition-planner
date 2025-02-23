package com.graphite.competitionplanner.security

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import com.graphite.competitionplanner.competition.interfaces.CompetitionDTO
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.HandlerMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class PathAccessInterceptor(val findCompetitions: FindCompetitions) : HandlerInterceptor {

    @Throws(Exception::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) != null) {
            val pathVariables = request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE) as Map<String, String>
            // If the path does not contain a competitionId, don't protect here
            val competitionId = pathVariables["competitionId"] ?: return true
            // If request is to Open, don't protect here
            if (!request.requestURI.contains("api")) {
                return true
            }

            // If it is an admin user, return true
            val authentication: Authentication = SecurityContextHolder.getContext().authentication
            if (authentication.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN"))) return true

            val competitionIdAsInt = competitionId.toInt()
            val clubId: Int = SecurityHelper.getClubIdFromToken(authentication.credentials as String)
            val competitionsForClub: List<CompetitionDTO> = findCompetitions.thatBelongTo(clubId)
            val competitionIds: List<Int> = competitionsForClub.map { c -> c.id }

            // If the list of competitions hosted by this club contains the competition id sent in, permit
            if (competitionIds.contains(competitionIdAsInt)) {
                return true
            } else {
                throw ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot access competition not belonging to club")
            }
        }
        return true
    }
}