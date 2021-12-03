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

    // Players
    getRegisteredPlayersInCompetition(searchParam, competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/registration`, {
            params: {
                searchType: searchParam
            },
            withCredentials: true
        })
    },

    getRegistrationsForPlayer(competitionId, playerId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/player/${playerId}`)
    },

    findByNameInCompetition(partOfName, competitionId) {
        return Axios.get(`${RESOURCE_NAME}/name-search/` + competitionId, {
            params: {
                partOfName: partOfName
            }
        })
    },
    // Matches/Results
    getMatchesInCompetition(competitionId) {
        return Axios.get(`${RESOURCE_NAME}/${competitionId}/matches`)
    }
}

export default ApiService