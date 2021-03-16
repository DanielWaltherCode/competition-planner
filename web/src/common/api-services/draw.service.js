import Axios from "axios"


const DrawService = {
    isClassDrawn(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/draw/${categoryId}/is-draw-made`, {withCredentials: true})
    },
    getCompetitionCategories(competitionId) {
        return Axios.get(`/competition/${competitionId}/category`, {withCredentials: true})
    },
    createDraw(competitionId, categoryId) {
        return Axios.put(`/competition/${competitionId}/draw/${categoryId}`, {},{withCredentials: true})
    },
    getDraw(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/draw/${categoryId}`, {withCredentials: true})
    }
}

export default DrawService