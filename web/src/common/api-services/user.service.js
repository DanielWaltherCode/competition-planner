import Axios from "axios"
const RESOURCE_NAME = "/user"

const UserService = {
    register(body) {
        return Axios.post(RESOURCE_NAME, body, {withCredentials: true})
    },
    login(username, password) {
        return Axios.post("/login", {username, password})
    },
    logout() {
        return Axios.get(RESOURCE_NAME + "/logout", {withCredentials: true})
    },
    getUser() {
        return Axios.get(RESOURCE_NAME)
    }
}

export default UserService