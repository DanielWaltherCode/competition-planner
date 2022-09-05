import Vue from 'vue'
import App from './App.vue'
import Axios from "axios";
import router from "./router/router"
import {i18n} from "./i18n"
import 'bootstrap'
import "@/assets/css//bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import "@/assets/css/style.css"
import AOS from "aos";
import "aos/dist/aos.css";
import store from "@/store/store";
import 'vue2-timepicker/dist/VueTimepicker.css'
import '@fortawesome/fontawesome-free/css/all.min.css'
import Toasted from 'vue-toasted';
import {vfmPlugin} from 'vue-final-modal'

Vue.use(vfmPlugin)
import VModal from 'vue-js-modal'
import UserService from "@/common/api-services/user.service";

Vue.use(VModal)
Vue.use(Toasted)

Vue.config.productionTip = false

App.AOS = new AOS.init({
    disable: "phone",
    duration: 1000,
    easing: "ease-in-out",
    once: true,
    mirror: false
});

Axios.defaults.baseURL = process.env.VUE_APP_BASE_URL

// Add a request interceptor
Axios.interceptors.request.use(
    config => {
        if (config.withCredentials) {
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

Axios.interceptors.response.use(
    response => {
        return response
    },
    error => {
        console.log(error.response)
        const currentRequest = error.config
        const refreshToken = store.getters.refreshToken
        if (error.response.status === 401) {
            if (refreshToken != null) {
                // If previous request tried to refresh token but failed, abort here
                console.log("original request: " + currentRequest.url)
                if (currentRequest.url.includes("request-token")) {
                    logout(error)
                }
                return UserService.refreshToken(refreshToken).then(res => {
                    // 1) put token to LocalStorage
                    console.log("Token refreshed, updating!")
                    store.commit("auth_success", res.data)
                    // 3) return originalRequest object with Axios.
                    return Axios(currentRequest);
                }).catch(() => {
                    // Failed to refresh token
                    console.log("Couldn't refresh token")
                    logout(error)
                })
            }
            // If there is no refresh token
            else {
                logout(error)
            }
        }
        throw new ResponseException(error)
    })

function ResponseException(error) {
    this.status = error.status
    this.data = error.response.data
}

function logout(error) {
    store.commit("logout")
    router.push('/landing');
    return Promise.reject(error);
}

new Vue({
    render: h => h(App),
    store,
    i18n,
    router,
    el: '#app',
    template: '<App/>',
    components: {App}
}).$mount('#app')
