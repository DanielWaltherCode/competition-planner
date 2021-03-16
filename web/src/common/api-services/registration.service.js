import Axios from "axios";


const RegistrationService = {
    getRegistrationsInCategory(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/registration/${categoryId}`, {withCredentials: true})
    },
    createDraw(competitionId, categoryId) {
        return Axios.put(`/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    getDraw(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    }
}

export default RegistrationService