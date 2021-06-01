import Axios from "axios"

const RESOURCE_NAME = "/open/competition"

const ApiService = {
    getCompetitions() {
        return Axios.get(RESOURCE_NAME)
    },
    getCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}`)
    },
    getCompetitionCategories(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/categories`)
    },
    // Draw
    getDraw(competitionId, categoryId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/draw/${categoryId}` )
    },
    isClassDrawn(competitionId, categoryId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/draw/${categoryId}/is-draw-made`)
    },
    getRegistrationsInCategory(competitionId, categoryId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/registration/${categoryId}`)
    },

    // Category
    getCategory(competitionId, categoryId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/categories/${categoryId}`)
    },


    // Matches/Results
    getMatchesInCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/matches/${competitionId}`)
    }
}

export default ApiService