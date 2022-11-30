import Axios from "axios"


const DrawService = {
    isDrawMade(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/draw/${categoryId}/is-draw-made`, {withCredentials: true})
    },

    createDraw(competitionId, categoryId) {
        return Axios.put(`/competition/${competitionId}/draw/${categoryId}`, {},{withCredentials: true})
    },
    getDraw(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    deleteDraw(competitionId, categoryId) {
        return Axios.delete(`/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    },
    getPlayoffDraw() {
        return Axios.get(`/competition/1/draw/1/mock`, {withCredentials: true})
    },
    getCurrentSeed(competitionId, categoryId) {
       return Axios.get(`/competition/${competitionId}/draw/${categoryId}/seeding`, {withCredentials: true})
    },
    approveSeeding(competitionId, categoryId, seedingObject) {
        return Axios.post(`/competition/${competitionId}/draw/${categoryId}/seeding`,
            seedingObject,{withCredentials: true})
    }
}

export default DrawService