import Vue from "vue";
import Router from "vue-router"

Vue.use(Router)

export default new Router({
        mode: "history",
        routes: [
            {
                path: "/",
                name: "home",
                component: () => import("@/components/Landing"),
            },
            {
                path: "/landing",
                name: "landing",
                component: () => import("@/components/Landing"),
            },
            {
                path: "/new-competition",
                name: "newCompetition",
                component: () => import("@/components/NewCompetition"),
            },
            {
                path: "/overview",
                name: "overview",
                component: () => import("@/components/CompetitionOverview"),
            },
            {
                path: "/classes",
                name: "classes",
                component: () => import("@/components/category/Categories")
            },
            {
                path: "/players",
                name: "players",
                component: () => import("@/components/Player")
            },
            {
                path: "/draw",
                name: "draw",
                component: () => import("@/components/draw/Draw"),
            },
            {
                path: "/schedule",
                name: "schedule",
                component: () => import("@/components/Schedule"),
            }
        ],
        scrollBehavior() {
            return {x: 0, y: 0}
        }
    }
)