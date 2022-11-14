import Axios from "axios"

const RESOURCE_NAME = "/player"

const PlayerService = {
    addPlayerInCompetition(competitionId, body,) {
        return Axios.post(`/player/competition/${competitionId}`, body, {withCredentials: true})
    },
    getRegisteredPlayersInCompetition(searchParam, competitionId) {
        return Axios.get(`/competition/${competitionId}/registration`, {
            params: {
                searchType: searchParam
            },
           withCredentials: true
        })
    },
    searchAllPlayers(competitionId, partOfName) {
        return Axios.get(`/player/name-search/with-competition/${competitionId}`, {
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