import Axios from "axios"
import store from "@/store/store";

const RESOURCE_NAME = "/competition"
let competitionId = null

if (store.getters.competition !== null) {
    competitionId = store.getters.competition.id
}

const CompetitionService = {
    addCompetition(body) {
        return Axios.post(RESOURCE_NAME, body, {withCredentials: true})
    },
    updateCompetition(body) {
        return Axios.put(`${RESOURCE_NAME}/${competitionId}`, body, {withCredentials: true})
    },
    getCompetitions() {
        return Axios.get(RESOURCE_NAME, {withCredentials: true})
    }
}

export default CompetitionService