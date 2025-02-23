import Axios from "axios";


const RegistrationService = {
    getRegistrationsInCategory(competitionId, categoryId) {
        return Axios.get(`/api/competition/${competitionId}/registration/${categoryId}`, {withCredentials: true})
    },
    registerPlayerSingles(competitionId, registrationSpec) {
        return Axios.post(`/api/competition/${competitionId}/registration/singles`, registrationSpec, {withCredentials: true})
    },
    registerPlayerDoubles(competitionId, registrationSpec) {
        return Axios.post(`/api/competition/${competitionId}/registration/doubles`, registrationSpec, {withCredentials: true})
    },
    getRegisteredPlayersSingles(competitionId, categoryId){
        return Axios.get(`/api/competition/${competitionId}/registration/${categoryId}/singles`, {withCredentials: true})
    },
    getRegisteredPlayersDoubles(competitionId, categoryId){
        return Axios.get(`/api/competition/${competitionId}/registration/${categoryId}/doubles`, {withCredentials: true})
    },
    createDraw(competitionId, categoryId) {
        return Axios.put(`/api/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    getDraw(competitionId, categoryId) {
        return Axios.get(`/api/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    getRegistrationsForPlayer(competitionId, playerId) {
        return Axios.get(`/api/competition/${competitionId}/registration/player/${playerId}`, {withCredentials: true})
    },
    // Withdrawing after draw is made but before any matches are played
    withdraw(competitionId, categoryId, registrationId) {
        return Axios.put(`/api/competition/${competitionId}/registration/withdraw/${categoryId}/${registrationId}`, {}, {withCredentials: true})
    },
    // After competition or category has started, this method should be called
    giveWalkover(competitionId, categoryId, registrationId) {
        return Axios.put(`/api/competition/${competitionId}/registration/walkover/${categoryId}/${registrationId}`, {}, {withCredentials: true})
    },
    getRegistrationId(competitionId, categoryId, playerId) {
        return Axios.get(`/api/competition/${competitionId}/registration/player/${categoryId}/${playerId}`, {withCredentials: true})
    }
}

export default RegistrationService