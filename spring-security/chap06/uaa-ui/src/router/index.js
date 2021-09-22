import Vue from "vue";
import VueRouter from "vue-router";
import Home from "../views/Home.vue";
import HomeContent from "../views/HomeContent.vue";
import guards from "./guards";

Vue.use(VueRouter);

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import(/* webpackChunkName: "login" */ "../views/auth/Login.vue"),
  },
  {
    path: "/mfa",
    name: "mfa",
    beforeEnter: guards.guardMfa,
    component: () => import(/* webpackChunkName: "mfa" */ "../views/auth/Mfa.vue"),
  },
  {
    path: "/register",
    name: "Register",
    component: () => import(/* webpackChunkName: "register" */ "../views/auth/Register.vue"),
  },
  {
    path: "/forbidden",
    component: () => import(/* webpackChunkName: "forbidden" */ "../views/Forbidden.vue"),
  },
  {
    path: "/",
    component: Home,
    children: [
      {
        path: "",
        component: HomeContent,
        meta: {
          breadcrumb: [{ to: "/", label: "首页" }],
        },
        children: [
          {
            path: "",
            redirect: "/users",
          },
          {
            path: "users",
            component: () => import(/* webpackChunkName: "users" */ "../views/users/Users.vue"),
            meta: {
              breadcrumb: [
                { to: "/", label: "首页" },
                { to: "/users", label: "用户管理" },
              ],
            },
          },
        ],
      },
      {
        path: "about",
        name: "About",
        // 路由懒加载，提供路由级别的代码分割
        // 为这个路由生成一个单独的 chunk (about.[hash].js)
        component: () => import(/* webpackChunkName: "about" */ "../views/About.vue"),
        meta: {
          breadcrumb: [
            { to: "/", label: "首页" },
            { to: "/about", label: "关于" },
          ],
        },
      },
    ],
    meta: {
      requiresAuth: true,
      requiredPermissions: ["ROLE_STAFF"],
      breadcrumb: ["首页"],
    },
  },
];

const router = new VueRouter({
  routes,
});

// 全局守卫
router.beforeEach(guards.globalGuard);
export default router;
