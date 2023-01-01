import Axios from "axios"

const RESOURCE_NAME = "/player"

const PlayerService = {
    addPlayerInCompetition(competitionId, body,) {
        return Axios.post(`/api/player/competition/${competitionId}`, body, {withCredentials: true})
    },
    getRegisteredPlayersInCompetition(searchParam, competitionId) {
        return Axios.get(`/api/competition/${competitionId}/registration`, {
            params: {
                searchType: searchParam
            },
           withCredentials: true
        })
    },
    searchAllPlayers(competitionId, partOfName) {
        return Axios.get(`/api/player/name-search/with-competition/${competitionId}`, {
            params: {
                partOfName: partOfName
            },
            withCredentials: true
        })
    },
    findByNameInCompetition(partOfName, competitionId) {
        return Axios.get(`/api/player/name-search/` + competitionId, {
            params: {
                partOfName: partOfName
            },
            withCredentials: true
        })
    }

}

export default PlayerService