import Vue from 'vue'
import App from './App.vue'
import Axios from "axios";
import router from "./router/router"
import {i18n} from "./i18n"
import 'bootstrap'
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap-icons/font/bootstrap-icons.css"
import '@fortawesome/fontawesome-free/css/all.min.css'
import 'vue2-timepicker/dist/VueTimepicker.css'




Vue.config.productionTip = false


Axios.defaults.baseURL = process.env.VUE_APP_BASE_URL

new Vue({
  render: h => h(App),
  i18n,
  router,
  el: '#app',
  template: '<App/>',
  components: {App}
}).$mount('#app')
