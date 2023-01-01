import Axios from "axios"

const RESOURCE_NAME = "/api/competition"

const CompetitionService = {
    addCompetition(body) {
        return Axios.post(RESOURCE_NAME, body, {withCredentials: true})
    },
    updateCompetition(body, competitionId) {
        return Axios.put(`${RESOURCE_NAME}/${competitionId}`, body, {withCredentials: true})
    },
    getCompetitions() {
        return Axios.get(`${RESOURCE_NAME}/for-club`, {withCredentials: true})
    },
    getDaysInCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/days`, {withCredentials: true})
    },
    getPossibleRounds() {
        return Axios.get(`${RESOURCE_NAME}/rounds`, {withCredentials: true})
    },
    getPossibleLevels() {
        return Axios.get(`${RESOURCE_NAME}/levels`, {withCredentials: true})
    },
}

export default CompetitionService