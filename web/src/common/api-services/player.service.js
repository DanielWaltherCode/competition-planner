import Axios from "axios"

const RESOURCE_NAME = "/player"

const PlayerService = {
    addPlayer(body) {
        return Axios.post(RESOURCE_NAME, body, {withCredentials: true})
    },
    getRegisteredPlayersInCompetition(searchParam, competitionId) {
        return Axios.get(`/competition/${competitionId}/registration`, {
            params: {
                searchType: searchParam
            },
           withCredentials: true
        })
    },
    searchAllPlayers(partOfName) {
        return Axios.get(`/player/name-search`, {
            params: {
                partOfName: partOfName
            },
            withCredentials: true
        })
    },
    findByNameInCompetition(partOfName, competitionId) {
        return Axios.get(`/player/name-search/` + competitionId, {
            params: {
                partOfName: partOfName
            },
            withCredentials: true
        })
    }

}

export default PlayerService