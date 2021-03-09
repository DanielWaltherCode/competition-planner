import Vue from 'vue'
import App from './App.vue'
import Axios from "axios";
import router from "./router/router"
import {i18n} from "./i18n"
import 'bootstrap'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import store from "@/store/store";
import UserService from "@/common/api-services/user.service";

Vue.config.productionTip = false

Axios.defaults.baseURL = "http://www.localhost:9002"

// Add a request interceptor
Axios.interceptors.request.use(
    config => {
        if(config.withCredentials) {
            const token = store.getters.accessToken;
            if (token) {
                config.headers['Authorization'] = 'Bearer ' + token;
            }
        }
        config.headers["Content-Type"] = "application/json"
        return config
    },
    error => {
        Promise.reject(error)
    });

// Response interceptor
Axios.interceptors.response.use((response) => {
        return response
    },
    function (error) {
        const originalRequest = error.config
        const refreshToken = store.getters.refreshToken
        console.log(error)
        if (error.response.status === 401 && refreshToken != null ){
            // If previous request tried to refresh token but failed, abort here
           console.log("original request: " + originalRequest.url)
            if (originalRequest.url.includes("request-token") ) {
                store.commit("logout")
                router.push('/landing');
                return Promise.reject(error);
            }

            return UserService.refreshToken(refreshToken).then(res => {
                // 1) put token to LocalStorage
                console.log("Token refreshed, updating!")
                store.commit("auth_success", res.data)
                // 3) return originalRequest object with Axios.
                return Axios(originalRequest);
            })
        }
    })

console.log("Starting application")
console.log("Axios base url: " + Axios.defaults.baseURL)
console.log("Process base url: " + process.env.VUE_APP_BASE_URL)


new Vue({
    render: h => h(App),
    store,
    i18n,
    router,
    el: '#app',
    template: '<App/>',
    components: {App}
}).$mount('#app')
