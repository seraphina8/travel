import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/login",
    name: "Login",
    component: () => import("../views/Login.vue"),
  },
  {
    path: "/",
    component: () => import("../layouts/AdminLayout.vue"),
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "Dashboard",
        component: () => import("../views/Dashboard.vue"),
        meta: { title: "控制台" },
      },
      {
        path: "user",
        name: "UserManage",
        component: () => import("../views/UserManage.vue"),
        meta: { title: "用户管理" },
      },
      {
        path: "scenic",
        name: "ScenicManage",
        component: () => import("../views/ScenicManage.vue"),
        meta: { title: "景区管理" },
      },
      {
        path: "attraction",
        name: "AttractionManage",
        component: () => import("../views/AttractionManage.vue"),
        meta: { title: "景点管理" },
      },
      {
        path: "admin",
        name: "AdminManage",
        component: () => import("../views/AdminManage.vue"),
        meta: { title: "管理员管理" },
      },
      {
        path: "order",
        name: "OrderManage",
        component: () => import("../views/OrderManage.vue"),
        meta: { title: "订单管理" },
      },
      {
        path: "ticket",
        name: "TicketManage",
        component: () => import("../views/TicketManage.vue"),
        meta: { title: "门票管理" },
      },
      {
        path: "strategy",
        name: "StrategyManage",
        component: () => import("../views/StrategyManage.vue"),
        meta: { title: "攻略管理" },
      },
      {
        path: "feedback",
        name: "FeedbackManage",
        component: () => import("../views/FeedbackManage.vue"),
        meta: { title: "反馈管理" },
      },
      {
        path: "banner",
        name: "BannerManage",
        component: () => import("../views/BannerManage.vue"),
        meta: { title: "轮播图管理" },
      },
      {
        path: "statistics",
        name: "Statistics",
        component: () => import("../views/Statistics.vue"),
        meta: { title: "数据统计" },
      },
      {
        path: "food",
        name: "FoodManage",
        component: () => import("../views/FoodManage.vue"),
        meta: { title: "美食管理" },
      },
      {
        path: "tag",
        name: "TagManage",
        component: () => import("../views/TagManage.vue"),
        meta: { title: "标签管理" },
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem("token");

  if (to.path === "/login" && token) {
    next("/dashboard");
    return;
  }

  if (to.path !== "/login" && !token) {
    next("/login");
    return;
  }

  next();
});
export default router;
