import Vue from "vue";
import Router from "vue-router"
import PlayerDetail from "../components/competition/PlayerDetail";

Vue.use(Router)

export default new Router({
        mode: "history",
        routes: [
            {
                path: "/",
                component: () => import("@/components/Landing"),
            },
            {
                path: "/landing",
                component: () => import("@/components/Landing"),
            },
            {
                path: "/competition/:competitionId",
                component: () => import("@/components/competition/Competition"),
                children: [
                    {
                        path: "landing",
                        component: () => import("@/components/competition/CompetitionLanding"),
                    },
                    {
                        path: "categories",
                        component: () => import("@/components/competition/CompetitionCategories"),
                    },
                    {
                        path: "matches",
                        component: () => import("@/components/competition/CompetitionMatches"),
                    },
                    {
                        path: "players",
                        component: () => import("@/components/competition/RegisteredPlayers"),
                    },
                    {
                        path: "draw/:categoryId",
                        component: () => import("@/components/competition/CompetitionDraw"),
                    },
                    {
                        path: "player/:playerId/detail",
                        component: PlayerDetail
                    }
                ]
            },

        ],
        scrollBehavior() {
            return {x: 0, y: 0}
        }
    }
)