import Axios from "axios"

const RESOURCE_NAME = "/result"

const ResultService = {
    addResult(competitionId, matchId, body) {
        return Axios.post(`${RESOURCE_NAME}/${competitionId}/${matchId}`, body, {withCredentials: true})
    },
    updateFullMatchResult(competitionId, matchId, body) {
        return Axios.put(`${RESOURCE_NAME}/${competitionId}/${matchId}`, body, {withCredentials: true})
    },
    addPartialResult(competitionId, matchId, body) {
      return Axios.put(`${RESOURCE_NAME}/${competitionId}/${matchId}/partial`, body, {withCredentials: true})
    },
    deleteResult(competitionId, matchId) {
        return Axios.delete(`${RESOURCE_NAME}/${competitionId}/${matchId}`, {withCredentials: true})
    }

}

export default ResultService