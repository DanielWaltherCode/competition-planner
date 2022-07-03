import Axios from "axios";

const ScheduleGeneralService = {
    getExcelSchedule(competitionId) {
        return Axios.get(`/schedule/${competitionId}/excel-table`, {withCredentials: true})
    },
    tryScheduleMatches(competitionId, competitionCategoryId, scheduleCategorySpec) {
        return Axios.put(`/schedule/${competitionId}/category/${competitionCategoryId}/`,
            scheduleCategorySpec,{withCredentials: true})
    },
    getCategorySchedulerSettings(competitionId) {
        return Axios.get(`/schedule/${competitionId}/category-settings`, {withCredentials: true})
    },
    getTimeTableInfo(competitionId) {
        return Axios.get(`/schedule/${competitionId}/timetable-info`, {withCredentials: true})
    },
    saveMainScheduleChanges(competitionId, mainScheduleSpec) {
        return Axios.put(`/schedule/${competitionId}/`, mainScheduleSpec, {withCredentials: true})
    }
}

export default ScheduleGeneralService