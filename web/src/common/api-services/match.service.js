import Axios from "axios"
const RESOURCE_NAME = "/match"

const MatchService = {
    getMatchesInCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}`, {withCredentials: true})
    },
    getMatch(matchId) {
        return Axios.get(`${RESOURCE_NAME}/single/${matchId}`, {withCredentials: true})
    }
}

export default MatchService