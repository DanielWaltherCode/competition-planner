import Axios from "axios";

const DailyStartEndService = {
    addDailyStartEnd(competitionId, dailyStartEndSpec) {
        return Axios.post(`/schedule/${competitionId}/daily-start-end/`,
            dailyStartEndSpec,
            {withCredentials: true})
    },
    updateDailyStartEnd(dailyStartEndId, competitionId, dailyStartEndSpec) {
        return Axios.put(`/schedule/${competitionId}/daily-start-end/${dailyStartEndId}`,
            dailyStartEndSpec,
            {withCredentials: true})
    },
    getDailyStartEndForCompetition(competitionId) {
        return Axios.get(`/schedule/${competitionId}/daily-start-end`,
            {withCredentials: true})
    }
}

export default DailyStartEndService