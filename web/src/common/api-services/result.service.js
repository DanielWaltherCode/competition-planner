import Axios from "axios"

const RESOURCE_NAME = "/registration"

const RegistrationService = {
    registerResult(body) {
        return Axios.post(RESOURCE_NAME, body, {withCredentials: true})
    }
}

export default RegistrationService