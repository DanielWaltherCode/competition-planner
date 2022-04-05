import Axios from "axios";

const ScheduleGeneralService = {
    getExcelSchedule(competitionId) {
        return Axios.get(`/schedule/${competitionId}/excel-table`, {withCredentials: true})
    }
}

export default ScheduleGeneralService