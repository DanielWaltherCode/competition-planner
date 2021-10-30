import Axios from "axios"


const DrawService = {
    isClassDrawn(competitionId, categoryId) {
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
    }
}

export default DrawService