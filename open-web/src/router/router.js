import Vue from "vue";
import Router from "vue-router"

Vue.use(Router)

export default new Router({
        mode: "history",
        routes: [
            {
                path: "/",
                name: "landing",
                component: () => import("@/components/Landing"),
            },
            {
                path: "/competition",
                name: "competition-landing",
                component: () => import("@/components/competition/CompetitionLanding"),
            },
            {
                path: "/competition/:competitionId",
                component: () => import("@/components/competition/Competition"),
                children: [
                    {
                        path: "/competition/:competitionId",
                        component: () => import("@/components/competition/CompetitionLanding"),
                    },
                    {
                        path: "/competition/:competitionId/categories",
                        component: () => import("@/components/competition/CompetitionCategories"),
                    },
                    {
                        path: "/competition/:competitionId/matches",
                        component: () => import("@/components/competition/CompetitionMatches"),
                    },
                    {
                        path: "/competition/:competitionId/players",
                        component: () => import("@/components/competition/CompetitionPlayers"),
                    },
                    {
                        path: "/competition/:competitionId/draw/:categoryId",
                        component: () => import("@/components/competition/CompetitionDraw"),
                    },
                ]
            },

        ],
        scrollBehavior() {
            return {x: 0, y: 0}
        }
    }
)