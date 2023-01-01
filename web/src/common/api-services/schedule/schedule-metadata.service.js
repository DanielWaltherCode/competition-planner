import Axios from "axios";

const ScheduleMetadataService = {
    updateMinutesPerMatch(competitionId, minutesPerMatchSpec) {
        return Axios.put(`/api/schedule/${competitionId}/metadata/minutes`,
            minutesPerMatchSpec,
            {withCredentials: true})
    },
    getScheduleMetadata(competitionId) {
        return Axios.get(`/api/schedule/${competitionId}/metadata`,
            {withCredentials: true})
    },
    updateScheduleMetadata(competitionId, scheduleMetadataId, scheduleMetadataSpec) {
        return Axios.put(`/api/schedule/${competitionId}/metadata/${scheduleMetadataId}`,
            scheduleMetadataSpec,
            {withCredentials: true})
    }
}

export default ScheduleMetadataService