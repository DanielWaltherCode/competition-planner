import Axios from "axios";

const ScheduleGeneralService = {
    scheduleCategory(competitionId, competitionCategoryId, scheduleCategorySpec) {
        return Axios.put(`/api/schedule/${competitionId}/category/${competitionCategoryId}/`,
            scheduleCategorySpec,{withCredentials: true})
    },
    getCategorySchedulerSettings(competitionId) {
        return Axios.get(`/api/schedule/${competitionId}/category-settings`, {withCredentials: true})
    },
    getTimeTableInfo(competitionId) {
        return Axios.get(`/api/schedule/${competitionId}/timetable-info`, {withCredentials: true})
    },
    saveMainScheduleChanges(competitionId, mainScheduleSpec) {
        return Axios.put(`/api/schedule/${competitionId}/`, mainScheduleSpec, {withCredentials: true})
    },
    publishSchedule(competitionId) {
        return Axios.put(`/api/schedule/${competitionId}/publish`, {}, {withCredentials: true})
    },
    deleteSchedule(competitionId) {
        return Axios.delete(`/api/schedule/${competitionId}/`, {withCredentials: true})
    },
    clearCategory(competitionId, categoryId, stage) {
        return Axios.delete(`/api/schedule/${competitionId}/category/${categoryId}/${stage}`, {withCredentials: true})
    },
    updateMatchTime(competitionId, matchId, requestBody) {
        return Axios.put(`/api/schedule/${competitionId}/match/${matchId}`, requestBody, {withCredentials: true})
    }
}

export default ScheduleGeneralService