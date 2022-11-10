import Axios from "axios"

const RESOURCE_NAME = "/club"

const ClubService = {
    getClubs() {
        return Axios.get(RESOURCE_NAME, {withCredentials: true})
    },
    getPaymentInfo(clubId) {
        return Axios.get(`${RESOURCE_NAME}/${clubId}/payment-info/`, {withCredentials: true})
    },
    updatePaymentInfo(clubId, paymentId, paymentInfoSpec) {
        return Axios.put(`${RESOURCE_NAME}/${clubId}/payment-info/${paymentId}`, paymentInfoSpec, {withCredentials: true})
    },
    getClub(clubId) {
        return Axios.get(`${RESOURCE_NAME}/${clubId}`, {withCredentials: true})
    },
    getClubsForCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/competition/${competitionId}`, {withCredentials: true})
    },
    addClubForCompetition(competitionId, clubSpec) {
        return Axios.post(`${RESOURCE_NAME}/competition/${competitionId}`, clubSpec, {withCredentials: true})
    }
}

export default ClubService