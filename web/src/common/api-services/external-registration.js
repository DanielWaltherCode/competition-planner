import Axios from "axios";

const RESOURCE_NAME = "external-registration"

const ExternalRegistrationService = {
    getRegisteredPlayersInCategoryForClub(categoryId, clubId) {
        return Axios.get(`${RESOURCE_NAME}/${categoryId}/club/${clubId}`, {withCredentials: true})
    },
    getCompetitionCategories(selectedCompetitionId) {
        return Axios.get(`${RESOURCE_NAME}/competition/${selectedCompetitionId}/category`, {withCredentials: true})
    },
    registerPlayerSingles(registrationSpec) {
       return Axios.post(`${RESOURCE_NAME}/register/singles`, registrationSpec, {withCredentials: true})
    },
    registerPlayerDoubles(registrationSpec) {
        return Axios.post(`${RESOURCE_NAME}/register/doubles`, registrationSpec, {withCredentials: true})
    },
}

export default ExternalRegistrationService
