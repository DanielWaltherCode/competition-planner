import Vue from "vue";
import Router from "vue-router"
import RegisteredPlayers from "@/components/player/RegisteredPlayers";
import AddPlayerToCompetition from "@/components/player/AddPlayerToCompetition";
import PlayerDetail from "@/components/player/PlayerDetail";

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
                component: () => import("@/components/competition/NewCompetition"),
            },
            {
                path: "/overview",
                name: "overview",
                component: () => import("@/components/competition/CompetitionOverview"),
            },
            {
                path: "/classes",
                name: "classes",
                component: () => import("@/components/category/Categories")
            },
            {
                path: "/players",
                name: "players",
                component: () => import("@/components/player/Player"),
                children: [
                    {
                        path: "overview",
                        component: RegisteredPlayers
                    },
                    {
                        path: "add",
                        component: AddPlayerToCompetition
                    },
                    {
                        path: "detail/:id",
                        component: PlayerDetail
                    }

                ]
            },
            {
                path: "/draw",
                name: "draw",
                component: () => import("@/components/draw/Draw"),
            },
            {
                path: "/schedule",
                name: "schedule",
                component: () => import("@/components/schedule/Schedule"),
            },
            {
                path: "/schedule-advanced",
                name: "schedule-advanced",
                component: () => import("@/components/schedule/ScheduleAdvanced"),
            },
            {
                path: "/results",
                name: "results",
                component: () => import("@/components/result/Result"),
            }
        ],
        scrollBehavior() {
            return {x: 0, y: 0}
        }
    }
)