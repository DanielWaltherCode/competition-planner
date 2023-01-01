import Axios from "axios"
const RESOURCE_NAME = "/api/match"

const MatchService = {
    getMatchesInCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}`, {withCredentials: true})
    },
    getMatch(competitionId, matchId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/single/${matchId}`, {withCredentials: true})
    }
}

export default MatchService