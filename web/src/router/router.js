import RegisteredPlayers from "@/components/player/RegisteredPlayers";
import AddPlayerToCompetition from "@/components/player/AddPlayerToCompetition";
import PlayerDetail from "@/components/player/PlayerDetail";
import PlayerComponent from "@/components/player/Player";
import CompetitionCategories from "@/components/category/Categories";
import LandingPage from "@/components/Landing";
import NewCompetitionOverview from "@/components/competition/NewCompetition";
import CompetitionOverview from "@/components/competition/CompetitionOverview";
import CompetitionDraw from "@/components/draw/Draw";
import CompetitionResult from "@/components/result/Result.vue"
import CompetitionSchedule from "@/components/schedule/Schedule";
import ScheduleAdvanced from "@/components/schedule/ScheduleAdvanced";
import PlayoffDraw from "@/components/draw/PlayoffDraw";
import CreateNewPlayer from "@/components/player/CreateNewPlayer";
import BillingPage from "@/components/billing/Billing";
import RegisterPaymentDetails from "@/components/billing/RegisterPaymentDetails";
import AdminComponent from "@/components/admin/admin.vue";
import NewClub from "@/components/admin/newClub.vue";
import CompetitionAdmin from "@/components/admin/competitionAdmin.vue";
import NewUser from "@/components/admin/newUser.vue";
import ExternalRegistration from "@/components/external-registration/ExternalRegistration.vue";
import {createRouter, createWebHistory} from "vue-router";
import ManualSchedule from "@/components/schedule/ManualSchedule.vue";

export default createRouter({
    history: createWebHistory(),
        routes: [
            {
                path: "/",
                component: LandingPage,
            },
            {
                path: "/landing",
                component: LandingPage,
            },
            {
                path: "/new-competition",
                component: NewCompetitionOverview,
            },
            {
                path: "/overview",
                component: CompetitionOverview,
            },
            {
                path: "/classes",
                name: "classes",
                component: CompetitionCategories
            },
            {
                path: "/players",
                name: "players",
                component: PlayerComponent,
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
                component: CompetitionDraw,
            },
            {
                path: "/playoffDraw",
                component: PlayoffDraw,
            },
            {
                path: "/schedule",
                component: CompetitionSchedule,
            },
            {
                path: "/schedule-advanced",
                component: ScheduleAdvanced,
            },
            {
                path: "/schedule-manual",
                component: ManualSchedule,
            },
            {
                path: "/results",
                component: CompetitionResult,
            },
            {
                path: "/billing",
                component: BillingPage
            },
            {
                path: "/payment-info",
                component: RegisterPaymentDetails
            },
            {
                path: "/admin",
                component: AdminComponent,
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
            },
            {
                path: "/externalRegistration",
                component: ExternalRegistration
            }
        ],
        scrollBehavior(to, from, savedPosition) {
            // always scroll to top
            return {top: 0}
        },
    }
)