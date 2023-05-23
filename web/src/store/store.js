import Vue from 'vue'
import createPersistedState from "vuex-persistedstate";
import {createStore} from "vuex";


const store = createStore({
    state: {
        isLoggedIn: false,
        accessToken: null,
        refreshToken: null,
        user : {},
        competition: null,
    },
    mutations: {
        auth_success(state, body){
            state.isLoggedIn = true
            state.accessToken = body["accessToken"]
            if (body["refreshToken"] !== '') {
                state.refreshToken = body["refreshToken"]
            }
        },
        logout(state){
            state.isLoggedIn = false
            state.user = {}
            state.refreshToken = null
            state.accessToken = null
            state.competition = null
        },
        set_user(state, user) {
            state.user = user
        },
        set_competition(state, competition) {
            state.competition = competition
        }
    },
    actions: {

    },
    getters : {
        isLoggedIn: state => state.isLoggedIn,
        user: state => state.user,
        accessToken: state => state.accessToken,
        refreshToken: state => state.refreshToken,
        competition: state => state.competition,
        isAdmin: state => state.user.role === "ROLE_ADMIN"
    },
    plugins: [createPersistedState()]
})

export default store;