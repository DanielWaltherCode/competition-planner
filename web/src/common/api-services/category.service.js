import Axios from "axios";

const CategoryService = {
    getCategories() {
        return Axios.get("/category", {withCredentials: true})
    },
    addCompetitionCategory(competitionId, categoryId) {
        return Axios.post(`competition/${competitionId}/category/${categoryId}`, {}, {withCredentials: true})
    },
    getCompetitionCategories(competitionId) {
        return Axios.get(`/competition/${competitionId}/category`, {withCredentials: true})
    },

    // Category settings/metadata
    addMetaData(competitionId, competitionCategoryId, categoryMetaDataSpec) {
        return Axios.post(`/competition/${competitionId}/category/metadata/${competitionCategoryId}`, categoryMetaDataSpec, {withCredentials: true})
    },
    getCategoryMetadata(competitionId, categoryId) {
        return Axios.get(`/competition/${competitionId}/category/metadata/${categoryId}/`, {withCredentials: true})
    },
    updateMetaData(competitionId,competitionCategoryId, categoryMetadataId, categoryMetaDataSpec) {
    return Axios.put(`/competition/${competitionId}/category/metadata/${competitionCategoryId}/${categoryMetadataId}`, categoryMetaDataSpec, {withCredentials: true})
    },
    getPossibleMetaDataValues(competitionId) {
        return Axios.get(`/competition/${competitionId}/category/metadata/possible-values`, {withCredentials: true})
    },



    // Category game rules
    getCategoryGameRules(categoryId) {
        return Axios.get(`/category/game-rules/${categoryId}/`, {withCredentials: true})
    },
    addGameRules(competitionCategoryId, categoryGameRulesSpec) {
        return Axios.post(`/category/game-rules/${competitionCategoryId}`, categoryGameRulesSpec, {withCredentials: true})
    },
    updateGameRules(competitionCategoryId, gameRulesId, categoryGameRulesSpec) {
        return Axios.put(`/category/game-rules/${competitionCategoryId}/${gameRulesId}`, categoryGameRulesSpec, {withCredentials: true})
    }
}

export default CategoryService