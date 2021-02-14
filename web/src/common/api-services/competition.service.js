import Axios from "axios"
const RESOURCE_NAME = "competition"

const CompetitionService = {
    addCompetition(body) {
        return Axios.post(RESOURCE_NAME, body)
    }
}

export default CompetitionService