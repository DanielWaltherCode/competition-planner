import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        isLoggedIn: false,
        token: "",
        user : {}
    },
    mutations: {
        auth_success(state, headers){
            state.isLoggedIn = true
            state.token = headers.authorization
        },
        logout(state){
            state.isLoggedIn = false
            state.user = {}
            state.token = ""
        },
        set_user(state, user) {
            state.user = user
        }
    },
    actions: {

    },
    getters : {
        isLoggedIn: state => !!state.isLoggedIn,
        user: state => state.user,
        token: state => state.token
    },
    plugins: [createPersistedState()]
})