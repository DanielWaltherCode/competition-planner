import Axios from "axios";

const ScheduleGeneralService = {
    getExcelSchedule(competitionId) {
        return Axios.get(`/schedule/${competitionId}/excel-table`, {withCredentials: true})
    },
    tryScheduleMatches(competitionId, competitionCategoryId, matchScheduleSpec) {
        return Axios.post(`/schedule/${competitionId}/pre-schedule/${competitionCategoryId}/try`,
            matchScheduleSpec,{withCredentials: true})
    }
}

export default ScheduleGeneralService