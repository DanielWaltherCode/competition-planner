import Axios from "axios";

const CategoryService = {
    getCategories() {
        return Axios.get("/category", {withCredentials: true})
    },
    addCompetitionCategory(competitionId, category) {
        return Axios.post(`competition/${competitionId}/category`, category, {withCredentials: true})
    },
    getCompetitionCategories(competitionId) {
        return Axios.get(`/competition/${competitionId}/category`, {withCredentials: true})
    },
    updateCompetitionCategory(competitionId, competitionCategoryId, updatedCategory) {
        return Axios.put(`/competition/${competitionId}/category/${competitionCategoryId}`, updatedCategory, {withCredentials: true})
    },
    deleteCompetitionCategory(competitionId, competitionCategoryId) {
        return Axios.delete(`/competition/${competitionId}/category/${competitionCategoryId}`, {withCredentials: true})
    },

    // Category general settings
    getCategoryMetadata(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/category/${categoryId}/metadata/`, {withCredentials: true})
    },
    getPossibleMetaDataValues(competitionId) {
        return Axios.get(`/competition/${competitionId}/category/metadata/possible-values`, {withCredentials: true})
    },

    // Category game rules
    getCategoryGameRules(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/category/${categoryId}/game-rules/`, {withCredentials: true})
    }
}

export default CategoryService