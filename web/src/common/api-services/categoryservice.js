import Axios from "axios";

const CategoryService = {
    getCategories() {
        return Axios.get("/category", {withCredentials: true})
    }
}

export default CategoryService