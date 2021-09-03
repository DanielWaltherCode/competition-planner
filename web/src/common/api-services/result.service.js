import Axios from "axios"

const RESOURCE_NAME = "/result"

const ResultService = {
    addResult(matchId, body) {
        return Axios.post(`${RESOURCE_NAME}/${matchId}`, body, {withCredentials: true})
    },
    updateFullMatchResult(matchId, body) {
        return Axios.put(`${RESOURCE_NAME}/${matchId}`, body, {withCredentials: true})
    },
    addPartialResult(matchId, body) {
      return Axios.put(`${RESOURCE_NAME}/${matchId}/partial`, body, {withCredentials: true})
    },
    deleteResult(matchId) {
        return Axios.delete(`${RESOURCE_NAME}/${matchId}`, {withCredentials: true})
    }

}

export default ResultService