import Axios from "axios";

const CategoryService = {
    getCategories() {
        return Axios.get("/category", {withCredentials: true})
    },
    createCompetitionCategory(competitionId, categoryId) {
        return Axios.post(`competition/${competitionId}/category/${categoryId}`, {}, {withCredentials: true})
    }
}

export default CategoryService