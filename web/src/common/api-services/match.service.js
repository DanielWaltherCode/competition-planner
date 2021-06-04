import Axios from "axios"
const RESOURCE_NAME = "/match"

const MatchService = {
    getMatchesInCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}`, {withCredentials: true})
    }
}

export default MatchService