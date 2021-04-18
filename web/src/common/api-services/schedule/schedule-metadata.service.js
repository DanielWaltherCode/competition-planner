import Axios from "axios";

const ScheduleMetadataService = {
    updateMinutesPerMatch(competitionId, minutesPerMatchSpec) {
        return Axios.put(`/schedule/${competitionId}/metadata/minutes`,
            minutesPerMatchSpec,
            {withCredentials: true})
    },
    getScheduleMetadata(competitionId) {
        return Axios.get(`/schedule/${competitionId}/metadata`,
            {withCredentials: true})
    },
}

export default ScheduleMetadataService