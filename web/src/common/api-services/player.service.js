import Axios from "axios"
const RESOURCE_NAME = "player"

const PlayerService = {
    addPlayer(body) {
        return Axios.post(RESOURCE_NAME, body)
    }
}

export default PlayerService