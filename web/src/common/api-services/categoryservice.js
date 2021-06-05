import Axios from "axios";

const CategoryService = {
    getCategories() {
        return Axios.get("/category", {withCredentials: true})
    },
    createCompetitionCategory(competitionId, categoryId) {
        return Axios.post(`competition/${competitionId}/category/${categoryId}`, {}, {withCredentials: true})
    },
    addMetaData(competitionId, competitionCategoryId, categoryMetaDataSpec) {
        return Axios.post(`/competition/${competitionId}/category/metadata/${competitionCategoryId}`, categoryMetaDataSpec, {withCredentials: true})
    },
    addGameRules(competitionCategoryId, categoryGameRulesSpec) {
        return Axios.post(`/category/game-rules/${competitionCategoryId}`, categoryGameRulesSpec, {withCredentials: true})
    },
    getPossibleMetaDataValues(competitionId) {
        return Axios.get(`/competition/${competitionId}/category/metadata/possible-values`, {withCredentials: true})
    }
}

export default CategoryService