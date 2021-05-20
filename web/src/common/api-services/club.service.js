import Axios from "axios"

const RESOURCE_NAME = "/club"

const ClubService = {
    getClubs() {
        return Axios.get(RESOURCE_NAME, {withCredentials: true})
    }
}

export default ClubService