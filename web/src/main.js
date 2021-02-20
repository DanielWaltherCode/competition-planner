import Vue from 'vue'
import App from './App.vue'
import Axios from "axios";
import router from "./router/router"
import {i18n} from "./i18n"
import 'bootstrap'
import "bootstrap/dist/css/bootstrap.min.css";
import store from "@/store/store";

Vue.config.productionTip = false

Axios.defaults.baseURL = "http://www.localhost:9002"
Axios.defaults.headers.common = {'Authorization': store.getters.token}


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
  components: { App }
}).$mount('#app')
