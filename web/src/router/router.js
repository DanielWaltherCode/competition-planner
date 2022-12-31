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
import CreateNewPlayer from "@/components/player/CreateNewPlayer";
import Billing from "@/components/billing/Billing";
import RegisterPaymentDetails from "@/components/billing/RegisterPaymentDetails";
import Admin from "@/components/admin/admin.vue";
import NewClub from "@/components/admin/newClub.vue";
import CompetitionAdmin from "@/components/admin/competitionAdmin.vue";
import NewUser from "@/components/admin/newUser.vue";

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
                    },
                    {
                        path: "create",
                        component: CreateNewPlayer
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
                component: Schedule,
            },
            {
                path: "/schedule-advanced",
                component: ScheduleAdvanced,
            },
            {
                path: "/results",
                component: Result,
            },
            {
                path: "/billing",
                component: Billing
            },
            {
                path: "/payment-info",
                component: RegisterPaymentDetails
            },
            {
                path: "/admin",
                component: Admin,
                children: [
                    {
                        path: "/newClub",
                        component: NewClub
                    },
                    {
                        path: "/newUser",
                        component: NewUser
                    },
                    {
                        path: "/competitionAdmin",
                        component: CompetitionAdmin
                    },
                ]
            }
        ],
        scrollBehavior(to, from, savedPosition) {
            // always scroll to top
            return {top: 0}
        },
    }
)