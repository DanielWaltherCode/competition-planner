import Axios from "axios";

const CategoryService = {
    getCategories(competitionId) {
        return Axios.get(`/api/category/${competitionId}/`, {withCredentials: true})
    },
    addCompetitionCategory(competitionId, category) {
        return Axios.post(`/api/competition/${competitionId}/category`, category, {withCredentials: true})
    },
    getCompetitionCategories(competitionId) {
        return Axios.get(`/api/competition/${competitionId}/category`, {withCredentials: true})
    },
    updateCompetitionCategory(competitionId, competitionCategoryId, updatedCategory) {
        return Axios.put(`/api/competition/${competitionId}/category/${competitionCategoryId}`, updatedCategory, {withCredentials: true})
    },
    deleteCompetitionCategory(competitionId, competitionCategoryId) {
        return Axios.delete(`/api/competition/${competitionId}/category/${competitionCategoryId}`, {withCredentials: true})
    },
    addCustomCategory(competitionId, category) {
        return Axios.post(`/api/category/${competitionId}`, category, {withCredentials: true})
    },
    openCompetitionCategoryForRegistration(competitionId, competitionCategoryId) {
        return Axios.put(`/api/competition/${competitionId}/category/${competitionCategoryId}/open-for-registration`, {}, {withCredentials: true})
    },

    // Category general settings
    getCategoryMetadata(competitionId, categoryId) {
        return Axios.get(`/api/competition/${competitionId}/category/${categoryId}/metadata/`, {withCredentials: true})
    },
    getPossibleMetaDataValues(competitionId) {
        return Axios.get(`/api/competition/${competitionId}/category/metadata/possible-values`, {withCredentials: true})
    },

    // Category game rules
    getCategoryGameRules(competitionId, categoryId) {
        return Axios.get(`/api/competition/${competitionId}/category/${categoryId}/game-rules/`, {withCredentials: true})
    }
}

export default CategoryService