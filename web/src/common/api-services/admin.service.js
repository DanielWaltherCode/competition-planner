import Axios from "axios";

const RESOURCE_NAME = "/admin"

const AdminService = {
    addClub(body) {
        return Axios.post(`${RESOURCE_NAME}/club`, body, {withCredentials: true})
    },
    addUser(body) {
        return Axios.post(`${RESOURCE_NAME}/user`, body, {withCredentials: true})
    },
    getCompetitions() {
        return Axios.get(`${RESOURCE_NAME}/competitions`, {withCredentials: true})
    }
}

export default AdminService