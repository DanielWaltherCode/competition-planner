import Axios from "axios";


const RegistrationService = {
    getRegistrationsInCategory(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/registration/${categoryId}`, {withCredentials: true})
    },
    registerPlayerSingles(competitionId, registrationSpec) {
        return Axios.post(`/competition/${competitionId}/registration/singles`, registrationSpec, {withCredentials: true})
    },
    registerPlayerDoubles(competitionId, registrationSpec) {
        return Axios.post(`/competition/${competitionId}/registration/doubles`, registrationSpec, {withCredentials: true})
    },
    createDraw(competitionId, categoryId) {
        return Axios.put(`/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    getDraw(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    getRegistrationsForPlayer(competitionId, playerId) {
        return Axios.get(`/competition/${competitionId}/registration/player/${playerId}`, {withCredentials: true})
    }
}

export default RegistrationService