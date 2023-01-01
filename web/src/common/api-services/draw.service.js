import Axios from "axios"


const DrawService = {
    isDrawMade(competitionId, categoryId) {
        return Axios.get(`/api/competition/${competitionId}/draw/${categoryId}/is-draw-made`, {withCredentials: true})
    },

    createDraw(competitionId, categoryId) {
        return Axios.put(`/api/competition/${competitionId}/draw/${categoryId}`, {},{withCredentials: true})
    },
    getDraw(competitionId, categoryId) {
        return Axios.get(`/api/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    deleteDraw(competitionId, categoryId) {
        return Axios.delete(`/api/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    getCurrentSeed(competitionId, categoryId) {
       return Axios.get(`/api/competition/${competitionId}/draw/${categoryId}/seeding`, {withCredentials: true})
    },
    approveSeeding(competitionId, categoryId, seedingObject) {
        return Axios.post(`/api/competition/${competitionId}/draw/${categoryId}/seeding`,
            seedingObject,{withCredentials: true})
    }
}

export default DrawService