import Vue from "vue";
import Router from "vue-router"
import RegisteredPlayers from "@/components/player/RegisteredPlayers";
import AddPlayerToCompetition from "@/components/player/AddPlayerToCompetition";
import PlayerDetail from "@/components/player/PlayerDetail";
import Player from "@/components/player/Player";
import Categories from "@/components/category/Categories";
import Landing from "@/components/Landing";
import NewCompetition from "@/components/competition/NewCompetition";
import CompetitionOverview from "@/components/competition/CompetitionOverview";
import Draw from "@/components/draw/Draw";
import Schedule from "@/components/schedule/Schedule";
import Result from "@/components/result/Result";
import ScheduleAdvanced from "@/components/schedule/ScheduleAdvanced";
import PlayoffDraw from "@/components/draw/PlayoffDraw";

Vue.use(Router)

export default new Router({
        mode: "history",
        routes: [
            {
                path: "/",
                component: Landing,
            },
            {
                path: "/landing",
                component: Landing,
            },
            {
                path: "/new-competition",
                component: NewCompetition,
            },
            {
                path: "/overview",
                component: CompetitionOverview,
            },
            {
                path: "/classes",
                name: "classes",
                component: Categories
            },
            {
                path: "/players",
                name: "players",
                component: Player,
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
                component: Draw,
            },
            {
                path: "/playoffDraw",
                component: PlayoffDraw,
            },
            {
                path: "/schedule",
                component:Schedule,
            },
            {
                path: "/schedule-advanced",
                component: ScheduleAdvanced,
            },
            {
                path: "/results",
                component: Result,
            }
        ],
        scrollBehavior() {
            return {x: 0, y: 0}
        }
    }
)