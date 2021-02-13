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
    ],
    scrollBehavior() {
        return {x: 0, y: 0}
    }
    }
)