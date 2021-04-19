import Axios from "axios";

const AvailableTablesService = {
    addTablesForDay(competitionId, availableTablesSpec) {
        return Axios.post(`/schedule/${competitionId}/available-tables/full-day`,
            availableTablesSpec,
            {withCredentials: true})
    },
    updateTablesForDay(competitionId, availableTablesSpec) {
        return Axios.put(`/schedule/${competitionId}/available-tables/full-day`,
            availableTablesSpec,
            {withCredentials: true})
    },
    updateDailyStartEnd(dailyStartEndId, competitionId, dailyStartEndSpec) {
        return Axios.put(`/schedule/${competitionId}/available-tables/${dailyStartEndId}`,
            dailyStartEndSpec,
            {withCredentials: true})
    },
    getAvailableTablesForCompetition(competitionId) {
        return Axios.get(`/schedule/${competitionId}/available-tables/main-table`,
            {withCredentials: true})
    },
}

export default AvailableTablesService