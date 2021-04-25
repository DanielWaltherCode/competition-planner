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
    updateScheduleMetadata(competitionId, scheduleMetadataId, scheduleMetadataSpec) {
        return Axios.put(`/schedule/${competitionId}/metadata/${scheduleMetadataId}`,
            scheduleMetadataSpec,
            {withCredentials: true})
    }
}

export default ScheduleMetadataService