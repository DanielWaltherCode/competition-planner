import Axios from "axios";

const AvailableTablesService = {
    addTablesForDay(competitionId, availableTablesSpec) {
        return Axios.post(`/api/schedule/${competitionId}/available-tables`,
            availableTablesSpec,
            {withCredentials: true})
    },
    updateTables(competitionId, availableTablesSpec) {
        return Axios.put(`/api/schedule/${competitionId}/available-tables`,
            availableTablesSpec,
            {withCredentials: true})
    },
    getAvailableTablesForDay(competitionId, day) {
        return Axios.get(`/api/schedule/${competitionId}/available-tables/${day}`,
            {withCredentials: true})
    },
    getAvailableTablesForCompetition(competitionId) {
        return Axios.get(`/api/schedule/${competitionId}/available-tables/main-table`,
            {withCredentials: true})
    },
}

export default AvailableTablesService