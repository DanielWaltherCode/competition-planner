import Vue from 'vue'
import Vuex from 'vuex'
import createPersistedState from "vuex-persistedstate";

Vue.use(Vuex)

export default new Vuex.Store({
    state: {
        isLoggedIn: false,
        accessToken: null,
        refreshToken: null,
        user : {}
    },
    mutations: {
        auth_success(state, body){
            state.isLoggedIn = true
            state.accessToken = body["accessToken"]
            state.refreshToken = body["refreshToken"]
        },
        logout(state){
            state.isLoggedIn = false
            state.user = {}
            state.refreshToken = null
            state.accessToken = null
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
        accessToken: state => state.accessToken,
        refreshToken: state => state.refreshToken
    },
    plugins: [createPersistedState()]
})