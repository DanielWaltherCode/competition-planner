import Axios from "axios";

const AvailableTablesService = {
    addTablesForDay(competitionId, availableTablesSpec) {
        return Axios.post(`/schedule/${competitionId}/available-tables`,
            availableTablesSpec,
            {withCredentials: true})
    },
    updateTables(competitionId, availableTablesSpec) {
        return Axios.put(`/schedule/${competitionId}/available-tables`,
            availableTablesSpec,
            {withCredentials: true})
    },
    getAvailableTablesForDay(competitionId, day) {
        return Axios.get(`/schedule/${competitionId}/available-tables/${day}`,
            {withCredentials: true})
    },
    getAvailableTablesForCompetition(competitionId) {
        return Axios.get(`/schedule/${competitionId}/available-tables/main-table`,
            {withCredentials: true})
    },
}

export default AvailableTablesService