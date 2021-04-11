import Axios from "axios";

const CategoryStartTimeService = {
    addCategoryStartTime(competitionId, categoryId, categoryStartTimeSpec) {
        return Axios.post(`/schedule/${competitionId}/category-start-time/${categoryId}`,
            categoryStartTimeSpec,
            {withCredentials: true})
    },
    updateCategoryStartTime(categoryStartTimeId, competitionId, categoryId, categoryStartTimeSpec) {
        return Axios.put(`/schedule/${competitionId}/category-start-time/${categoryId}/${categoryStartTimeId}`,
            categoryStartTimeSpec,
            {withCredentials: true})
    },
    getCategoryStartTimesInCompetition(competitionId, categoryId) {
        return Axios.get(`/schedule/${competitionId}/category-start-time/${categoryId}}`,
            {withCredentials: true})
    },
    deleteCategoryStartTime(categoryStartTimeId, competitionId, categoryId) {
        return Axios.delete(`/schedule/${competitionId}/category-start-time/${categoryId}/${categoryStartTimeId}`,
            {withCredentials: true})
    }
}

export default CategoryStartTimeService