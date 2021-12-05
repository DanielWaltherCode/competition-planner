import Axios from "axios";

const RESOURCE_NAME = "/billing"

const BillingService = {
    getParticipatingClubs(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}`, {withCredentials: true})
    },
    getCostSummaryForClub(competitionId, clubId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/cost-summary/${clubId}`, {withCredentials: true})
    },
    getCostSummaryForPlayers(competitionId, clubId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/cost-summary/${clubId}/players`, {withCredentials: true})
    }
}

export default BillingService