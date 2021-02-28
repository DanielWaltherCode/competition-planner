import Vue from "vue";
import Router from "vue-router"

Vue.use(Router)

export default new Router({
    mode: "history",
    routes: [
        {
            path: "/landing",
            name: "landing",
            component: () => import("@/components/Landing"),
        },
        {
            path: "/overview",
            name: "overview",
            component: () => import("@/components/Overview"),
        },
        {
            path: "/classes",
            name: "classes",
            component: () => import("@/components/Categories")
        }
    ],
    scrollBehavior() {
        return {x: 0, y: 0}
    }
    }
)