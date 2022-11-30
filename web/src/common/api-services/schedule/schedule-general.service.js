import Axios from "axios";

const ScheduleGeneralService = {
    scheduleCategory(competitionId, competitionCategoryId, scheduleCategorySpec) {
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
    },
    publishSchedule(competitionId) {
        return Axios.put(`/schedule/${competitionId}/publish`, {}, {withCredentials: true})
    },
    deleteSchedule(competitionId) {
        return Axios.delete(`/schedule/${competitionId}/`, {withCredentials: true})
    },
    clearCategory(competitionId, categoryId, stage) {
        return Axios.delete(`/schedule/${competitionId}/category/${categoryId}/${stage}`, {withCredentials: true})
    },
    updateMatchTime(competitionId, matchId, requestBody) {
        return Axios.put(`/schedule/${competitionId}/match/${matchId}`, requestBody, {withCredentials: true})
    }
}

export default ScheduleGeneralService