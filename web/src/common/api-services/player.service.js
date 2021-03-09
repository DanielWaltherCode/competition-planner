import Axios from "axios"
import store from "@/store/store";

const RESOURCE_NAME = "/player"

let competitionId = null
if (store.getters.competition !== null) {
    competitionId = store.getters.competition.id
}

const PlayerService = {
    addPlayer(body) {
        return Axios.post(RESOURCE_NAME, body, {withCredentials: true})
    },
    getRegisteredPlayersInCompetition(searchParam) {
        return Axios.get(`/competition/${competitionId}/registration`, {
            params: {
                searchType: searchParam
            },
           withCredentials: true
        })
    },
}

export default PlayerService